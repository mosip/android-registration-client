package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ApplicantValidDocRepository {

    private ApplicantValidDocumentDao applicantValidDocumentDao;

    @Inject
    public ApplicantValidDocRepository(ApplicantValidDocumentDao applicantValidDocumentDao) {
        this.applicantValidDocumentDao = applicantValidDocumentDao;
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
