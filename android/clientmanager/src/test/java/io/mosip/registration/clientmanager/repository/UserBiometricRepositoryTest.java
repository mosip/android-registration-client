package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.UserBiometricDao;
import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.entity.UserBiometric;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BDBInfo;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.QualityType;

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
public class UserBiometricRepositoryTest {

    @Mock
    private UserBiometricDao userBiometricDao;

    @Mock
    private UserDetailDao userDetailDao;

    private UserBiometricRepository userBiometricRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userBiometricRepository = new UserBiometricRepository(userBiometricDao, userDetailDao);
    }

    @Test
    public void testInsertExtractedTemplates() {
        // Mock BIR and its components
        BIR birMock = mock(BIR.class);
        BDBInfo bdbInfoMock = mock(BDBInfo.class);
        QualityType qualityMock = mock(QualityType.class);

        // Define behavior for mocks
        when(birMock.getBdbInfo()).thenReturn(bdbInfoMock);
        when(bdbInfoMock.getSubtype()).thenReturn("face");
        when(bdbInfoMock.getQuality()).thenReturn(qualityMock);
        when(qualityMock.getScore()).thenReturn(80L);

        when(birMock.getBdb()).thenReturn(new byte[]{1, 2, 3});

        List<BIR> birList = Arrays.asList(birMock);

        String response = userBiometricRepository.insertExtractedTemplates(birList, "9343");

        ArgumentCaptor<List<UserBiometric>> captor = ArgumentCaptor.forClass(List.class);
        verify(userBiometricDao).insertAllUserBiometrics(captor.capture());

        List<UserBiometric> capturedBiometrics = captor.getValue();
        assertEquals(1, capturedBiometrics.size());
        assertEquals("face", capturedBiometrics.get(0).getBioAttributeCode());
        assertEquals(80, capturedBiometrics.get(0).getQualityScore().intValue());

        assertEquals("Success", response);
    }

    @Test
    public void testFindAllOperatorBiometrics() {

        UserBiometric bioMock = new UserBiometric();
        bioMock.setUsrId("9343");
        bioMock.setBioTypeCode("Face");

        List<UserBiometric> mockList = Arrays.asList(bioMock);
        when(userBiometricDao.findAll("Face")).thenReturn(mockList);

        List<UserBiometric> result = userBiometricRepository.findAllOperatorBiometrics("Face");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("9343", result.get(0).getUsrId());
    }

    @Test
    public void testSaveOnboardStatus_UserExists() {
        UserDetail mockUser = new UserDetail("9343");
        when(userDetailDao.getUserDetail("9343")).thenReturn(mockUser);

        String response = userBiometricRepository.saveOnboardStatus("9343");

        verify(userDetailDao).updateUserDetail(eq(true), eq("9343"), anyLong());
        assertEquals("Success", response);
    }

    @Test
    public void testSaveOnboardStatus_UserNotFound() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        String response = userBiometricRepository.saveOnboardStatus("9343");

        verify(userDetailDao, never()).updateUserDetail(anyBoolean(), anyString(), anyLong());
        assertEquals("Success", response);
    }
}
