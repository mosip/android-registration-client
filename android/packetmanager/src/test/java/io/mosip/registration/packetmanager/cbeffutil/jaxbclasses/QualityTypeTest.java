package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class QualityTypeTest extends TestCase {

    public void testGettersAndSetters() {
        QualityType quality = new QualityType();

        // Test algorithm
        RegistryIDType algorithm = new RegistryIDType();
        quality.setAlgorithm(algorithm);
        assertEquals(algorithm, quality.getAlgorithm());

        // Test score
        Long score = 85L;
        quality.setScore(score);
        assertEquals(score, quality.getScore());

        // Test qualityCalculationFailed
        String failed = "true";
        quality.setQualityCalculationFailed(failed);
        assertEquals(failed, quality.getQualityCalculationFailed());

        // Test nulls
        quality.setAlgorithm(null);
        quality.setScore(null);
        quality.setQualityCalculationFailed(null);
        assertNull(quality.getAlgorithm());
        assertNull(quality.getScore());
        assertNull(quality.getQualityCalculationFailed());
    }

    public void testEqualsAndHashCode() {
        QualityType q1 = new QualityType();
        QualityType q2 = new QualityType();

        RegistryIDType algorithm = new RegistryIDType();
        q1.setAlgorithm(algorithm);
        q1.setScore(100L);
        q1.setQualityCalculationFailed("false");

        q2.setAlgorithm(algorithm);
        q2.setScore(100L);
        q2.setQualityCalculationFailed("false");

        assertTrue(q1.equals(q2));
        assertEquals(q1.hashCode(), q2.hashCode());

        q2.setScore(101L);
        assertFalse(q1.equals(q2));
        assertNotSame(q1.hashCode(), q2.hashCode());
    }

    public void testToString() {
        QualityType quality = new QualityType();
        quality.setAlgorithm(new RegistryIDType());
        quality.setScore(50L);
        quality.setQualityCalculationFailed("yes");
        String str = quality.toString();
        assertNotNull(str);
        assertTrue(str.contains("algorithm"));
        assertTrue(str.contains("score"));
        assertTrue(str.contains("qualityCalculationFailed"));
    }
}
