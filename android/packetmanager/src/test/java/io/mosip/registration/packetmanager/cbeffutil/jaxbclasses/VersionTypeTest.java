package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class VersionTypeTest extends TestCase {

    public void testAllArgsConstructorAndGettersSetters() {
        VersionType version = new VersionType(1, 2);
        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());

        version.setMajor(10);
        version.setMinor(20);
        assertEquals(10, version.getMajor());
        assertEquals(20, version.getMinor());
    }

    public void testBuilder() {
        VersionType.VersionTypeBuilder builder = new VersionType.VersionTypeBuilder();
        builder.withMajor(5).withMinor(6);
        VersionType version = builder.build();
        assertEquals(5, version.getMajor());
        assertEquals(6, version.getMinor());
    }

    public void testBuilderOrder() {
        VersionType.VersionTypeBuilder builder = new VersionType.VersionTypeBuilder();
        builder.withMinor(8).withMajor(7);
        VersionType version = builder.build();
        assertEquals(7, version.getMajor());
        assertEquals(8, version.getMinor());
    }

    public void testEqualsAndHashCode() {
        VersionType v1 = new VersionType(1, 2);
        VersionType v2 = new VersionType(1, 2);
        VersionType v3 = new VersionType(2, 1);

        assertTrue(v1.equals(v2));
        assertEquals(v1.hashCode(), v2.hashCode());
        assertFalse(v1.equals(v3));
        assertNotSame(v1.hashCode(), v3.hashCode());
        assertFalse(v1.equals(null));
        assertFalse(v1.equals("string"));
        assertTrue(v1.equals(v1));
    }

    public void testToString() {
        VersionType version = new VersionType(3, 4);
        String str = version.toString();
        assertNotNull(str);
        assertTrue(str.contains("major=3"));
        assertTrue(str.contains("minor=4"));
    }

    public void testBuilderDefaultValues() {
        VersionType.VersionTypeBuilder builder = new VersionType.VersionTypeBuilder();
        VersionType version = builder.build();
        assertEquals(0, version.getMajor());
        assertEquals(0, version.getMinor());
    }
}
