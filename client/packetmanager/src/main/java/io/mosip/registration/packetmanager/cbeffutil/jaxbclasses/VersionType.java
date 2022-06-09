package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Data
@AllArgsConstructor
public class VersionType {
	@Element(name = "Major", required = false)
	private int major;
	@Element(name = "Minor", required = false)
	private int minor;

	public VersionType(VersionTypeBuilder versionTypeBuilder) {
		this.major = versionTypeBuilder.major;
		this.minor = versionTypeBuilder.minor;
	}
	public static class VersionTypeBuilder {
		private int major;
		private int minor;

		public VersionTypeBuilder withMinor(int minor) {
			this.minor = minor;
			return this;
		}

		public VersionTypeBuilder withMajor(int major) {
			this.major = major;
			return this;
		}

		public VersionType build() {
			return new VersionType(this);
		}
	}
}
