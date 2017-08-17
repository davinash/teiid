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

public class GeodeManagedConnectionFactory extends BasicManagedConnectionFactory {

  public static final BundleUtil
      UTIL =
      BundleUtil.getBundleUtil(GeodeManagedConnectionFactory.class);

  private String sampleProperty = null;
  private String locatorList;
  private List<Pair<String, Integer>> locatorAddrPortPairList;

  @Override
  public BasicConnectionFactory<GeodeConnectionImpl> createConnectionFactory()
      throws ResourceException {

    if (sampleProperty == null) {
      throw new InvalidPropertyException(
          UTIL.getString("GeodeManagedConnectionFactory.sampleproperty_not_set")); //$NON-NLS-1$
    }

    if ( locatorList == null) {
      throw new InvalidPropertyException(UTIL.getString("GeodeManagedConnectionFactory.locatorList is not set"));
    }

    validateLocatorProperty();

    return new BasicConnectionFactory<GeodeConnectionImpl>() {
      @Override
      public GeodeConnectionImpl getConnection() throws ResourceException {
        return new GeodeConnectionImpl(GeodeManagedConnectionFactory.this);
      }
    };
  }

  private void validateLocatorProperty()  throws ResourceException {
    final String locators = this.getLocatorList();
    this.locatorAddrPortPairList = new ArrayList<>();
    String[] locatorSpecs = locators.split(",");
    for (String locatorSpec : locatorSpecs) {
      String[] addrPort = locatorSpec.split(Pattern.quote("["));
      if (addrPort.length != 2) {
        throw new InvalidPropertyException(
            "Allowed format for locators is: \"addr[port], addr[port]\"");
      } else if (addrPort[1].charAt(addrPort[1].length() - 1) != ']') {
        throw new InvalidPropertyException(
            "Allowed format for locators is: \"addr[port], addr[port]\"");
      } else if (addrPort[1].length() < 2) {
        throw new InvalidPropertyException(
            "Allowed format for locators is: \"addr[port], addr[port]\"");
      }
      String addr = addrPort[0];
      Integer port = Integer.parseInt(addrPort[1].substring(0, addrPort[1].length() - 1));
      this.locatorAddrPortPairList.add(new Pair<>(addr, port));
    }
  }

  // ra.xml files getters and setters go here.
  public String getSampleProperty() {
    return sampleProperty;
  }

  public void setSampleProperty(String property) {
    this.sampleProperty = property;
  }

  public String getLocatorList() { return locatorList;}

  public void setLocatorList(String locatorList) {
    this.locatorList = locatorList;
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
        + ((sampleProperty == null) ? 0 : sampleProperty.hashCode())
        + ((locatorList == null) ? 0 : locatorList.hashCode());
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

    if (!checkEquals(this.getSampleProperty(), other.getSampleProperty())) {
      return false;
    }
    if (!checkEquals(this.getLocatorList(), other.getLocatorList())) {
      return false;
    }

    return true;

  }

}
