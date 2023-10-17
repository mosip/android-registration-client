package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.List;

public class ApplicantValidDocRepository {

    private ApplicantValidDocumentDao applicantValidDocumentDao;

    @Inject
    public ApplicantValidDocRepository(ApplicantValidDocumentDao applicantValidDocumentDao) {
        this.applicantValidDocumentDao = applicantValidDocumentDao;
    }

    public List<String> getDocumentTypes(String applicantType, String categoryCode, String langCode) {
        if(applicantType == null)
            return this.applicantValidDocumentDao.findAllDocTypesByDocCategory(categoryCode);

        return this.applicantValidDocumentDao.findAllDocTypesByDocCategoryAndApplicantType(applicantType,
                categoryCode);
    }

    public void saveApplicantValidDocument(JSONObject jsonObject, String defaultAppTypeCode) throws JSONException {
        String appTypeCode = defaultAppTypeCode;
        if(jsonObject.has("appTypeCode")){
            appTypeCode = jsonObject.getString("appTypeCode");
        }
        ApplicantValidDocument applicantValidDocument = new ApplicantValidDocument(
                appTypeCode,
                jsonObject.getString("docTypeCode"),
                jsonObject.getString("docCatCode") );
        applicantValidDocument.setIsActive(jsonObject.getBoolean("isActive"));
        applicantValidDocument.setIsDeleted(jsonObject.optBoolean("isDeleted"));
        applicantValidDocumentDao.insert(applicantValidDocument);
    }
}
