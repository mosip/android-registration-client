package io.mosip.registration.transliterationmanager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import io.mosip.registration.transliterationmanager.service.TransliterationServiceImpl;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class TransliterationServiceUnitTest {
    TransliterationServiceImpl transliterationService;

    @Before
    public void setUp() {
        this.transliterationService = new TransliterationServiceImpl();
    }

    @Test
    public void successfulTransliterationTest() {
        String arabicString = transliterationService.transliterate("en","ar", "Document");
        assertEquals("دُكُمِنت", arabicString);
    }

    @Test
    public void unsupportedLanguageTest() {
        String frenchString = transliterationService.transliterate("en","fr", "Document");
        assertEquals("", frenchString);
    }

    @Test
    public void sameLanguageTest() {
        String englishString = transliterationService.transliterate("en","en", "Document");
        assertEquals("Document", englishString);
    }
}