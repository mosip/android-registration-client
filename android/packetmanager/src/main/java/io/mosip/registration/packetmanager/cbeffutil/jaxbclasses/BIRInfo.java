package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;

import java.time.LocalDateTime;

public class BIRInfo {

	private static final long serialVersionUID = -2466414332099574792L;
	@Element(name = "Creator", required = false)
	private String creator;
	@Element(name = "Index", required = false)
	private String index;
	@Element(name = "Payload", required = false)
	private byte[] payload;
	@Element(name = "Integrity", required = false)
	private Boolean integrity;
	@Element(name = "CreationDate", required = false)
	private LocalDateTime creationDate;
	@Element(name = "NotValidBefore", required = false)
	private LocalDateTime notValidBefore;
	@Element(name = "NotValidAfter", required = false)
	private LocalDateTime notValidAfter;


	public BIRInfo(BIRInfoBuilder bIRInfoBuilder) {
		this.creator = bIRInfoBuilder.creator;
		this.index = bIRInfoBuilder.index;
		this.payload = bIRInfoBuilder.payload;
		this.integrity = bIRInfoBuilder.integrity;
		this.creationDate = bIRInfoBuilder.creationDate;
		this.notValidBefore = bIRInfoBuilder.notValidBefore;
		this.notValidAfter = bIRInfoBuilder.notValidAfter;
	}

	public static class BIRInfoBuilder {
		private String creator;
		private String index;
		private byte[] payload;
		private Boolean integrity;
		private LocalDateTime creationDate;
		private LocalDateTime notValidBefore;
		private LocalDateTime notValidAfter;

		public BIRInfoBuilder withCreator(String creator) {
			this.creator = creator;
			return this;
		}

		public BIRInfoBuilder withIndex(String index) {
			this.index = index;
			return this;
		}

		public BIRInfoBuilder withPayload(byte[] payload) {
			this.payload = payload;
			return this;
		}

		public BIRInfoBuilder withIntegrity(Boolean integrity) {
			this.integrity = integrity;
			return this;
		}

		public BIRInfoBuilder withCreationDate(LocalDateTime creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public BIRInfoBuilder withNotValidBefore(LocalDateTime notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}

		public BIRInfoBuilder withNotValidAfter(LocalDateTime notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}

		public BIRInfo build() {
			return new BIRInfo(this);
		}

	}
}
