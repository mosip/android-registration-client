package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
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
import io.mosip.registration.clientmanager.service.AuditManagerServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

/**
 * @author Anshul Vanawat
 * @since 1.0.0
 */

@RunWith(RobolectricTestRunner.class)
public class AuditManagerServiceTest {

    private static final String PACKET_ID = "10001103911003120220530051317";
    private static final String USER_NAME = "audit@123";
    private static final String SCREEN_NAME = "Biometric_capture";

    private Context appContext;
    private ClientDatabase clientDatabase;
    private AuditRepository auditRepository;
    private GlobalParamRepository globalParamRepository;
    private AuditManagerService auditManagerService;

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

        //Dummy value for session user
        SharedPreferences.Editor editor = this.appContext.getSharedPreferences(this.appContext.getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();
        editor.putString(SessionManager.USER_NAME, USER_NAME);
        editor.apply();
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void auditWithAllParameters_test() {
        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION.getId(), Components.REGISTRATION.getName(), PACKET_ID, AuditReferenceIdTypes.REGISTRATION_ID.toString());

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);

        assertEquals(1, audits.size());
        assertEquals(USER_NAME, audits.get(0).getSessionUserId());
        assertEquals(USER_NAME, audits.get(0).getSessionUserName());
        Assert.assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostIp());
        assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostName());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationId());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationName());
        assertEquals(PACKET_ID, audits.get(0).getRefId());
        assertEquals(AuditReferenceIdTypes.REGISTRATION_ID.toString(), audits.get(0).getRefIdType());
        assertEquals(AuditEvent.LOADED_LOGIN.getId(), audits.get(0).getEventId());
        assertEquals(AuditEvent.LOADED_LOGIN.getName(), audits.get(0).getEventName());
        assertEquals(AuditEvent.LOADED_LOGIN.getType(), audits.get(0).getEventType());
        assertEquals(Components.REGISTRATION.getId(), audits.get(0).getModuleId());
        assertEquals(Components.REGISTRATION.getName(), audits.get(0).getModuleName());
    }

    @Test
    public void auditDelete_test() {
        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION.getId(), PACKET_ID, AuditReferenceIdTypes.REGISTRATION_ID.toString());

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);
        assertEquals(1, audits.size());

        globalParamRepository.saveGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL, String.valueOf(System.currentTimeMillis()));
        auditManagerService.deleteAuditLogs();

        audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);
        assertEquals(0, audits.size());
    }

    @Test
    public void auditWithComponent_test() {
        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
        auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.REGISTRATION);

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);

        assertEquals(1, audits.size());
        assertEquals(USER_NAME, audits.get(0).getSessionUserId());
        assertEquals(USER_NAME, audits.get(0).getSessionUserName());
        assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostIp());
        assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostName());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationId());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationName());
        assertEquals(USER_NAME, audits.get(0).getRefId());
        assertEquals(AuditReferenceIdTypes.USER_ID.name(), audits.get(0).getRefIdType());
        assertEquals(AuditEvent.LOADED_LOGIN.getId(), audits.get(0).getEventId());
        assertEquals(AuditEvent.LOADED_LOGIN.getName(), audits.get(0).getEventName());
        assertEquals(AuditEvent.LOADED_LOGIN.getType(), audits.get(0).getEventType());
        assertEquals(Components.REGISTRATION.getId(), audits.get(0).getModuleId());
        assertEquals(Components.REGISTRATION.getName(), audits.get(0).getModuleName());
    }

    @Test
    public void auditWithoutComponent_test() {
        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
        auditManagerService.audit(AuditEvent.NEXT_BUTTON_CLICKED, Components.REGISTRATION.getId(), String.format(RegistrationConstants.REGISTRATION_SCREEN, SCREEN_NAME));

        List<Audit> audits = auditRepository.getAuditsFromDate(System.currentTimeMillis() - 5000);

        assertEquals(1, audits.size());
        assertEquals(USER_NAME, audits.get(0).getSessionUserId());
        assertEquals(USER_NAME, audits.get(0).getSessionUserName());
        assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostIp());
        assertEquals(BuildConfig.BASE_URL, audits.get(0).getHostName());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationId());
        assertEquals(appContext.getString(R.string.app_name), audits.get(0).getApplicationName());
        assertEquals(USER_NAME, audits.get(0).getRefId());
        assertEquals(AuditReferenceIdTypes.USER_ID.name(), audits.get(0).getRefIdType());
        assertEquals(AuditEvent.NEXT_BUTTON_CLICKED.getId(), audits.get(0).getEventId());
        assertEquals(AuditEvent.NEXT_BUTTON_CLICKED.getName(), audits.get(0).getEventName());
        assertEquals(AuditEvent.NEXT_BUTTON_CLICKED.getType(), audits.get(0).getEventType());
        assertEquals(Components.REGISTRATION.getId(), audits.get(0).getModuleId());
        assertEquals(String.format(RegistrationConstants.REGISTRATION_SCREEN, SCREEN_NAME), audits.get(0).getModuleName());
    }
}
