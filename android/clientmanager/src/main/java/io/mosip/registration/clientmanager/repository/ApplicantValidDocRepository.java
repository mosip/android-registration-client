package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
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

    public List<String> getDocumentTypes(String applicantType, String categoryCode, String langCode) {
        List<String> docTypeList;
        ArrayList<String> documentList = new ArrayList<>();
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
                    documentList.add(docListByLang.get(0));
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
