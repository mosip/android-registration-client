package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import io.mosip.registration.clientmanager.entity.DocumentType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class ApplicantValidDocRepository {

    private ApplicantValidDocumentDao applicantValidDocumentDao;

    @Inject
    public ApplicantValidDocRepository(ApplicantValidDocumentDao applicantValidDocumentDao) {
        this.applicantValidDocumentDao = applicantValidDocumentDao;
    }

    public List<DocumentType> getDocumentTypes(String applicantType, String categoryCode, String langCode) {
        List<String> docTypeList;
        ArrayList<DocumentType> documentList = new ArrayList<>();
        if (applicantType == null) {
            docTypeList = this.applicantValidDocumentDao.findAllDocTypesByDocCategory(categoryCode);
        }else {
            docTypeList = this.applicantValidDocumentDao.findAllDocTypesByDocCategoryAndApplicantType(applicantType,
                    categoryCode);
        }
        docTypeList.forEach((v) -> {
            if(v!=null) {
                List<String> docListByLang = this.applicantValidDocumentDao.findAllDocTypesByLanguageCode(v, langCode);
                if (docListByLang != null && !docListByLang.isEmpty()) {
                    DocumentType documentType = new DocumentType(v, langCode);
                    documentType.setName(docListByLang.get(0));
                    documentList.add(documentType);
                }
            }
        });
        return documentList;
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
