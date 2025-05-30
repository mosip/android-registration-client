package io.mosip.registration.keymanager.repository;

import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.entity.CACertificateStore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CACertificateStoreRepositoryTest {

    private CACertificateStoreDao mockDao;
    private CACertificateStoreRepository repository;

    private final String thumbprint = "7ff90b5023f4f79c6e78f1f80dbdb9edeed10127";
    private final String domain = "DEVICE";
    private final String certSubject = "N=PARTNER-device,OU=IDA-TEST-ORG-UNIT,O=dsl_device_pid1747985848450,ST=KA,C=IN";

    @Before
    public void setup() {
        mockDao = mock(CACertificateStoreDao.class);
        repository = new CACertificateStoreRepository(mockDao);
    }

    @Test
    public void testGetCACertStore() {
        CACertificateStore expectedStore = new CACertificateStore(UUID.randomUUID().toString());

        when(mockDao.findByCertThumbprintAndPartnerDomain(thumbprint, domain)).thenReturn(expectedStore);

        CACertificateStore result = repository.getCACertStore(thumbprint, domain);

        assertNotNull(result);
        assertEquals(expectedStore, result);
        verify(mockDao).findByCertThumbprintAndPartnerDomain(thumbprint, domain);
    }

    @Test
    public void testGetAllCACertStore() {
        List<CACertificateStore> expectedList = Arrays.asList(new CACertificateStore(UUID.randomUUID().toString()), new CACertificateStore(UUID.randomUUID().toString()));
        when(mockDao.findByPartnerDomain(domain)).thenReturn(expectedList);

        List<CACertificateStore> result = repository.getAllCACertStore(domain);

        assertEquals(2, result.size());
        verify(mockDao).findByPartnerDomain(domain);
    }

    @Test
    public void testGetAllCACertStoreByCertSubject() {
        List<CACertificateStore> expectedList = Arrays.asList(new CACertificateStore(UUID.randomUUID().toString()));
        when(mockDao.findByCertSubject(certSubject)).thenReturn(expectedList);

        List<CACertificateStore> result = repository.getAllCACertStoreByCertSubject(certSubject);

        assertEquals(1, result.size());
        verify(mockDao).findByCertSubject(certSubject);
    }

    @Test
    public void testSave() {
        CACertificateStore certStore = new CACertificateStore(UUID.randomUUID().toString());

        repository.save(certStore);

        ArgumentCaptor<CACertificateStore> captor = ArgumentCaptor.forClass(CACertificateStore.class);
        verify(mockDao).insert(captor.capture());

        CACertificateStore saved = captor.getValue();
        assertEquals("SYSTEM", saved.getCreatedBy());
        assertFalse(saved.getIsDeleted());
        assertTrue(saved.getCreatedtimes() > 0);
    }

    @Test
    public void testSaveWithNullShouldThrowException() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}

