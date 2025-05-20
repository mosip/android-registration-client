package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.entity.DocumentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DocumentTypeRepositoryTest {

    @Mock
    private DocumentTypeDao documentTypeDao;

    @Mock
    private JSONObject jsonObject;

    @InjectMocks
    private DocumentTypeRepository documentTypeRepository;

    @Before
    public void setUp() throws JSONException {
        // Mock JSONObject behavior
        when(jsonObject.getString("code")).thenReturn("POA");
        when(jsonObject.getString("langCode")).thenReturn("en");
        when(jsonObject.getString("name")).thenReturn("Passport");
        when(jsonObject.getString("description")).thenReturn("Official travel document");
        when(jsonObject.optBoolean("isDeleted")).thenReturn(false);
    }

    @Test
    public void testSaveDocumentType_Success() throws JSONException {

        documentTypeRepository.saveDocumentType(jsonObject);

        verify(documentTypeDao, times(1)).insert(any(DocumentType.class));
    }

    @Test(expected = JSONException.class)
    public void testSaveDocumentType_InvalidJson() throws JSONException {

        when(jsonObject.getString("code")).thenThrow(new JSONException("Invalid key"));

        documentTypeRepository.saveDocumentType(jsonObject);
    }
}
