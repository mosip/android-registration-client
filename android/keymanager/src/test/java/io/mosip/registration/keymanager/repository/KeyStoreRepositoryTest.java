package io.mosip.registration.keymanager.repository;

import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.entity.KeyStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KeyStoreRepositoryTest {

    @Mock
    private KeyStoreDao keyStoreDao;

    @InjectMocks
    private KeyStoreRepository keyStoreRepository;

    @Before
    public void setUp() {
        keyStoreRepository = new KeyStoreRepository(keyStoreDao);
    }

    @Test
    public void testGetCertificateData_WhenKeyStoreExists() {
        String referenceId = "testRef";
        String certData = "mockCertData";
        KeyStore mockKeyStore = new KeyStore(referenceId);
        mockKeyStore.setCertificateData(certData);

        when(keyStoreDao.findOneKeyStoreByAlias(referenceId)).thenReturn(mockKeyStore);

        String result = keyStoreRepository.getCertificateData(referenceId);

        assertEquals(certData, result);
        verify(keyStoreDao, times(1)).findOneKeyStoreByAlias(referenceId);
    }

    @Test
    public void testGetCertificateData_WhenKeyStoreDoesNotExist() {
        String referenceId = "invalidRef";

        when(keyStoreDao.findOneKeyStoreByAlias(referenceId)).thenReturn(null);

        String result = keyStoreRepository.getCertificateData(referenceId);

        assertNull(result);
        verify(keyStoreDao, times(1)).findOneKeyStoreByAlias(referenceId);
    }

    @Test
    public void testSaveKeyStore() {
        String referenceId = "newRef";
        String certData = "newCertData";

        keyStoreRepository.saveKeyStore(referenceId, certData);

        verify(keyStoreDao, times(1)).insert(any(KeyStore.class));
    }
}
