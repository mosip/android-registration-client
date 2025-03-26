package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.entity.Template;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TemplateRepositoryTest {

    @Mock
    private TemplateDao templateDao;

    private TemplateRepository templateRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        templateRepository = new TemplateRepository(templateDao);
    }

    @Test
    public void testGetTemplate() {

        List<String> mockTemplates = Arrays.asList("Hello ", "World");
        when(templateDao.findAllTemplateText("reg-consent-screen-content-template%", "en")).thenReturn(mockTemplates);

        String result = templateRepository.getTemplate("reg-consent-screen-content-template", "en");

        assertEquals("Hello World", result);
    }

    @Test
    public void testGetPreviewTemplate() {

        List<String> mockPreviewTemplates = Arrays.asList("Preview ", "Content");
        when(templateDao.findPreviewTemplateText("reg-android-preview-template-part%", "en")).thenReturn(mockPreviewTemplates);

        String result = templateRepository.getPreviewTemplate("reg-android-preview-template-part", "en");

        assertEquals("Preview Content", result);
    }

    @Test
    public void testSaveTemplate() throws Exception {
        // Mock JSON object
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getString("id")).thenReturn("template1");
        when(mockJson.getString("langCode")).thenReturn("en");
        when(mockJson.getString("templateTypeCode")).thenReturn("TYPE_A");
        when(mockJson.getString("fileText")).thenReturn("Sample text content");
        when(mockJson.getString("fileFormatCode")).thenReturn("TXT");
        when(mockJson.getString("name")).thenReturn("Welcome Template");
        when(mockJson.optBoolean("isDeleted")).thenReturn(false);
        when(mockJson.getBoolean("isActive")).thenReturn(true);

        templateRepository.saveTemplate(mockJson);

        ArgumentCaptor<Template> captor = ArgumentCaptor.forClass(Template.class);
        verify(templateDao, times(1)).insert(captor.capture());

        Template capturedTemplate = captor.getValue();
        assertEquals("template1", capturedTemplate.getId());
        assertEquals("en", capturedTemplate.getLangCode());
        assertEquals("TYPE_A", capturedTemplate.getTemplateTypeCode());
        assertEquals("Sample text content", capturedTemplate.getFileText());
        assertEquals("TXT", capturedTemplate.getFileFormatCode());
        assertEquals("Welcome Template", capturedTemplate.getName());
        assertFalse(capturedTemplate.getIsDeleted());
        assertTrue(capturedTemplate.getIsActive());
    }
}
