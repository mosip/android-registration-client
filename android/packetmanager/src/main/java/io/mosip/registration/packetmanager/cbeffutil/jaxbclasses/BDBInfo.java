package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.time.LocalDateTime;
import java.util.List;

public class BDBInfo {

	@Element(name = "ChallengeResponse", required = false)
	private byte[] challengeResponse;
	@Element(name = "Index", required = false)
	private String index;
	@Element(name = "Format", required = false)
	private RegistryIDType format;
	@Element(name = "Encryption", required = false)
	private Boolean encryption;
	@Element(name = "CreationDate", required = false)
	private LocalDateTime creationDate;
	@Element(name = "NotValidBefore", required = false)
	private LocalDateTime notValidBefore;
	@Element(name = "NotValidAfter", required = false)
	private LocalDateTime notValidAfter;
	@Element(name = "Type", required = false)
	private BiometricType type;

	@Element(name = "Subtype", required = false)
	private String subtype;
	@Element(name = "Level", required = false)
	private ProcessedLevelType level;
	@Element(name = "Product", required = false)
	private RegistryIDType product;
	@Element(name = "CaptureDevice", required = false)
	private RegistryIDType captureDevice;
	@Element(name = "FeatureExtractionAlgorithm", required = false)
	private RegistryIDType featureExtractionAlgorithm;
	@Element(name = "ComparisonAlgorithm", required = false)
	private RegistryIDType comparisonAlgorithm;
	@Element(name = "CompressionAlgorithm", required = false)
	private RegistryIDType compressionAlgorithm;
	@Element(name = "Purpose", required = false)
	private PurposeType purpose;
	@Element(name = "Quality", required = false)
	private QualityType quality;





	public BDBInfo(BDBInfoBuilder bDBInfoBuilder) {
		this.challengeResponse = bDBInfoBuilder.challengeResponse;
		this.index = bDBInfoBuilder.index;
		this.format = bDBInfoBuilder.format;
		this.encryption = bDBInfoBuilder.encryption;
		this.creationDate = bDBInfoBuilder.creationDate;
		this.notValidBefore = bDBInfoBuilder.notValidBefore;
		this.notValidAfter = bDBInfoBuilder.notValidAfter;
		this.type = bDBInfoBuilder.type;
		this.subtype = bDBInfoBuilder.subtype;
		this.level = bDBInfoBuilder.level;
		this.product = bDBInfoBuilder.product;
		this.purpose = bDBInfoBuilder.purpose;
		this.quality = bDBInfoBuilder.quality;
		this.captureDevice = bDBInfoBuilder.captureDevice;
		this.featureExtractionAlgorithm = bDBInfoBuilder.featureExtractionAlgorithm;
		this.comparisonAlgorithm = bDBInfoBuilder.comparisonAlgorithm;
		this.compressionAlgorithm = bDBInfoBuilder.compressionAlgorithm;
	}


	public static class BDBInfoBuilder {
		private byte[] challengeResponse;
		private String index;
		private RegistryIDType format;
		private Boolean encryption;
		private LocalDateTime creationDate;
		private LocalDateTime notValidBefore;
		private LocalDateTime notValidAfter;
		private BiometricType type;
		private String subtype;
		private ProcessedLevelType level;
		private RegistryIDType product;
		private PurposeType purpose;
		private QualityType quality;
		private RegistryIDType captureDevice;
		private RegistryIDType featureExtractionAlgorithm;
		private RegistryIDType comparisonAlgorithm;
		private RegistryIDType compressionAlgorithm;

		public BDBInfoBuilder withChallengeResponse(byte[] challengeResponse) {
			this.challengeResponse = challengeResponse;
			return this;
		}

		public BDBInfoBuilder withIndex(String index) {
			this.index = index;
			return this;
		}

		public BDBInfoBuilder withFormat(RegistryIDType format) {
			this.format = format;
			return this;
		}

		public BDBInfoBuilder withEncryption(Boolean encryption) {
			this.encryption = encryption;
			return this;
		}

		public BDBInfoBuilder withCreationDate(LocalDateTime creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public BDBInfoBuilder withNotValidBefore(LocalDateTime notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}

		public BDBInfoBuilder withNotValidAfter(LocalDateTime notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}

		public BDBInfoBuilder withType(BiometricType type) {
			this.type = type;
			return this;
		}

		public BDBInfoBuilder withSubtype(String subtype) {
			this.subtype = subtype;
			return this;
		}

		public BDBInfoBuilder withLevel(ProcessedLevelType level) {
			this.level = level;
			return this;
		}

		public BDBInfoBuilder withProduct(RegistryIDType product) {
			this.product = product;
			return this;
		}

		public BDBInfoBuilder withPurpose(PurposeType purpose) {
			this.purpose = purpose;
			return this;
		}

		public BDBInfoBuilder withQuality(QualityType quality) {
			this.quality = quality;
			return this;
		}

		public BDBInfo build() {
			//TODO
			return new BDBInfo(this);
		}

		public BDBInfoBuilder withCaptureDevice(RegistryIDType captureDevice) {
			this.captureDevice = captureDevice;
			return this;
		}

		public BDBInfoBuilder withFeatureExtractionAlgorithm(RegistryIDType featureExtractionAlgorithm) {
			this.featureExtractionAlgorithm = featureExtractionAlgorithm;
			return this;
		}

		public BDBInfoBuilder withComparisonAlgorithm(RegistryIDType comparisonAlgorithm) {
			this.comparisonAlgorithm = comparisonAlgorithm;
			return this;
		}

		public BDBInfoBuilder withCompressionAlgorithm(RegistryIDType compressionAlgorithm) {
			this.compressionAlgorithm = compressionAlgorithm;
			return this;
		}
	}

}
