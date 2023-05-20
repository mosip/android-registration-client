package io.mosip.registration.keymanager.repository;

import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.entity.CACertificateStore;
import lombok.NonNull;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class CACertificateStoreRepository {

    private CACertificateStoreDao certificateStoreDao;

    @Inject
    public CACertificateStoreRepository(CACertificateStoreDao certificateStoreDao) {
        this.certificateStoreDao = certificateStoreDao;
    }

    public CACertificateStore getCACertStore(String thumbprint, String domain) {
        return certificateStoreDao.findByCertThumbprintAndPartnerDomain(thumbprint, domain);
    }

    public List<CACertificateStore> getAllCACertStore(String domain) {
        return certificateStoreDao.findByPartnerDomain(domain);
    }

    public List<CACertificateStore> getAllCACertStoreByCertSubject(String certSubject) {
        return certificateStoreDao.findByCertSubject(certSubject);
    }

    public void save(@NonNull CACertificateStore certStoreObj) {
        String contextUser = "SYSTEM";
        certStoreObj.setCreatedBy(contextUser);
        certStoreObj.setCreatedtimes(System.currentTimeMillis());
        certStoreObj.setIsDeleted(false);
        certificateStoreDao.insert(certStoreObj);
    }

}
