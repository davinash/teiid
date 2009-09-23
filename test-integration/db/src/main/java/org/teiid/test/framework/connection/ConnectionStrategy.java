/*
 * Copyright (c) 2000-2007 MetaMatrix, Inc.
 * All rights reserved.
 */
package org.teiid.test.framework.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.sql.XAConnection;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminOptions;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import org.teiid.test.framework.datasource.DataSource;
import org.teiid.test.framework.datasource.DataSourceMgr;
import org.teiid.test.framework.exception.QueryTestFailedException;
import org.teiid.test.framework.exception.TransactionRuntimeException;

import com.metamatrix.jdbc.api.ExecutionProperties;



public abstract class ConnectionStrategy {
    
     public static final String JNDINAME_USERTXN = "usertxn-jndiname"; //$NON-NLS-1$  
	
	public static final String PROCESS_BATCH = "process-batch"; //$NON-NLS-1$
	public static final String CONNECTOR_BATCH = "connector-batch"; //$NON-NLS-1$

    public static final String AUTOCOMMIT = "autocommit"; //$NON-NLS-1$
    
    public static final String TXN_AUTO_WRAP = ExecutionProperties.PROP_TXN_AUTO_WRAP;
    
    public static final String FETCH_SIZE = ExecutionProperties.PROP_FETCH_SIZE;
    
    public static final String EXEC_IN_BATCH = "execute.in.batch"; //$NON-NLS-1$
    
    
    private Map<String, DataSource> datasources = null;

    
    public ConnectionStrategy(Properties props) throws QueryTestFailedException {
    	this.env = props;
   	
    }
    
    /*
     * Lifecycle methods for managing the  connection
     */
    
    /**
     * Returns a connection
     * @return Connection
     */
    public abstract Connection getConnection() throws QueryTestFailedException;
    
    public Connection getAdminConnection() throws QueryTestFailedException{
    	return null;
    }
    
    private boolean autoCommit;
    public boolean getAutocommit() {
    	return autoCommit;
    }

    public abstract void shutdown();
    
    public XAConnection getXAConnection() throws QueryTestFailedException {
        return null;
    }
    
    
    private Properties env = null;
    
    
    public Properties getEnvironment() {
    	return env;
    }
    
    public Map<String, DataSource> getDataSources() {
    	return this.datasources;
    }
    
    class CloseInterceptor implements InvocationHandler {

        Connection conn;

        CloseInterceptor(Connection conn) {
            this.conn = conn;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("close")) { //$NON-NLS-1$
                return null;
            }
            try {
                return method.invoke(this.conn, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }    
    
   
    void configure() throws QueryTestFailedException  {
    	
    	datasources = new HashMap<String, DataSource>(3);
    	
    	String ac = this.env.getProperty(AUTOCOMMIT, "true");
    	this.autoCommit = Boolean.getBoolean(ac);
    	
        com.metamatrix.jdbc.api.Connection c =null;
        try {
        	
        	// the the driver strategy is going to be used to connection directly to the connector binding
        	// source, then no administration can be done
        	java.sql.Connection conn = getConnection();
        	if ( conn instanceof com.metamatrix.jdbc.api.Connection) {
        		c = (com.metamatrix.jdbc.api.Connection) conn;
        	} else {
        		return;
        	}
            
            Admin admin = (Admin)c.getAdminAPI();
        
//            Properties p = new Properties();
//            if (this.env.getProperty(PROCESS_BATCH) != null) {
//                p.setProperty("metamatrix.buffer.processorBatchSize", this.env.getProperty(PROCESS_BATCH)); //$NON-NLS-1$
//            }
//            
//            if (this.env.getProperty(CONNECTOR_BATCH) != null) {
//                p.setProperty("metamatrix.buffer.connectorBatchSize", this.env.getProperty(CONNECTOR_BATCH)); //$NON-NLS-1$
//            }
            
            setupVDBConnectorBindings(admin);
            
            admin.restart();
 
            System.out.println("Bouncing the system..(wait 15 seconds)"); //$NON-NLS-1$
            Thread.sleep(1000*15);
        //    Thread.sleep(1000*60);
            System.out.println("done."); //$NON-NLS-1$

        } catch (Exception e) {
        	e.printStackTrace();

            throw new TransactionRuntimeException(e);
        }  finally {
        	// need to close and flush the connection after restarting
        	this.shutdown();
           	
        }
    }    
    
    protected void setupVDBConnectorBindings(Admin api) throws QueryTestFailedException {
         
    	try {

    		Collection<VDB> vdbs = api.getVDBs("*");
    		if (vdbs == null) {
    	  		throw new QueryTestFailedException("GetVDBS returned no vdbs available");
    	  		 
    		} else if (vdbs.size() != 1) {
    			throw new QueryTestFailedException("GetVDBS returned more than 1 vdb available");
    		}
    		VDB vdb = (VDB) vdbs.iterator().next();
    		Iterator<Model> modelIt = vdb.getModels().iterator();
    		while (modelIt.hasNext() ) {
    			Model m = modelIt.next();
    			
    			if (!m.isPhysical()) continue;
    			
    			// get the mapping, if defined
    			String mappedName = this.env.getProperty(m.getName());
    			
	        	String useName = m.getName();
	        	if(mappedName != null) {
	        		useName = mappedName;
	        	}

	        	org.teiid.test.framework.datasource.DataSource ds = DataSourceMgr.getInstance().getDatasource(useName, m.getName());
	        	
	        	if (ds != null) {
		        	datasources.put(m.getName(), ds);

	                System.out.println("Set up Connector Binding (model:mapping:type): " + m.getName() + ":" + useName + ":"  + ds.getConnectorType()); //$NON-NLS-1$

		        	AdminOptions ao = new AdminOptions(AdminOptions.OnConflict.OVERWRITE);
		        	ao.addOption(AdminOptions.BINDINGS_IGNORE_DECRYPT_ERROR);
		        	
		        	api.addConnectorBinding(ds.getName(), ds.getConnectorType(), ds.getProperties(), ao);
		        	
		        	api.assignBindingToModel(ds.getName(), vdb.getName(), vdb.getVDBVersion(), m.getName());
		        	
	        	} else {
	        		throw new QueryTestFailedException("Error: Unable to create binding to map to model : " + m.getName() + ", the mapped name " + useName + " had no datasource properties defined");
	        	}

    		}
    		
    	} catch (QueryTestFailedException qt) {
    		throw qt;
    	} catch (Exception t) {
    		t.printStackTrace();
    		throw new QueryTestFailedException(t);
    	}

    	
    }
        
 
   
}
