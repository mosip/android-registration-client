package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceImplTest {

    @Mock
    private Context mockApplicationContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private MasterDataService masterDataService;
    @Mock
    private IdentitySchemaRepository identitySchemaRepository;
    @Mock
    private KeyStoreRepository keyStoreRepository;
    @Mock
    private GlobalParamRepository globalParamRepository;
    @Mock
    private PacketWriterService packetWriterService;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private AuditManagerService auditManagerService;
    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;
    private RegistrationService registrationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mockSharedPreferences.edit()).thenReturn(editor);
        when(mockApplicationContext.getString(anyInt())).thenReturn("Registration Client");
        when(mockApplicationContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        registrationService = new RegistrationServiceImpl(mockApplicationContext, packetWriterService,
                registrationRepository, masterDataService, identitySchemaRepository, clientCryptoManagerService,
                keyStoreRepository, globalParamRepository, auditManagerService);
    }

    @Test
    public void approveRegistration() {
        //Not Implemented
    }

    @Test
    public void rejectRegistration() {
        //Not Implemented
    }


    @Test(expected = ClientCheckedException.class)
    public void getRegistrationDtoWithoutStartingRegistration() throws Exception {
        registrationService.getRegistrationDto();
    }

    @Test
    public void getRegistrationDtoAfterStartingRegistration() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        RegistrationDto registrationDto = registrationService.startRegistration(Arrays.asList("eng"));
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());
    }

    @Test(expected = ClientCheckedException.class)
    public void submitRegistrationDtoWithoutStartingRegistration() throws Exception {
        registrationService.submitRegistrationDto("100006");
    }

    @Test(expected = ClientCheckedException.class)
    public void startAndSubmitRegistration() throws Exception {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mockSharedPreferences.edit()).thenReturn(editor);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        RegistrationDto registrationDto = registrationService.startRegistration(selectedLanguages);
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());

        registrationService.submitRegistrationDto("100006");
    }

    @Test(expected = ClientCheckedException.class)
    public void startRegistrationWithoutMasterSync_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        registrationService.startRegistration(selectedLanguages);
    }

    @Test(expected = ClientCheckedException.class)
    public void startRegistrationWithoutIDSchema_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        registrationService.startRegistration(selectedLanguages);
    }

    @Test(expected = ClientCheckedException.class)
    public void startRegistrationWithoutPolicyKey_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages);
    }

    @Test(expected = ClientCheckedException.class)
    public void startRegistrationInactiveCenter_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(false);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages);
    }

    @Test(expected = ClientCheckedException.class)
    public void startRegistrationInactiveMachine_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(false);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages);
    }

    @Test(expected = ClientCheckedException.class)
    public void clearRegistration() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        List<String> languages = new ArrayList<>();
        languages.add("eng");
        RegistrationDto registrationDto = registrationService.startRegistration(languages);
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());

        registrationService.clearRegistration();
        registrationService.getRegistrationDto();
    }

    @Test
    public void getAudits() {
        //TODO
    }

    @Test
    public void buildBIR() {
        //TODO
    }
}