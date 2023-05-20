package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

public enum SingleType {

	SCENT("Scent"), DNA("DNA"),
	EAR("Ear "), FACE("Face"),
	FINGER("Finger"),
	FOOT("Foot"),
	VEIN("Vein"),
	HAND_GEOMETRY("HandGeometry"),
	IRIS("Iris"),
	RETINA("Retina"),
	VOICE("Voice"),
	GAIT("Gait"),
	KEYSTROKE("Keystroke"),
	LIP_MOVEMENT("LipMovement"),
	SIGNATURE_SIGN("SignatureSign"),
	EXCEPTION_PHOTO("ExceptionPhoto");

	private final String value;

	SingleType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static SingleType fromValue(String v) {
		for (SingleType c : SingleType.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
