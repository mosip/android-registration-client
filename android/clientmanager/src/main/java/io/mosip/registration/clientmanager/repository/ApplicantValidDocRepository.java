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

    public void saveApplicantValidDocument(JSONObject jsonObject) throws JSONException {
        ApplicantValidDocument applicantValidDocument = new ApplicantValidDocument(
                jsonObject.getString("appTypeCode"),
                jsonObject.getString("docTypeCode"),
                jsonObject.getString("docCatCode") );
        applicantValidDocument.setIsActive(jsonObject.getBoolean("isActive"));
        applicantValidDocument.setIsDeleted(jsonObject.getBoolean("isDeleted"));
        applicantValidDocumentDao.insert(applicantValidDocument);
    }
}
