package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

public enum SingleAnySubtypeType {

	LEFT("Left"),
	RIGHT("Right"),
	THUMB("Thumb"),
	INDEX_FINGER("IndexFinger"),
	MIDDLE_FINGER("MiddleFinger"),
	RING_FINGER("RingFinger"),
	LITTLE_FINGER("LittleFinger");

	private final String value;

	SingleAnySubtypeType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static SingleAnySubtypeType fromValue(String v) {
		for (SingleAnySubtypeType c : SingleAnySubtypeType.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
