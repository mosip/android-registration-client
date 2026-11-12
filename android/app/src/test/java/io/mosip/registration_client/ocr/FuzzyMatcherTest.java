package io.mosip.registration_client.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

import io.mosip.registration_client.ocr.extraction.FuzzyMatcher;

public class FuzzyMatcherTest {

    @Test
    public void testJaroWinklerExactMatch() {
        assertEquals(1.0, FuzzyMatcher.jaroWinkler("name", "name"), 0.001);
        assertEquals(1.0, FuzzyMatcher.jaroWinkler("date of birth", "date of birth"), 0.001);
    }

    @Test
    public void testJaroWinklerNullOrEmpty() {
        assertEquals(0.0, FuzzyMatcher.jaroWinkler(null, "test"), 0.001);
        assertEquals(0.0, FuzzyMatcher.jaroWinkler("test", null), 0.001);
        assertEquals(0.0, FuzzyMatcher.jaroWinkler("", "test"), 0.001);
    }

    @Test
    public void testJaroWinklerMinorTypo() {
        double score = FuzzyMatcher.jaroWinkler("address", "adress");
        assertTrue("Score for address/adress should be > 0.85", score > 0.85);

        double birthScore = FuzzyMatcher.jaroWinkler("birth", "b1rth");
        assertTrue("Score for birth/b1rth should be > 0.80", birthScore > 0.80);
    }

    @Test
    public void testContainsFuzzy() {
        assertTrue(FuzzyMatcher.containsFuzzy("Name: John Doe", "name"));
        assertTrue(FuzzyMatcher.containsFuzzy("Permanent Adress Line", "address", 0.80));
        assertFalse(FuzzyMatcher.containsFuzzy("Telephone", "date of birth"));
    }
}
