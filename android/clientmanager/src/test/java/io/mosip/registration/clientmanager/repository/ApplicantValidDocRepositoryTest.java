package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import io.mosip.registration.clientmanager.entity.DocumentType;
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

    @Mock
    private DocumentTypeDao documentTypeDao;

    private ApplicantValidDocRepository applicantValidDocRepository;

    @Before
    public void setUp() {
        applicantValidDocumentDao = mock(ApplicantValidDocumentDao.class);
        documentTypeDao = mock(DocumentTypeDao.class);
        applicantValidDocRepository = new ApplicantValidDocRepository(applicantValidDocumentDao, documentTypeDao);
    }

    @Test
    public void testGetDocumentTypes_WithApplicantType() {
        String applicantType = "004";
        String categoryCode = "POA";
        String langCode = "en";
        List<String> langCodes = Collections.singletonList(langCode);
        List<String> docTypes = Arrays.asList("doc1", "doc2");

        when(applicantValidDocumentDao.findAllDocTypesByDocCategoryAndApplicantType(applicantType, categoryCode))
                .thenReturn(docTypes);

        DocumentType doc1 = new DocumentType("doc1", langCode);
        doc1.setName("doc1_en");
        DocumentType doc2 = new DocumentType("doc2", langCode);
        doc2.setName("doc2_en");
        List<DocumentType> expected = Arrays.asList(doc1, doc2);

        when(documentTypeDao.findDocumentTypesByCodesAndLangCodes(eq(docTypes), eq(langCodes)))
                .thenReturn(expected);

        List<DocumentType> result = applicantValidDocRepository.getDocumentTypes(applicantType, categoryCode, langCodes);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("doc1_en", result.get(0).getName());
        assertEquals("doc1", result.get(0).getCode());
        assertEquals("doc2_en", result.get(1).getName());
        assertEquals("doc2", result.get(1).getCode());

        verify(applicantValidDocumentDao, times(1)).findAllDocTypesByDocCategoryAndApplicantType(applicantType, categoryCode);
        verify(documentTypeDao, times(1)).findDocumentTypesByCodesAndLangCodes(eq(docTypes), eq(langCodes));
    }

    @Test
    public void testGetDocumentTypes_WithoutApplicantType() {
        String categoryCode = "POA";
        String langCode = "en";

        List<String> langCodes = Collections.singletonList(langCode);
        List<String> docTypes = Arrays.asList("doc1", "doc2");

        when(applicantValidDocumentDao.findAllDocTypesByDocCategory(categoryCode))
                .thenReturn(docTypes);

        DocumentType doc1 = new DocumentType("doc1", langCode);
        doc1.setName("doc1_en");
        DocumentType doc2 = new DocumentType("doc2", langCode);
        doc2.setName("doc2_en");
        List<DocumentType> expected = Arrays.asList(doc1, doc2);

        when(documentTypeDao.findDocumentTypesByCodesAndLangCodes(eq(docTypes), eq(langCodes)))
                .thenReturn(expected);

        List<DocumentType> result = applicantValidDocRepository.getDocumentTypes(null, categoryCode, langCodes);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("doc1_en", result.get(0).getName());
        assertEquals("doc1", result.get(0).getCode());
        assertEquals("doc2_en", result.get(1).getName());
        assertEquals("doc2", result.get(1).getCode());

        verify(applicantValidDocumentDao, times(1)).findAllDocTypesByDocCategory(categoryCode);
        verify(documentTypeDao, times(1)).findDocumentTypesByCodesAndLangCodes(eq(docTypes), eq(langCodes));
    }

    @Test
    public void testGetDocumentTypes_EmptyResult() {
        when(applicantValidDocumentDao.findAllDocTypesByDocCategory(anyString()))
                .thenReturn(Collections.emptyList());

        List<DocumentType> result = applicantValidDocRepository.getDocumentTypes(null, "POA", Collections.singletonList("en"));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(documentTypeDao, never()).findDocumentTypesByCodesAndLangCodes(anyList(), anyList());
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
