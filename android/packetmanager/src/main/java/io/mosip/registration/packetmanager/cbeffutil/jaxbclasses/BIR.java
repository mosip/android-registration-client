package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Root(name = "BIR")
@Data
@NoArgsConstructor
public class BIR implements Serializable {

	@Element(name = "Version", required = false)
	private VersionType version;
	@Element(name = "CBEFFVersion", required = false)
	private VersionType cbeffversion;
	@Element(name = "BIRInfo", required = false)
	private BIRInfo birInfo;
	@Element(name = "BDBInfo", required = false)
	private BDBInfo bdbInfo;
	@Element(name = "BDB", required = false)
	private byte[] bdb;
	@Element(name = "SB", required = false)
	private byte[] sb;

	@ElementList(required = false, inline = true)
	protected List<BIR> birs;
	@Element(name = "SBInfo", required = false)
	private SBInfo sbInfo;
	@ElementMap(name = "others", key="key", attribute=true, required = false)
	private HashMap<String, String> others;

	public BIR(BIRBuilder birBuilder) {
		this.version = birBuilder.version;
		this.cbeffversion = birBuilder.cbeffversion;
		this.birInfo = birBuilder.birInfo;
		this.bdbInfo = birBuilder.bdbInfo;
		this.bdb = birBuilder.bdb;
		this.sb = birBuilder.sb;
		this.sbInfo = birBuilder.sbInfo;
		this.others = birBuilder.others;
	}

	public static class BIRBuilder {
		private VersionType version;
		private VersionType cbeffversion;
		private BIRInfo birInfo;
		private BDBInfo bdbInfo;
		private byte[] bdb;
		private byte[] sb;
		private SBInfo sbInfo;
		private HashMap<String, String> others = new HashMap<>();

		public BIRBuilder withOthers(HashMap<String, String> others) {
			this.others = others;
			return this;
		}

		public BIRBuilder withOthers(String key, String value) {
			if(Objects.isNull(this.others))
				this.others = new HashMap<>();
			else
				this.others.put(key, value);
			return this;
		}

		public BIRBuilder withVersion(VersionType version) {
			this.version = version;
			return this;
		}

		public BIRBuilder withCbeffversion(VersionType cbeffversion) {
			this.cbeffversion = cbeffversion;
			return this;
		}

		public BIRBuilder withBirInfo(BIRInfo birInfo) {
			this.birInfo = birInfo;
			return this;
		}

		public BIRBuilder withBdbInfo(BDBInfo bdbInfo) {
			this.bdbInfo = bdbInfo;
			return this;
		}

		public BIRBuilder withBdb(byte[] bdb) {
			this.bdb = bdb;
			return this;
		}

		public BIRBuilder withSb(byte[] sb) {
			this.sb = sb == null ? new byte[0] : sb;
			return this;
		}

		public BIRBuilder withSbInfo(SBInfo sbInfo) {
			this.sbInfo = sbInfo;
			return this;
		}

		public BIR build() {
			return new BIR(this);
		}

	}

}
