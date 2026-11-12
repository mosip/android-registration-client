package io.mosip.registration_client.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

import io.mosip.registration_client.ocr.extraction.TextNormalizer;

public class TextNormalizerTest {

    @Test
    public void testNormalizeWhitespaceAndQuotes() {
        String input = "  John   Doe  ";
        assertEquals("John Doe", TextNormalizer.normalize(input));

        String quotes = "‘John’ “Doe”";
        assertEquals("'John' \"Doe\"", TextNormalizer.normalize(quotes));
    }

    @Test
    public void testNormalizeStraySeparators() {
        String line = ": John Doe";
        assertEquals("John Doe", TextNormalizer.normalize(line));

        String line2 = "; 12/05/1990";
        assertEquals("12/05/1990", TextNormalizer.normalize(line2));
    }

    @Test
    public void testNormalizeBloodGroup() {
        assertEquals("O+", TextNormalizer.normalizeBloodGroup("O."));
        assertEquals("B+", TextNormalizer.normalizeBloodGroup("B\u2022"));
        assertEquals("A+ (ve)", TextNormalizer.normalizeBloodGroup("A+(ve)"));
        assertEquals("AB- (ve)", TextNormalizer.normalizeBloodGroup("AB - ( ve )"));
    }
}
