package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class SBInfo {

	@Element(name = "Format")
	private RegistryIDType format;

	public SBInfo(SBInfoBuilder sBInfoBuilder) {
		this.format = sBInfoBuilder.format;
	}

	public RegistryIDType getFormat() {
		return format;
	}

	public static class SBInfoBuilder {
		private RegistryIDType format;

		public SBInfoBuilder setFormatOwner(RegistryIDType format) {
			this.format = format;
			return this;
		}

		public SBInfo build() {
			return new SBInfo(this);
		}
	}

}
