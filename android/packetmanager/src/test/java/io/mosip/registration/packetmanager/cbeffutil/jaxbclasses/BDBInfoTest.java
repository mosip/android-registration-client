package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class BDBInfoTest {

    // Minimal stub classes for non-enum types
    static class DummyRegistryIDType extends RegistryIDType {}
    static class DummyQualityType extends QualityType {}

    @Test
    public void testBuilderAndConstructor_AllFields() {
        byte[] challengeResponse = new byte[]{1, 2, 3};
        String index = "idx";
        DummyRegistryIDType format = new DummyRegistryIDType();
        Boolean encryption = Boolean.TRUE;
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime notValidBefore = LocalDateTime.now().minusDays(1);
        LocalDateTime notValidAfter = LocalDateTime.now().plusDays(1);
        BiometricType type = BiometricType.FACE;
        String subtype = "sub";
        ProcessedLevelType level = ProcessedLevelType.RAW; // Use actual enum value
        DummyRegistryIDType product = new DummyRegistryIDType();
        PurposeType purpose = PurposeType.ENROLL; // Use the correct enum value
        QualityType quality = new DummyQualityType(); // Use a stub instance
        DummyRegistryIDType captureDevice = new DummyRegistryIDType();
        DummyRegistryIDType featureExtractionAlgorithm = new DummyRegistryIDType();
        DummyRegistryIDType comparisonAlgorithm = new DummyRegistryIDType();
        DummyRegistryIDType compressionAlgorithm = new DummyRegistryIDType();

        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder()
                .withChallengeResponse(challengeResponse)
                .withIndex(index)
                .withFormat(format)
                .withEncryption(encryption)
                .withCreationDate(creationDate)
                .withNotValidBefore(notValidBefore)
                .withNotValidAfter(notValidAfter)
                .withType(type)
                .withSubtype(subtype)
                .withLevel(level)
                .withProduct(product)
                .withPurpose(purpose)
                .withQuality(quality)
                .withCaptureDevice(captureDevice)
                .withFeatureExtractionAlgorithm(featureExtractionAlgorithm)
                .withComparisonAlgorithm(comparisonAlgorithm)
                .withCompressionAlgorithm(compressionAlgorithm);

        BDBInfo info = builder.build();

        assertArrayEquals(challengeResponse, info.getChallengeResponse());
        assertEquals(index, info.getIndex());
        assertEquals(format, info.getFormat());
        assertEquals(encryption, info.getEncryption());
        assertEquals(creationDate, info.getCreationDate());
        assertEquals(notValidBefore, info.getNotValidBefore());
        assertEquals(notValidAfter, info.getNotValidAfter());
        assertEquals(type, info.getType());
        assertEquals(subtype, info.getSubtype());
        assertEquals(level, info.getLevel());
        assertEquals(product, info.getProduct());
        assertEquals(purpose, info.getPurpose());
        assertEquals(quality, info.getQuality());
        assertEquals(captureDevice, info.getCaptureDevice());
        assertEquals(featureExtractionAlgorithm, info.getFeatureExtractionAlgorithm());
        assertEquals(comparisonAlgorithm, info.getComparisonAlgorithm());
        assertEquals(compressionAlgorithm, info.getCompressionAlgorithm());
    }

    @Test
    public void testBuilder_Defaults() {
        // All fields null
        BDBInfo info = new BDBInfo.BDBInfoBuilder().build();
        assertNull(info.getChallengeResponse());
        assertNull(info.getIndex());
        assertNull(info.getFormat());
        assertNull(info.getEncryption());
        assertNull(info.getCreationDate());
        assertNull(info.getNotValidBefore());
        assertNull(info.getNotValidAfter());
        assertNull(info.getType());
        assertNull(info.getSubtype());
        assertNull(info.getLevel());
        assertNull(info.getProduct());
        assertNull(info.getPurpose());
        assertNull(info.getQuality());
        assertNull(info.getCaptureDevice());
        assertNull(info.getFeatureExtractionAlgorithm());
        assertNull(info.getComparisonAlgorithm());
        assertNull(info.getCompressionAlgorithm());
    }

    @Test
    public void testBuilder_Chaining() {
        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder();
        assertSame(builder, builder.withIndex("a"));
        assertSame(builder, builder.withChallengeResponse(new byte[0]));
        assertSame(builder, builder.withFormat(new DummyRegistryIDType()));
        assertSame(builder, builder.withEncryption(false));
        assertSame(builder, builder.withCreationDate(LocalDateTime.now()));
        assertSame(builder, builder.withNotValidBefore(LocalDateTime.now()));
        assertSame(builder, builder.withNotValidAfter(LocalDateTime.now()));
        assertSame(builder, builder.withType(BiometricType.FACE));
        assertSame(builder, builder.withSubtype("b"));
        assertSame(builder, builder.withLevel(ProcessedLevelType.RAW));
        assertSame(builder, builder.withProduct(new DummyRegistryIDType()));
        assertSame(builder, builder.withPurpose(PurposeType.ENROLL));
        assertSame(builder, builder.withQuality(new DummyQualityType()));
        assertSame(builder, builder.withCaptureDevice(new DummyRegistryIDType()));
        assertSame(builder, builder.withFeatureExtractionAlgorithm(new DummyRegistryIDType()));
        assertSame(builder, builder.withComparisonAlgorithm(new DummyRegistryIDType()));
        assertSame(builder, builder.withCompressionAlgorithm(new DummyRegistryIDType()));
    }

    @Test
    public void testEqualsAndHashCode() {
        BDBInfo.BDBInfoBuilder builder1 = new BDBInfo.BDBInfoBuilder()
                .withIndex("idx")
                .withType(BiometricType.FACE);
        BDBInfo.BDBInfoBuilder builder2 = new BDBInfo.BDBInfoBuilder()
                .withIndex("idx")
                .withType(BiometricType.FACE);
        BDBInfo info1 = builder1.build();
        BDBInfo info2 = builder2.build();

        // Should not be equal to null or different type
        assertFalse(info1.equals(null));
        assertFalse(info1.equals("string"));

        // Should be equal to itself
        assertTrue(info1.equals(info1));

        // Should be equal if fields are the same (default Lombok @Data)
        assertTrue(info1.equals(info2));
        assertEquals(info1.hashCode(), info2.hashCode());

        // Should not be equal if fields differ
        BDBInfo info3 = new BDBInfo.BDBInfoBuilder().withIndex("other").build();
        assertFalse(info1.equals(info3));
    }

    @Test
    public void testToString() {
        BDBInfo info = new BDBInfo.BDBInfoBuilder()
                .withIndex("idx")
                .withType(BiometricType.FACE)
                .build();
        String str = info.toString();
        assertNotNull(str);
        assertTrue(str.contains("idx"));
        assertTrue(str.contains("FACE"));
    }

    @Test
    public void testBuilderWithNulls() {
        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder()
                .withChallengeResponse(null)
                .withIndex(null)
                .withFormat(null)
                .withEncryption(null)
                .withCreationDate(null)
                .withNotValidBefore(null)
                .withNotValidAfter(null)
                .withType(null)
                .withSubtype(null)
                .withLevel(null)
                .withProduct(null)
                .withPurpose(null)
                .withQuality(null)
                .withCaptureDevice(null)
                .withFeatureExtractionAlgorithm(null)
                .withComparisonAlgorithm(null)
                .withCompressionAlgorithm(null);
        BDBInfo info = builder.build();
        assertNull(info.getChallengeResponse());
        assertNull(info.getIndex());
        assertNull(info.getFormat());
        assertNull(info.getEncryption());
        assertNull(info.getCreationDate());
        assertNull(info.getNotValidBefore());
        assertNull(info.getNotValidAfter());
        assertNull(info.getType());
        assertNull(info.getSubtype());
        assertNull(info.getLevel());
        assertNull(info.getProduct());
        assertNull(info.getPurpose());
        assertNull(info.getQuality());
        assertNull(info.getCaptureDevice());
        assertNull(info.getFeatureExtractionAlgorithm());
        assertNull(info.getComparisonAlgorithm());
        assertNull(info.getCompressionAlgorithm());
    }

    @Test
    public void testBuilderChainingOrder() {
        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder();
        builder.withCompressionAlgorithm(new DummyRegistryIDType())
               .withComparisonAlgorithm(new DummyRegistryIDType())
               .withFeatureExtractionAlgorithm(new DummyRegistryIDType())
               .withCaptureDevice(new DummyRegistryIDType())
               .withQuality(new DummyQualityType())
               .withPurpose(PurposeType.ENROLL)
               .withProduct(new DummyRegistryIDType())
               .withLevel(ProcessedLevelType.RAW)
               .withSubtype("sub")
               .withType(BiometricType.FACE)
               .withNotValidAfter(LocalDateTime.now())
               .withNotValidBefore(LocalDateTime.now())
               .withCreationDate(LocalDateTime.now())
               .withEncryption(true)
               .withFormat(new DummyRegistryIDType())
               .withIndex("idx")
               .withChallengeResponse(new byte[]{1,2,3});
        BDBInfo info = builder.build();
        assertNotNull(info);
    }

    @Test
    public void testEqualsAndHashCodeWithAllFields() {
        byte[] challengeResponse = new byte[]{1, 2, 3};
        String index = "idx";
        DummyRegistryIDType format = new DummyRegistryIDType();
        Boolean encryption = Boolean.TRUE;
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime notValidBefore = LocalDateTime.now().minusDays(1);
        LocalDateTime notValidAfter = LocalDateTime.now().plusDays(1);
        BiometricType type = BiometricType.FACE;
        String subtype = "sub";
        ProcessedLevelType level = ProcessedLevelType.RAW;
        DummyRegistryIDType product = new DummyRegistryIDType();
        PurposeType purpose = PurposeType.ENROLL;
        QualityType quality = new DummyQualityType();
        DummyRegistryIDType captureDevice = new DummyRegistryIDType();
        DummyRegistryIDType featureExtractionAlgorithm = new DummyRegistryIDType();
        DummyRegistryIDType comparisonAlgorithm = new DummyRegistryIDType();
        DummyRegistryIDType compressionAlgorithm = new DummyRegistryIDType();

        BDBInfo info1 = new BDBInfo.BDBInfoBuilder()
                .withChallengeResponse(challengeResponse)
                .withIndex(index)
                .withFormat(format)
                .withEncryption(encryption)
                .withCreationDate(creationDate)
                .withNotValidBefore(notValidBefore)
                .withNotValidAfter(notValidAfter)
                .withType(type)
                .withSubtype(subtype)
                .withLevel(level)
                .withProduct(product)
                .withPurpose(purpose)
                .withQuality(quality)
                .withCaptureDevice(captureDevice)
                .withFeatureExtractionAlgorithm(featureExtractionAlgorithm)
                .withComparisonAlgorithm(comparisonAlgorithm)
                .withCompressionAlgorithm(compressionAlgorithm)
                .build();

        BDBInfo info2 = new BDBInfo.BDBInfoBuilder()
                .withChallengeResponse(challengeResponse)
                .withIndex(index)
                .withFormat(format)
                .withEncryption(encryption)
                .withCreationDate(creationDate)
                .withNotValidBefore(notValidBefore)
                .withNotValidAfter(notValidAfter)
                .withType(type)
                .withSubtype(subtype)
                .withLevel(level)
                .withProduct(product)
                .withPurpose(purpose)
                .withQuality(quality)
                .withCaptureDevice(captureDevice)
                .withFeatureExtractionAlgorithm(featureExtractionAlgorithm)
                .withComparisonAlgorithm(comparisonAlgorithm)
                .withCompressionAlgorithm(compressionAlgorithm)
                .build();

        assertTrue(info1.equals(info2));
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    public void testEqualsAndHashCodeWithDifferentFields() {
        BDBInfo info1 = new BDBInfo.BDBInfoBuilder().withIndex("a").build();
        BDBInfo info2 = new BDBInfo.BDBInfoBuilder().withIndex("b").build();
        assertFalse(info1.equals(info2));
        assertNotEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    public void testEqualsWithNullAndOtherType() {
        BDBInfo info = new BDBInfo.BDBInfoBuilder().build();
        assertFalse(info.equals(null));
        assertFalse(info.equals("not a BDBInfo"));
    }

    @Test
    public void testToStringWithAllFields() {
        BDBInfo info = new BDBInfo.BDBInfoBuilder()
                .withIndex("idx")
                .withType(BiometricType.FACE)
                .withLevel(ProcessedLevelType.RAW)
                .withPurpose(PurposeType.ENROLL)
                .withQuality(new DummyQualityType())
                .build();
        String str = info.toString();
        assertNotNull(str);
        assertTrue(str.contains("idx"));
        assertTrue(str.contains("FACE"));
        assertTrue(str.contains("RAW"));
        assertTrue(str.contains("ENROLL"));
    }

    @Test
    public void testBuilderWithNullChaining() {
        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder();
        builder.withChallengeResponse(null)
               .withIndex(null)
               .withFormat(null)
               .withEncryption(null)
               .withCreationDate(null)
               .withNotValidBefore(null)
               .withNotValidAfter(null)
               .withType(null)
               .withSubtype(null)
               .withLevel(null)
               .withProduct(null)
               .withPurpose(null)
               .withQuality(null)
               .withCaptureDevice(null)
               .withFeatureExtractionAlgorithm(null)
               .withComparisonAlgorithm(null)
               .withCompressionAlgorithm(null);
        BDBInfo info = builder.build();
        assertNull(info.getChallengeResponse());
        assertNull(info.getIndex());
        assertNull(info.getFormat());
        assertNull(info.getEncryption());
        assertNull(info.getCreationDate());
        assertNull(info.getNotValidBefore());
        assertNull(info.getNotValidAfter());
        assertNull(info.getType());
        assertNull(info.getSubtype());
        assertNull(info.getLevel());
        assertNull(info.getProduct());
        assertNull(info.getPurpose());
        assertNull(info.getQuality());
        assertNull(info.getCaptureDevice());
        assertNull(info.getFeatureExtractionAlgorithm());
        assertNull(info.getComparisonAlgorithm());
        assertNull(info.getCompressionAlgorithm());
    }

    @Test
    public void testBuilderSelfEquality() {
        BDBInfo.BDBInfoBuilder builder = new BDBInfo.BDBInfoBuilder();
        assertTrue(builder == builder.withIndex("test"));
    }
}
