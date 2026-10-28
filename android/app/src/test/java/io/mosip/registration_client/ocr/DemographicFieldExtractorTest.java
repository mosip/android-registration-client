package io.mosip.registration_client.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

import io.mosip.registration_client.ocr.extraction.DemographicFieldExtractor;

public class DemographicFieldExtractorTest {

    @Test
    public void testNormalizePhone_ValidFormats() {
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("+91-9876543210"));
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("+91 9876543210"));
        assertEquals("+15551234567", DemographicFieldExtractor.normalizePhone("+1 (555) 123-4567"));
        assertEquals("0712345678", DemographicFieldExtractor.normalizePhone("0712345678"));
        assertEquals("+442079460958", DemographicFieldExtractor.normalizePhone("+44 20 7946 0958"));
    }

    @Test
    public void testNormalizePhone_Idempotent() {
        String input = "+919876543210";
        String first = DemographicFieldExtractor.normalizePhone(input);
        String second = DemographicFieldExtractor.normalizePhone(first);
        assertEquals("+919876543210", first);
        assertEquals(first, second);
        assertFalse(first.startsWith("++"));
    }

    @Test
    public void testNormalizePhone_FixesCorruptedDuplicatePlus() {
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("++919876543210"));
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("+++919876543210"));
    }

    @Test
    public void testNormalizePhone_CleansOcrLabelNoise() {
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone(": +91 9876543210"));
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("; +91 9876543210"));
        assertEquals("+919876543210", DemographicFieldExtractor.normalizePhone("| +91 9876543210"));
    }

    @Test
    public void testNormalizePhone_RejectsInvalidLengths() {
        assertNull("Too short phone (< 7 digits)", DemographicFieldExtractor.normalizePhone("12345"));
        assertNull("Too short phone (< 7 digits)", DemographicFieldExtractor.normalizePhone("+12345"));
        assertNull("Too long phone (> 15 digits)", DemographicFieldExtractor.normalizePhone("1234567890123456"));
        assertNull("Too long phone (> 15 digits)", DemographicFieldExtractor.normalizePhone("+1234567890123456"));
    }

    @Test
    public void testNormalizePhone_RejectsNoise() {
        assertNull(DemographicFieldExtractor.normalizePhone(null));
        assertNull(DemographicFieldExtractor.normalizePhone(""));
        assertNull(DemographicFieldExtractor.normalizePhone("   "));
        assertNull(DemographicFieldExtractor.normalizePhone("+"));
        assertNull(DemographicFieldExtractor.normalizePhone("abc"));
        assertNull(DemographicFieldExtractor.normalizePhone("Phone:"));
    }
}
