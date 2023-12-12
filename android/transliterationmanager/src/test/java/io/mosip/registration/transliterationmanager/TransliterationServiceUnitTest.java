package io.mosip.registration.transliterationmanager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import io.mosip.registration.transliterationmanager.service.TransliterationServiceImpl;
import io.mosip.registration.transliterationmanager.spi.TransliterationService;


public class TransliterationServiceUnitTest {
    TransliterationService transliterationService;

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