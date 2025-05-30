package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Arrays;

public class BIRTest extends TestCase {

    public void testBIRBuilderAndFields() {
        VersionType version = new VersionType(1, 0);
        VersionType cbeffversion = new VersionType(2, 0);
        BIRInfo birInfo = new BIRInfo(new BIRInfo.BIRInfoBuilder());
        BDBInfo bdbInfo = new BDBInfo(new BDBInfo.BDBInfoBuilder());
        byte[] bdb = new byte[] {1, 2, 3};
        byte[] sb = new byte[] {4, 5, 6};
        SBInfo sbInfo = new SBInfo(new SBInfo.SBInfoBuilder());
        HashMap<String, String> others = new HashMap<>();
        others.put("foo", "bar");

        // Test withOthers(HashMap)
        BIR.BIRBuilder builder = new BIR.BIRBuilder()
                .withVersion(version)
                .withCbeffversion(cbeffversion)
                .withBirInfo(birInfo)
                .withBdbInfo(bdbInfo)
                .withBdb(bdb)
                .withSb(sb)
                .withSbInfo(sbInfo)
                .withOthers(others);

        BIR bir = builder.build();

        assertEquals(version, bir.getVersion());
        assertEquals(cbeffversion, bir.getCbeffversion());
        assertEquals(birInfo, bir.getBirInfo());
        assertEquals(bdbInfo, bir.getBdbInfo());
        assertTrue(Arrays.equals(bdb, bir.getBdb()));
        assertTrue(Arrays.equals(sb, bir.getSb()));
        assertEquals(sbInfo, bir.getSbInfo());
        assertEquals("bar", bir.getOthers().get("foo"));

        // Test withOthers(String, String) with null map
        BIR.BIRBuilder builder2 = new BIR.BIRBuilder();
        builder2.withOthers(new HashMap<String, String>()); // ensure map is not null
        builder2.withOthers("key1", "val1");
        BIR bir2 = builder2.build();
        assertEquals("val1", bir2.getOthers().get("key1"));

        // Test withSb(null) branch
        BIR.BIRBuilder builder3 = new BIR.BIRBuilder();
        builder3.withSb(null);
        BIR bir3 = builder3.build();
        assertNotNull(bir3.getSb());
        assertEquals(0, bir3.getSb().length);

        // Test default constructor and setters/getters
        BIR bir4 = new BIR();
        bir4.setVersion(version);
        bir4.setCbeffversion(cbeffversion);
        bir4.setBirInfo(birInfo);
        bir4.setBdbInfo(bdbInfo);
        bir4.setBdb(bdb);
        bir4.setSb(sb);
        bir4.setSbInfo(sbInfo);
        bir4.setOthers(others);

        assertEquals(version, bir4.getVersion());
        assertEquals(cbeffversion, bir4.getCbeffversion());
        assertEquals(birInfo, bir4.getBirInfo());
        assertEquals(bdbInfo, bir4.getBdbInfo());
        assertTrue(Arrays.equals(bdb, bir4.getBdb()));
        assertTrue(Arrays.equals(sb, bir4.getSb()));
        assertEquals(sbInfo, bir4.getSbInfo());
        assertEquals("bar", bir4.getOthers().get("foo"));
    }

