package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.LanguageDao;
import io.mosip.registration.clientmanager.entity.Language;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LanguageRepositoryTest {

    @Mock
    private LanguageDao languageDao;

    private LanguageRepository languageRepository;

    @Before
    public void setUp() {
        languageRepository = new LanguageRepository(languageDao);
    }

    private void setLocalCache(LanguageRepository repository, String key, String value) throws Exception {
        Field cacheField = LanguageRepository.class.getDeclaredField("localCache");
        cacheField.setAccessible(true);
        Map<String, String> cache = (Map<String, String>) cacheField.get(repository);
        cache.put(key, value);
    }

    @Test
    public void testSaveLanguage() {
        JSONObject mockJson = mock(JSONObject.class);

        try {
            when(mockJson.getString("code")).thenReturn("en");
            when(mockJson.getString("name")).thenReturn("English");
            when(mockJson.getString("nativeName")).thenReturn("English");
            when(mockJson.getBoolean("isActive")).thenReturn(true);
            when(mockJson.getBoolean("isDeleted")).thenReturn(false);
        } catch (Exception e) {
            fail("Mocking failed: " + e.getMessage());
        }

        // Call method
        try {
            languageRepository.saveLanguage(mockJson);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        // Capture argument
        ArgumentCaptor<Language> argumentCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageDao, times(1)).insertLanguage(argumentCaptor.capture());

        // Validate inserted language object
        Language capturedLanguage = argumentCaptor.getValue();
        assertEquals("en", capturedLanguage.getCode());
        assertEquals("English", capturedLanguage.getName());
        assertEquals("English", capturedLanguage.getNativeName());
        assertTrue(capturedLanguage.getIsActive());
        assertFalse(capturedLanguage.getIsDeleted());

        // Validate local cache
        assertEquals("English", languageRepository.getNativeName("en"));
    }

    @Test
    public void testGetNativeNameFromCache() throws Exception {
        // Set cache using reflection
        setLocalCache(languageRepository, "en", "English");

        // Test fetching from cache
        String nativeName = languageRepository.getNativeName("en");
        assertEquals("English", nativeName);
    }

    @Test
    public void testGetNativeNameFromDatabase() {
        // Prepare database response
        List<Language> languageList = new ArrayList<>();
        Language language = new Language("fr");
        language.setNativeName("Français");
        languageList.add(language);

        when(languageDao.getAllActiveLanguage()).thenReturn(languageList);

        // Test fetching from database
        String nativeName = languageRepository.getNativeName("fr");
        assertEquals("Français", nativeName);
    }

    @Test
    public void testGetNativeName_NotFound() {
        when(languageDao.getAllActiveLanguage()).thenReturn(new ArrayList<>());

        // Fetch unknown language
        String nativeName = languageRepository.getNativeName("xx");
        assertNull(nativeName);
    }

    @Test
    public void testGetAllLanguages() {
        List<Language> mockLanguages = new ArrayList<>();
        mockLanguages.add(new Language("en"));
        mockLanguages.add(new Language("fr"));

        when(languageDao.getAllActiveLanguage()).thenReturn(mockLanguages);

        List<Language> result = languageRepository.getAllLanguages();
        assertEquals(2, result.size());
    }
}
