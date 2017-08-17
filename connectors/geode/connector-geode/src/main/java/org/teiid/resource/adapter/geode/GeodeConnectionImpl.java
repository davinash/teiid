/*
 * ${license}
 */
package org.teiid.resource.adapter.geode;


import javax.resource.ResourceException;

import org.teiid.resource.spi.BasicConnection;
import org.teiid.logging.LogManager;
import org.teiid.core.BundleUtil;
import org.teiid.logging.LogConstants;
import org.teiid.translator.geode.GeodeConnection;

/**
 * Connection to the resource. You must define geodeConnection interface, that 
 * extends the "javax.resource.cci.Connection".  If a custom translator is also being created,
 * the Connection interface should be defined in that project or a common project.
 */
public class GeodeConnectionImpl extends BasicConnection implements GeodeConnection {

	public static final BundleUtil UTIL = BundleUtil.getBundleUtil(GeodeConnectionImpl.class);


    private GeodeManagedConnectionFactory config;

    public GeodeConnectionImpl(GeodeManagedConnectionFactory env) {
        this.config = env;
        // todo: connect to your source here
        
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, "geode Connection has been created."); //$NON-NLS-1$

    }
    
    @Override
    public void close() {
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, "geode Connection has been closed."); //$NON-NLS-1$
    	
    }

    @Override
    public void someMethod() throws ResourceException {

    }
}
