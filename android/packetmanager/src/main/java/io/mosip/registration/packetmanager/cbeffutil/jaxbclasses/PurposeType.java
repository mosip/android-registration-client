package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;


public enum PurposeType {

	VERIFY("Verify"),
	IDENTIFY("Identify"),
	ENROLL("Enroll"),
	ENROLL_VERIFY("EnrollVerify"),
	ENROLL_IDENTIFY("EnrollIdentify"),
	AUDIT("Audit");

	private final String value;

	PurposeType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static PurposeType fromValue(String v) {
		for (PurposeType c : PurposeType.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
