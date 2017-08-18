/*
 * ${license}
 */
package org.teiid.resource.adapter.geode;

import javax.resource.ResourceException;
import javax.resource.spi.InvalidPropertyException;

import org.teiid.resource.spi.BasicConnectionFactory;
import org.teiid.resource.spi.BasicManagedConnectionFactory;

import org.teiid.core.BundleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/****
 * This class also defines configuration variables, like user, password, URL etc to connect to
 * the EIS system. Define an attribute for each configuration variable, and then provide both
 * "getter" and "setter" methods for them. Note to use only "java.lang" objects as the attributes,
 * DO NOT use Java primitives for defining and accessing the properties.
 */
public class GeodeManagedConnectionFactory extends BasicManagedConnectionFactory {

  public static final BundleUtil
      UTIL =
      BundleUtil.getBundleUtil(GeodeManagedConnectionFactory.class);


  private List<Pair<String, Integer>> locatorAddrPortPairList = null;

  @Override
  public BasicConnectionFactory<GeodeConnectionImpl> createConnectionFactory()
      throws ResourceException {

    if (locatorListProperty == null) {
      throw new InvalidPropertyException(UTIL.getString("NullLocatorProperty"));
    }

    validateLocatorProperty();

    return new BasicConnectionFactory<GeodeConnectionImpl>() {
      @Override
      public GeodeConnectionImpl getConnection() throws ResourceException {
        return new GeodeConnectionImpl(GeodeManagedConnectionFactory.this);
      }
    };
  }

  private void validateLocatorProperty() throws ResourceException {
    final String locators = this.getLocatorListProperty();
    this.locatorAddrPortPairList = new ArrayList<>();
    String[] locatorSpecs = locators.split(",");
    for (String locatorSpec : locatorSpecs) {
      String[] addrPort = locatorSpec.split(Pattern.quote("["));
      if (addrPort.length != 2) {
        throw new InvalidPropertyException(UTIL.getString("LocatorPropertyFormat"));
      } else if (addrPort[1].charAt(addrPort[1].length() - 1) != ']') {
        throw new InvalidPropertyException(UTIL.getString("LocatorPropertyFormat"));
      } else if (addrPort[1].length() < 2) {
        throw new InvalidPropertyException(UTIL.getString("LocatorPropertyFormat"));
      }
      String addr = addrPort[0];
      Integer port = Integer.parseInt(addrPort[1].substring(0, addrPort[1].length() - 1));
      this.locatorAddrPortPairList.add(new Pair<>(addr, port));
    }
  }

  // ra.xml files getters and setters go here.
  private String locatorListProperty = null;
  public String getLocatorListProperty() {
    return locatorListProperty;
  }
  public void setLocatorListProperty(String locatorListProperty) {
    this.locatorListProperty = locatorListProperty;
  }

  public List<Pair<String, Integer>> getLocatorAddrPortPairList() {
    return this.locatorAddrPortPairList;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime
        * result
        + ((locatorListProperty == null) ? 0 : locatorListProperty.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    GeodeManagedConnectionFactory other = (GeodeManagedConnectionFactory) obj;
    if (!checkEquals(this.getLocatorListProperty(), other.getLocatorListProperty())) {
      return false;
    }

    return true;

  }

}
