package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.time.LocalDateTime;


@Root
public class BIRInfoType {

	@Element(name = "Creator")
	protected String creator;
	@Element(name = "Index")
	protected String index;
	@Element(name = "Payload")
	protected byte[] payload;
	@Element(name = "Integrity")
	protected boolean integrity;
	@Element(name = "CreationDate")
	protected LocalDateTime creationDate;
	@Element(name = "NotValidBefore")
	protected LocalDateTime notValidBefore;
	@Element(name = "NotValidAfter")
	protected LocalDateTime notValidAfter;

	/**
	 * Gets the value of the creator property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Sets the value of the creator property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setCreator(String value) {
		this.creator = value;
	}

	/**
	 * Gets the value of the index property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * Sets the value of the index property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setIndex(String value) {
		this.index = value;
	}

	/**
	 * Gets the value of the payload property.
	 * 
	 * @return possible object is byte[]
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Sets the value of the payload property.
	 * 
	 * @param value allowed object is byte[]
	 */
	public void setPayload(byte[] value) {
		this.payload = value;
	}

	/**
	 * Gets the value of the integrity property.
	 * 
	 */
	public boolean isIntegrity() {
		return integrity;
	}

	/**
	 * Sets the value of the integrity property.
	 * 
	 */
	public void setIntegrity(boolean value) {
		this.integrity = value;
	}

	/**
	 * Gets the value of the creationDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the value of the creationDate property.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setCreationDate(LocalDateTime value) {
		this.creationDate = value;
	}

	/**
	 * Gets the value of the notValidBefore property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public LocalDateTime getNotValidBefore() {
		return notValidBefore;
	}

	/**
	 * Sets the value of the notValidBefore property.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setNotValidBefore(LocalDateTime value) {
		this.notValidBefore = value;
	}

	/**
	 * Gets the value of the notValidAfter property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public LocalDateTime getNotValidAfter() {
		return notValidAfter;
	}

	/**
	 * Sets the value of the notValidAfter property.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setNotValidAfter(LocalDateTime value) {
		this.notValidAfter = value;
	}

}
