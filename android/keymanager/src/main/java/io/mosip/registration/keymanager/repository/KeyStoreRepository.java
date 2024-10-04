package io.mosip.registration.keymanager.repository;

import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.entity.KeyStore;

import javax.inject.Inject;

public class KeyStoreRepository {

    private KeyStoreDao keyStoreDao;

    @Inject
    public KeyStoreRepository(KeyStoreDao keyStoreDao) {
        this.keyStoreDao = keyStoreDao;
    }

    public String getCertificateData(String referenceId) {
        KeyStore keyStore = keyStoreDao.findOneKeyStoreByAlias(referenceId);
        return keyStore == null ? null : keyStore.getCertificateData();
    }

    public void saveKeyStore(String referenceId, String certificateData) {
        KeyStore keyStore = new KeyStore(referenceId);
        keyStore.setCertificateData(certificateData);
        keyStore.setCreatedOn(System.currentTimeMillis());
        keyStore.setIsActive(true);
        keyStore.setIsDeleted(false);
        keyStoreDao.insert(keyStore);
    }
}
