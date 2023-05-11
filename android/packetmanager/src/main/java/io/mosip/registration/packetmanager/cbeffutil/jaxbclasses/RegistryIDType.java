package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;


import org.simpleframework.xml.Element;

public class RegistryIDType {

	@Element(name = "Organization", required = false)
	protected String organization;
	@Element(name = "Type", required = false)
	protected String type;

	/**
	 * Gets the value of the organization property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Sets the value of the organization property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setOrganization(String value) {
		this.organization = value;
	}

	/**
	 * Gets the value of the type property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setType(String value) {
		this.type = value;
	}
}
