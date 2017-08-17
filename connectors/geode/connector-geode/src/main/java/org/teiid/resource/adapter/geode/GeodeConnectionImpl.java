/*
 * ${license}
 */
package org.teiid.resource.adapter.geode;


import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.teiid.core.BundleUtil;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import org.teiid.resource.spi.BasicConnection;
import org.teiid.translator.geode.GeodeConnection;

import java.util.Properties;
import javax.resource.ResourceException;

/**
 * Connection to the resource. You must define geodeConnection interface, that
 * extends the "javax.resource.cci.Connection".  If a custom translator is also being created,
 * the Connection interface should be defined in that project or a common project.
 */
public class GeodeConnectionImpl extends BasicConnection implements GeodeConnection {

  public static final BundleUtil UTIL = BundleUtil.getBundleUtil(GeodeConnectionImpl.class);
  GemFireCacheImpl geodeClientCache = null;


  private GeodeManagedConnectionFactory config;

  public GeodeConnectionImpl(GeodeManagedConnectionFactory env) {
    this.config = env;
    // todo: connect to your source here
    final Properties cacheProperties = new Properties();
    ClientCacheFactory ccf = new ClientCacheFactory(cacheProperties);
    this.config.getLocatorAddrPortPairList().forEach(P-> ccf.addPoolLocator(P.getFirst(), P.getSecond()));
    ccf.setPdxReadSerialized(true);

    this.geodeClientCache = (GemFireCacheImpl) ccf.create();

    LogManager
        .logDetail(LogConstants.CTX_CONNECTOR, "geode Connection has been created."); //$NON-NLS-1$

  }


  @Override
  public void close() {
    if ( this.geodeClientCache != null) {
      this.geodeClientCache.close();
    }
    LogManager
        .logDetail(LogConstants.CTX_CONNECTOR, "geode Connection has been closed."); //$NON-NLS-1$
  }

  @Override
  public void someMethod() throws ResourceException {

  }
}
