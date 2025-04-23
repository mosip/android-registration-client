package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.entity.Audit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditRepositoryTest {

    @Mock
    private AuditDao auditDao;

    private AuditRepository auditRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        auditRepository = new AuditRepository(auditDao);
    }

    @Test
    public void testGetAuditsFromDate() {
        // Mock data
        Audit audit1 = createAudit("UPDATE");
        Audit audit2 = createAudit("UPDATE");
        List<Audit> mockAudits = Arrays.asList(audit1, audit2);

        when(auditDao.getAll(1700000000000L)).thenReturn(mockAudits);

        // Execute
        List<Audit> result = auditRepository.getAuditsFromDate(1700000000000L);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("UPDATE", result.get(0).getEventName());
        assertEquals("UPDATE", result.get(1).getEventName());
    }

    @Test
    public void testInsertAudit() {
        Audit audit = createAudit("UPDATE");

        // Execute
        auditRepository.insertAudit(audit);

        // Verify insertion
        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        verify(auditDao).insert(captor.capture());

        Audit capturedAudit = captor.getValue();
        assertNotNull(capturedAudit);
        assertEquals("UPDATE", capturedAudit.getEventName());
    }

    @Test
    public void testDeleteAllAuditsTillDate() {
        long tillDate = 1700000000000L;

        // Execute
        auditRepository.deleteAllAuditsTillDate(tillDate);

        // Verify deletion
        verify(auditDao).deleteAll(tillDate);
    }

    private Audit createAudit(String eventName) {
        return new Audit(
                1L,
                "RPR_402",
                eventName,
                "BUSINESS",
                1744383051000L,
                "regproc-workflow-db87b7956-thczs",
                "10.42.1.216",
                "MOSIP_4",
                "REGISTRATION_PROCESSOR",
                "SYSTEM",
                "",
                "",
                "NO_ID",
                "SYSTEM",
                "WORKFLOW_ACTION_JOB",
                "RPR-WAJ-000",
                ""
        );
    }
}
