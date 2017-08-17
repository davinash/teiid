/*
 * ${license}
 */
package org.teiid.resource.adapter.geode;

import javax.resource.ResourceException;
import javax.resource.spi.InvalidPropertyException;

import org.teiid.resource.spi.BasicConnectionFactory;
import org.teiid.resource.spi.BasicManagedConnectionFactory;

import org.teiid.core.BundleUtil;

public class geodeManagedConnectionFactory extends BasicManagedConnectionFactory {

	public static final BundleUtil UTIL = BundleUtil.getBundleUtil(geodeManagedConnectionFactory.class);

	private String sampleProperty = null;
	
	@Override
	public BasicConnectionFactory<geodeConnectionImpl> createConnectionFactory() throws ResourceException {

		if (sampleProperty == null) {
	 		throw new InvalidPropertyException(UTIL.getString("geodeManagedConnectionFactory.sampleproperty_not_set")); //$NON-NLS-1$
		}
		
		return new BasicConnectionFactory<geodeConnectionImpl>() {
			@Override
			public geodeConnectionImpl getConnection() throws ResourceException {
				return new geodeConnectionImpl(geodeManagedConnectionFactory.this);
			}
		};
	}	
	
	// ra.xml files getters and setters go here.
	public String getSampleProperty() {
		return sampleProperty;
	}
	
	public void setSampleProperty(String property) {
		this.sampleProperty = property;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((sampleProperty == null) ? 0 : sampleProperty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
			
		geodeManagedConnectionFactory other = (geodeManagedConnectionFactory) obj;

		if (!checkEquals(this.getSampleProperty(), other.getSampleProperty())) {
			return false;
		}

		return true;

	}

}
