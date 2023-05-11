package io.mosip.registration.packetmanager.dto.PacketWriter;

import java.io.Serializable;

public enum BiometricType implements Serializable {

	SCENT("Scent"),
	DNA("DNA"),
	EAR("Ear "),
	FACE("Face"),
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

	BiometricType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static BiometricType fromValue(String v) {
		for (BiometricType c : BiometricType.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
