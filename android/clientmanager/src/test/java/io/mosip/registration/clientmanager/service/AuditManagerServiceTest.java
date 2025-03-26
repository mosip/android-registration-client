package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.AuditReferenceIdTypes;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

@RunWith(RobolectricTestRunner.class)
public class AuditManagerServiceTest {

    private static final String PACKET_ID = "10001103911003120220530051317";
    private static final String USER_NAME = "audit@123";
    private static final String SCREEN_NAME = "Biometric_capture";
    private static final String TEST_APP_NAME = "test_app"; // Hardcoded app name

    private Context appContext;
    private ClientDatabase clientDatabase;
    private AuditRepository auditRepository;
    private GlobalParamRepository globalParamRepository;
    private AuditManagerServiceImpl auditManagerService;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        AuditDao auditDao = clientDatabase.auditDao();
        auditRepository = new AuditRepository(auditDao);

        GlobalParamDao globalParamDao = clientDatabase.globalParamDao();
        globalParamRepository = new GlobalParamRepository(globalParamDao);

        // Set up SharedPreferences with hardcoded name
        SharedPreferences.Editor editor = appContext.getSharedPreferences(TEST_APP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(SessionManager.USER_NAME, USER_NAME);
        editor.putString(SessionManager.RID, PACKET_ID); // Add RID for registration events
        editor.apply();

        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void auditWithAllParameters_test() {
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION.getId(), Components.REGISTRATION.getName(), PACKET_ID, AuditReferenceIdTypes.REGISTRATION_ID.toString());

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);

        assertEquals(1, audits.size());
        Audit audit = audits.get(0);
        assertEquals(USER_NAME, audit.getSessionUserId());
        assertEquals(USER_NAME, audit.getSessionUserName());
        assertEquals(BuildConfig.BASE_URL, audit.getHostIp());
        assertEquals(BuildConfig.BASE_URL, audit.getHostName());
        assertEquals(TEST_APP_NAME, audit.getApplicationId()); // Matches hardcoded value
        assertEquals(TEST_APP_NAME, audit.getApplicationName()); // Matches hardcoded value
        assertEquals(PACKET_ID, audit.getRefId());
        assertEquals(AuditReferenceIdTypes.REGISTRATION_ID.toString(), audit.getRefIdType());
        assertEquals(AuditEvent.LOADED_LOGIN.getId(), audit.getEventId());
        assertEquals(AuditEvent.LOADED_LOGIN.getName(), audit.getEventName());
        assertEquals(AuditEvent.LOADED_LOGIN.getType(), audit.getEventType());
        assertEquals(Components.REGISTRATION.getId(), audit.getModuleId());
        assertEquals(Components.REGISTRATION.getName(), audit.getModuleName());
    }

    @Test
    public void auditDelete_test() {
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION.getId(), PACKET_ID, AuditReferenceIdTypes.REGISTRATION_ID.toString());

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);
        assertEquals(1, audits.size());

        try {
            Thread.sleep(100); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long currentTime = System.currentTimeMillis();
        globalParamRepository.saveGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL, String.valueOf(currentTime));
        boolean deleted = auditManagerService.deleteAuditLogs();

        assertTrue(deleted);
        audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);
        assertEquals(0, audits.size());
    }

    @Test
    public void auditWithComponent_test() {
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION);

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);

        assertEquals(1, audits.size());
        Audit audit = audits.get(0);
        assertEquals(USER_NAME, audit.getSessionUserId());
        assertEquals(USER_NAME, audit.getSessionUserName());
        assertEquals(PACKET_ID, audit.getRefId()); // Since RID is set and event is REG-EVT
        assertEquals(AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId(), audit.getRefIdType());
        assertEquals(AuditEvent.LOADED_LOGIN.getId(), audit.getEventId());
        assertEquals(Components.REGISTRATION.getId(), audit.getModuleId());
        assertEquals(Components.REGISTRATION.getName(), audit.getModuleName());
    }
}