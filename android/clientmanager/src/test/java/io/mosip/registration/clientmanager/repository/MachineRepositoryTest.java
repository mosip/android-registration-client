package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.MachineMasterDao;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MachineRepositoryTest {

    @Mock
    private MachineMasterDao machineMasterDao;

    private MachineRepository machineRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        machineRepository = new MachineRepository(machineMasterDao);
    }

    @Test
    public void testGetMachine() {
        MachineMaster mockMachine = new MachineMaster("mockName");
        when(machineMasterDao.findMachineByName("mockName")).thenReturn(mockMachine);

        MachineMaster result = machineRepository.getMachine("mockName");
        assertNotNull(result);
        assertEquals("mockName", result.getId());
    }

    @Test
    public void testSaveMachineMaster() throws Exception {
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getString("id")).thenReturn("mockName");
        when(mockJson.getBoolean("isActive")).thenReturn(true);
        when(mockJson.getString("name")).thenReturn("Test Machine");
        when(mockJson.has("regCenterId")).thenReturn(true);
        when(mockJson.getString("regCenterId")).thenReturn("RC001");

        machineRepository.saveMachineMaster(mockJson);

        ArgumentCaptor<MachineMaster> captor = ArgumentCaptor.forClass(MachineMaster.class);
        verify(machineMasterDao, times(1)).insert(captor.capture());

        MachineMaster capturedMachine = captor.getValue();
        assertEquals("mockName", capturedMachine.getId());
        assertTrue(capturedMachine.getIsActive());
        assertEquals("Test Machine", capturedMachine.getName());
        assertEquals("RC001", capturedMachine.getRegCenterId());
        assertNull(capturedMachine.getValidityDateTime()); //TODO: validityDateTime is null as expected
    }

    @Test
    public void testUpdateMachine() {
        machineRepository.updateMachine("mockName", "RC002");
        verify(machineMasterDao, times(1)).updateMachine("mockName", "RC002");
    }
}
