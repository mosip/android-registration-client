package io.mosip.registration.clientmanager.dao;

import org.junit.Before;
import org.junit.Test;

import io.mosip.registration.clientmanager.entity.ReasonList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReasonListDaoTest {

    private ReasonListDao reasonListDao;
    private ReasonList reason;

    @Before
    public void setUp() {
        reasonListDao = mock(ReasonListDao.class, CALLS_REAL_METHODS);
        reason = new ReasonList();
        reason.setCode("DEMO");
        reason.setName("InvalidData");
        reason.setLangCode("eng");
        reason.setDescription("Data Invalid");
    }

    @Test
    public void testUpsert_insertsWhenReasonIsNew() {
        doReturn(0).when(reasonListDao).updateByCodeAndLangCode("DEMO", "eng", "InvalidData", "Data Invalid");
        doNothing().when(reasonListDao).insert(any());

        reasonListDao.upsert(reason);

        verify(reasonListDao).insert(reason);
    }

    @Test
    public void testUpsert_updatesInPlaceWhenReasonExists() {
        doReturn(1).when(reasonListDao).updateByCodeAndLangCode("DEMO", "eng", "InvalidData", "Data Invalid");

        reasonListDao.upsert(reason);

        verify(reasonListDao, never()).insert(any());
    }
}
