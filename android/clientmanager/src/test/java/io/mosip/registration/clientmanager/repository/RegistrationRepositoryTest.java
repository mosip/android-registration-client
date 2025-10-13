package io.mosip.registration.clientmanager.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.entity.Registration;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationRepositoryTest {

    @Mock
    private RegistrationDao registrationDao;

    @Mock
    private ObjectMapper objectMapper;

    private RegistrationRepository registrationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registrationRepository = new RegistrationRepository(registrationDao, objectMapper);
    }

    @Test
    public void testGetAllRegistrations() {
        // Mock data
        Registration reg1 = new Registration("10001111491003120250217081657");
        Registration reg2 = new Registration("10001155851003120250220055513");

        List<Registration> mockList = Arrays.asList(reg1, reg2);
        when(registrationDao.findAll()).thenReturn(mockList);

        // Execute method
        List<Registration> result = registrationRepository.getAllRegistrations();

        // Assertions
        assertEquals(2, result.size());
        assertEquals("10001111491003120250217081657", result.get(0).getPacketId());
        assertEquals("10001155851003120250220055513", result.get(1).getPacketId());
    }

    @Test
    public void testGetAllNotUploadedRegistrations() {
        Registration reg = new Registration("10001155851003120250220055513");
        List<Registration> mockList = Arrays.asList(reg);
        when(registrationDao.findAllNotUploaded()).thenReturn(mockList);

        List<Registration> result = registrationRepository.getAllNotUploadedRegistrations();

        assertEquals(1, result.size());
        assertEquals("10001155851003120250220055513", result.get(0).getPacketId());
    }

    @Test
    public void testGetRegistrationsByStatus() {
        Registration reg = new Registration("10001155851003120250220055513");
        List<Registration> mockList = Arrays.asList(reg);
        when(registrationDao.findRegistrationByStatus("CREATED", 10)).thenReturn(mockList);

        List<Registration> result = registrationRepository.getRegistrationsByStatus("CREATED", 10);

        assertEquals(1, result.size());
        assertEquals("10001155851003120250220055513", result.get(0).getPacketId());
    }

    @Test
    public void testGetAllRegistrationByStatus() {
        when(registrationDao.findAllRegistrationByStatus("CREATED")).thenReturn(5);

        int count = registrationRepository.getAllRegistrationByStatus("CREATED");

        assertEquals(5, count);
    }

    @Test
    public void testGetRegistration() {
        Registration reg = new Registration("10001155851003120250220055513");
        when(registrationDao.findOneByPacketId("10001155851003120250220055513")).thenReturn(reg);

        Registration result = registrationRepository.getRegistration("10001155851003120250220055513");

        assertNotNull(result);
        assertEquals("10001155851003120250220055513", result.getPacketId());
    }

    @Test
    public void testUpdateServerStatus() {
        registrationRepository.updateServerStatus("10001155851003120250220055513", "APPROVED");

        verify(registrationDao, times(1)).updateServerStatus("10001155851003120250220055513", "APPROVED");
    }

    @Test
    public void testUpdateStatus() {
        registrationRepository.updateStatus("10001155851003120250220055513", "APPROVED", "COMPLETED");

        verify(registrationDao, times(1)).updateStatus("10001155851003120250220055513", "COMPLETED", "APPROVED");
    }

    @Test
    public void testInsertRegistration() throws Exception {
        // Mock JSON object
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.toString()).thenReturn("{\"key\":\"value\"}");

        // Execute method
        Registration result = registrationRepository.insertRegistration("10001155851003120250220055513", "/path/to/container",
                "10011", "NEW", mockJson, "");

        // Capture inserted object
        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);
        verify(registrationDao, times(1)).insert(captor.capture());

        Registration capturedReg = captor.getValue();

        // Assertions
        assertEquals("10001155851003120250220055513", capturedReg.getPacketId());
        assertEquals("/path/to/container", capturedReg.getFilePath());
        assertEquals("NEW", capturedReg.getRegType());
        assertEquals("10011", capturedReg.getCenterId());
        assertEquals(PacketClientStatus.CREATED.name(), capturedReg.getClientStatus());
        assertNull(capturedReg.getServerStatus());
        assertEquals("110006", capturedReg.getCrBy());
        assertNotNull(capturedReg.getCrDtime());
        assertArrayEquals(mockJson.toString().getBytes(StandardCharsets.UTF_8), capturedReg.getAdditionalInfo());
    }

    @Test
    public void testDeleteRegistration() {
        registrationRepository.deleteRegistration("10001155851003120250220055513");

        verify(registrationDao, times(1)).delete("10001155851003120250220055513");
    }

    @Test
    public void testUpdateSupervisorReview() {
        registrationRepository.updateSupervisorReview("10001155851003120250220055513", "APPROVED", "All good");

        verify(registrationDao, times(1)).updateSupervisorReview("10001155851003120250220055513", "APPROVED", "All good");
    }
}