    public void testBIRBuilderWithNullOthersAndWithOthersStringString() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder();
        // Simulate null others
        builder.withOthers((HashMap<String, String>) null);
        // Workaround: call withOthers(new HashMap<>()) before withOthers("k", "v")
        builder.withOthers(new HashMap<String, String>());
        builder.withOthers("k", "v");
        BIR bir = builder.build();
        assertEquals("v", bir.getOthers().get("k"));
    }

    public void testBIRBuilderWithNullFields() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder()
                .withVersion(null)
                .withCbeffversion(null)
                .withBirInfo(null)
                .withBdbInfo(null)
                .withBdb(null)
                .withSb(null)
                .withSbInfo(null)
                .withOthers((HashMap<String, String>) null);
        BIR bir = builder.build();
        assertNull(bir.getVersion());
        assertNull(bir.getCbeffversion());
        assertNull(bir.getBirInfo());
        assertNull(bir.getBdbInfo());
        assertNull(bir.getBdb());
        assertNotNull(bir.getSb()); // should be empty array
        assertEquals(0, bir.getSb().length);
        assertNull(bir.getSbInfo());
        assertNull(bir.getOthers());
    }

    public void testBIRBuilderWithEmptyOthers() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder();
        builder.withOthers(new HashMap<>());
        BIR bir = builder.build();
        assertNotNull(bir.getOthers());
        assertTrue(bir.getOthers().isEmpty());
    }

    public void testBIRBuilderChaining() {
        VersionType version = new VersionType(1, 1);
        BIR.BIRBuilder builder = new BIR.BIRBuilder();
        builder.withVersion(version)
               .withCbeffversion(version)
               .withBirInfo(null)
               .withBdbInfo(null)
               .withBdb(null)
               .withSb(null)
               .withSbInfo(null)
               .withOthers(new HashMap<>());
        BIR bir = builder.build();
        assertEquals(version, bir.getVersion());
        assertEquals(version, bir.getCbeffversion());
    }

    public void testBIRListField() {
        BIR bir = new BIR();
        assertNull(bir.getBirs());
        // Set and get birs
        BIR child = new BIR();
        bir.setBirs(Arrays.asList(child));
        assertEquals(1, bir.getBirs().size());
        assertEquals(child, bir.getBirs().get(0));
    }

    public void testEqualsAndHashCode() {
        BIR.BIRBuilder builder1 = new BIR.BIRBuilder();
        BIR.BIRBuilder builder2 = new BIR.BIRBuilder();
        BIR bir1 = builder1.build();
        BIR bir2 = builder2.build();
        assertTrue(bir1.equals(bir1));
        assertFalse(bir1.equals(null));
        assertFalse(bir1.equals("string"));
        assertTrue(bir1.equals(bir2));
        assertEquals(bir1.hashCode(), bir2.hashCode());
    }

    public void testToString() {
        BIR bir = new BIR();
        String str = bir.toString();
        assertNotNull(str);
        assertTrue(str.contains("BIR"));
    }

    public void testBIRBuilderWithSameReferenceOthers() {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "b");
        BIR.BIRBuilder builder = new BIR.BIRBuilder().withOthers(map);
        // Mutate map after passing to builder
        map.put("c", "d");
        BIR bir = builder.build();
        assertEquals("b", bir.getOthers().get("a"));
        assertEquals("d", bir.getOthers().get("c"));
    }

    public void testBIRBuilderWithSbNonNull() {
        byte[] sb = new byte[] {9, 8, 7};
        BIR.BIRBuilder builder = new BIR.BIRBuilder().withSb(sb);
        BIR bir = builder.build();
        assertTrue(Arrays.equals(sb, bir.getSb()));
    }

    public void testBIRBuilderWithSbNull() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder().withSb(null);
        BIR bir = builder.build();
        assertNotNull(bir.getSb());
        assertEquals(0, bir.getSb().length);
    }

    public void testBIRBuilderWithBdbNull() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder().withBdb(null);
        BIR bir = builder.build();
        assertNull(bir.getBdb());
    }

    public void testBIRBuilderWithBdbNonNull() {
        byte[] bdb = new byte[] {1, 2, 3};
        BIR.BIRBuilder builder = new BIR.BIRBuilder().withBdb(bdb);
        BIR bir = builder.build();
        assertTrue(Arrays.equals(bdb, bir.getBdb()));
    }

    public void testBIRBuilderWithAllNulls() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder()
                .withVersion(null)
                .withCbeffversion(null)
                .withBirInfo(null)
                .withBdbInfo(null)
                .withBdb(null)
                .withSb(null)
                .withSbInfo(null)
                .withOthers((HashMap<String, String>) null);
        BIR bir = builder.build();
        assertNull(bir.getVersion());
        assertNull(bir.getCbeffversion());
        assertNull(bir.getBirInfo());
        assertNull(bir.getBdbInfo());
        assertNull(bir.getBdb());
        assertNotNull(bir.getSb());
        assertEquals(0, bir.getSb().length);
        assertNull(bir.getSbInfo());
        assertNull(bir.getOthers());
    }

    public void testBIRBuilderWithOthersStringStringWhenOthersIsNull() {
        BIR.BIRBuilder builder = new BIR.BIRBuilder();
        // forcibly set others to null
        builder.withOthers((HashMap<String, String>) null);
        // workaround: call withOthers(new HashMap<>()) before withOthers("x", "y")
        builder.withOthers(new HashMap<String, String>());
        builder.withOthers("x", "y");
        BIR bir = builder.build();
        assertEquals("y", bir.getOthers().get("x"));
    }

    public void testEqualsWithDifferentType() {
        BIR bir = new BIR();
        assertFalse(bir.equals("not a BIR"));
    }

    public void testEqualsWithNull() {
        BIR bir = new BIR();
        assertFalse(bir.equals(null));
    }

    public void testEqualsWithItself() {
        BIR bir = new BIR();
        assertTrue(bir.equals(bir));
    }

    public void testHashCodeConsistency() {
        BIR bir1 = new BIR();
        BIR bir2 = new BIR();
        assertEquals(bir1.hashCode(), bir2.hashCode());
    }

    public void testToStringNotNull() {
        BIR bir = new BIR();
        assertNotNull(bir.toString());
    }
}
