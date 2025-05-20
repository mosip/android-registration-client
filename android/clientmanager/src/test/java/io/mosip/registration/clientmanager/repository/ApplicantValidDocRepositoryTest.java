package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicantValidDocRepositoryTest {

    @Mock
    private ApplicantValidDocumentDao applicantValidDocumentDao;

    private ApplicantValidDocRepository applicantValidDocRepository;

    @Before
    public void setUp() {
        applicantValidDocumentDao = mock(ApplicantValidDocumentDao.class);
        applicantValidDocRepository = new ApplicantValidDocRepository(applicantValidDocumentDao);
    }

    @Test
    public void testGetDocumentTypes_WithApplicantType() {
        String applicantType = "004";
        String categoryCode = "POA";
        String langCode = "en";

        List<String> docTypes = Arrays.asList("doc1", "doc2");
        List<String> langSpecificDocs = Arrays.asList("doc1_en");

        when(applicantValidDocumentDao.findAllDocTypesByDocCategoryAndApplicantType(applicantType, categoryCode))
                .thenReturn(docTypes);
        when(applicantValidDocumentDao.findAllDocTypesByLanguageCode(anyString(), eq(langCode)))
                .thenReturn(langSpecificDocs);

        List<String> result = applicantValidDocRepository.getDocumentTypes(applicantType, categoryCode, langCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("doc1_en", result.get(0));

        verify(applicantValidDocumentDao, times(1)).findAllDocTypesByDocCategoryAndApplicantType(applicantType, categoryCode);
        verify(applicantValidDocumentDao, times(2)).findAllDocTypesByLanguageCode(anyString(), eq(langCode));
    }

    @Test
    public void testGetDocumentTypes_WithoutApplicantType() {
        String categoryCode = "POA";
        String langCode = "en";

        List<String> docTypes = Arrays.asList("doc1", "doc2");
        List<String> langSpecificDocs = Arrays.asList("doc1_en");

        when(applicantValidDocumentDao.findAllDocTypesByDocCategory(categoryCode))
                .thenReturn(docTypes);
        when(applicantValidDocumentDao.findAllDocTypesByLanguageCode(anyString(), eq(langCode)))
                .thenReturn(langSpecificDocs);

        List<String> result = applicantValidDocRepository.getDocumentTypes(null, categoryCode, langCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("doc1_en", result.get(0));

        verify(applicantValidDocumentDao, times(1)).findAllDocTypesByDocCategory(categoryCode);
        verify(applicantValidDocumentDao, times(2)).findAllDocTypesByLanguageCode(anyString(), eq(langCode));
    }

    @Test
    public void testGetDocumentTypes_EmptyResult() {
        when(applicantValidDocumentDao.findAllDocTypesByDocCategory(anyString()))
                .thenReturn(Collections.emptyList());

        List<String> result = applicantValidDocRepository.getDocumentTypes(null, "POA", "en");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSaveApplicantValidDocument() throws JSONException {
        JSONObject jsonObjectMock = mock(JSONObject.class);

        when(jsonObjectMock.has("appTypeCode")).thenReturn(true);
        when(jsonObjectMock.getString("appTypeCode")).thenReturn("004");
        when(jsonObjectMock.getString("docTypeCode")).thenReturn("DocType");
        when(jsonObjectMock.getString("docCatCode")).thenReturn("POA");
        when(jsonObjectMock.getBoolean("isActive")).thenReturn(true);
        when(jsonObjectMock.optBoolean("isDeleted")).thenReturn(false);

        applicantValidDocRepository.saveApplicantValidDocument(jsonObjectMock, "defaultType");

        verify(applicantValidDocumentDao, times(1)).insert(any(ApplicantValidDocument.class));
    }

    @Test
    public void testSaveApplicantValidDocument_UsesDefaultAppTypeCode() throws JSONException {
        JSONObject jsonObjectMock = mock(JSONObject.class);
        String defaultAppTypeCode = "defaultType";

        when(jsonObjectMock.has("appTypeCode")).thenReturn(false);
        when(jsonObjectMock.getString("docTypeCode")).thenReturn("DocType");
        when(jsonObjectMock.getString("docCatCode")).thenReturn("POA");
        when(jsonObjectMock.getBoolean("isActive")).thenReturn(true);
        when(jsonObjectMock.optBoolean("isDeleted")).thenReturn(false);

        applicantValidDocRepository.saveApplicantValidDocument(jsonObjectMock, defaultAppTypeCode);

        verify(applicantValidDocumentDao, times(1)).insert(argThat(new ArgumentMatcher<ApplicantValidDocument>() {
            @Override
            public boolean matches(ApplicantValidDocument doc) {
                return doc.getAppTypeCode().equals(defaultAppTypeCode) &&
                        doc.getDocTypeCode().equals("DocType") &&
                        doc.getDocCatCode().equals("POA") &&
                        doc.getIsActive() &&
                        !doc.getIsDeleted();
            }
        }));
    }
}
