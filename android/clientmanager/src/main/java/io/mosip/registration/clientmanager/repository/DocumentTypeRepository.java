package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.entity.DocumentType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class DocumentTypeRepository {

    private DocumentTypeDao documentTypeDao;

    @Inject
    public DocumentTypeRepository(DocumentTypeDao documentTypeDao) {
        this.documentTypeDao = documentTypeDao;
    }

    public void saveDocumentType(JSONObject typeJson) throws JSONException {
        DocumentType documentType = new DocumentType(typeJson.getString("code"),
                typeJson.getString("langCode"));
        documentType.setName(typeJson.getString("name"));
        documentType.setDescription(typeJson.getString("description"));
        documentType.setIsDeleted(typeJson.getBoolean("isDeleted"));
        documentTypeDao.insert(documentType);
    }
}
