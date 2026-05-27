package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import io.mosip.registration.clientmanager.entity.DocumentType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import java.util.Collections;
import java.util.List;

public class ApplicantValidDocRepository {

    private ApplicantValidDocumentDao applicantValidDocumentDao;

    private DocumentTypeDao documentTypeDao;

    @Inject
    public ApplicantValidDocRepository(ApplicantValidDocumentDao applicantValidDocumentDao,
                                       DocumentTypeDao documentTypeDao) {
        this.applicantValidDocumentDao = applicantValidDocumentDao;
        this.documentTypeDao = documentTypeDao;
    }

    public List<DocumentType> getDocumentTypes(String applicantType, String categoryCode, List<String> langCodes) {

        List<String> docTypeCodes;
        if (applicantType == null) {
            docTypeCodes = this.applicantValidDocumentDao.findAllDocTypesByDocCategory(categoryCode);
        } else {
            docTypeCodes = this.applicantValidDocumentDao.findAllDocTypesByDocCategoryAndApplicantType(applicantType,
                    categoryCode);
        }

        if (docTypeCodes == null || docTypeCodes.isEmpty()) {
            return Collections.emptyList();
        }

        return this.documentTypeDao.findDocumentTypesByCodesAndLangCodes(docTypeCodes, langCodes);
    }

    public void saveApplicantValidDocument(JSONObject jsonObject, String defaultAppTypeCode) throws JSONException {
        String appTypeCode = jsonObject.has("appTypeCode") ? jsonObject.getString("appTypeCode") : defaultAppTypeCode;
        ApplicantValidDocument applicantValidDocument = new ApplicantValidDocument(
                appTypeCode,
                jsonObject.getString("docTypeCode"),
                jsonObject.getString("docCatCode"));
        applicantValidDocument.setIsActive(jsonObject.getBoolean("isActive"));
        applicantValidDocument.setIsDeleted(jsonObject.optBoolean("isDeleted"));
        applicantValidDocumentDao.insert(applicantValidDocument);
    }
}
