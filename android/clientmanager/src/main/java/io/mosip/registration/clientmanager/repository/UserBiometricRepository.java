package io.mosip.registration.clientmanager.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.UserBiometricDao;
import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.entity.UserBiometric;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.Biometric;

public class UserBiometricRepository {

    private UserBiometricDao userBiometricDao;

    private UserDetailDao userDetailDao;

    @Inject
    public UserBiometricRepository(UserBiometricDao userBiometricDao, UserDetailDao userDetailDao) {
        this.userBiometricDao = userBiometricDao;
        this.userDetailDao = userDetailDao;
    }

    public String insertExtractedTemplates(List<BIR> templates, String userId) {
        String response = "";
        List<UserBiometric> bioMetricsList = new ArrayList<>();
        try {
            for (BIR template : templates) {
                UserBiometric biometrics = new UserBiometric();
                biometrics.setBioAttributeCode(getBioAttribute(template.getBdbInfo().getSubtype()));
                biometrics.setBioTypeCode(getBioAttributeCode(getBioAttribute(template.getBdbInfo().getSubtype())));
                biometrics.setUsrId(userId);
                biometrics.setBioTemplate(template.getBdb());
                Long qualityScore = template.getBdbInfo().getQuality().getScore();
                biometrics.setQualityScore(qualityScore.intValue());
                biometrics.setIsDeleted(false);
                bioMetricsList.add(biometrics);
            }
            clearUserBiometrics(userId);
            userBiometricDao.insertAllUserBiometrics(bioMetricsList);
            response = RegistrationConstants.SUCCESS;
        } catch (Exception exception) {
            response = RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE;
        }
        return response;
    }

    public String saveOnboardStatus(String userId) {
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        if (userDetail != null) {
            userDetailDao.updateUserDetail(true, userId);
        }
        return RegistrationConstants.SUCCESS;
    }

    private String getBioAttribute(String subType) {
        String subTypeName = (subType == null || subType.isEmpty()) ? "Face" : String.join("", subType);
        return Modality.getBioAttribute(subTypeName);
    }

    private String getBioAttributeCode(String bioAttribute) {
        Biometric bioType = Biometric.getBiometricByAttribute(bioAttribute);
        return bioType.getBiometricType().value();
    }

    private void clearUserBiometrics(String userId) {
        userBiometricDao.deleteByUsrId(userId);
    }
}
