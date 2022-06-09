package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class QualityType {

	@Element(name = "Algorithm")
	protected RegistryIDType algorithm;
	@Element(name = "Score")
	protected Long score;
	@Element(name = "QualityCalculationFailed", required = false)
	protected String qualityCalculationFailed;

	/**
	 * Gets the value of the algorithm property.
	 * 
	 * @return possible object is {@link RegistryIDType }
	 * 
	 */
	public RegistryIDType getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the value of the algorithm property.
	 * 
	 * @param value allowed object is {@link RegistryIDType }
	 * 
	 */
	public void setAlgorithm(RegistryIDType value) {
		this.algorithm = value;
	}

	/**
	 * Gets the value of the score property.
	 * 
	 * @return possible object is {@link Long }
	 * 
	 */
	public Long getScore() {
		return score;
	}

	/**
	 * Sets the value of the score property.
	 * 
	 * @param value allowed object is {@link Long }
	 * 
	 */
	public void setScore(Long value) {
		this.score = value;
	}

	/**
	 * Gets the value of the qualityCalculationFailed property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getQualityCalculationFailed() {
		return qualityCalculationFailed;
	}

	/**
	 * Sets the value of the qualityCalculationFailed property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setQualityCalculationFailed(String value) {
		this.qualityCalculationFailed = value;
	}

}
