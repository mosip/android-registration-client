package io.mosip.registration.clientmanager.repository;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.dao.ReasonListDao;
import io.mosip.registration.clientmanager.entity.ReasonList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReasonListRepositoryTest {

    @Mock
    private ReasonListDao reasonListDao;

    private ReasonListRepository reasonListRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reasonListRepository = new ReasonListRepository(reasonListDao);
    }

    @Test
    public void testGetAllReasonList() {
        // Create a ReasonList object using setter methods
        ReasonList reason = new ReasonList();
        reason.setCode("DEMO");
        reason.setName("InvalidData");
        reason.setLangCode("en");
        reason.setDescription("Data Invalid");

        List<ReasonList> mockReasons = Arrays.asList(reason);
        when(reasonListDao.getAllReasonList("en")).thenReturn(mockReasons);

        // Perform test
        List<ReasonList> result = reasonListRepository.getAllReasonList("en");

        // Assertions
        assertEquals(1, result.size());
        assertEquals("DEMO", result.get(0).getCode());
        assertEquals("InvalidData", result.get(0).getName());
        assertEquals("en", result.get(0).getLangCode());
        assertEquals("Data Invalid", result.get(0).getDescription());
    }

    @Test
    public void testSaveReasonList() throws Exception {
        // Mocking JSON object
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getString("code")).thenReturn("DEMO");
        when(mockJson.getString("name")).thenReturn("InvalidData");
        when(mockJson.getString("langCode")).thenReturn("en");
        when(mockJson.getString("description")).thenReturn("Data Invalid");

        // Perform save operation
        reasonListRepository.saveReasonList(mockJson);

        // Capture the saved ReasonList object
        ArgumentCaptor<ReasonList> captor = ArgumentCaptor.forClass(ReasonList.class);
        verify(reasonListDao, times(1)).insert(captor.capture());

        // Retrieve captured value
        ReasonList capturedReason = captor.getValue();

        // Assertions
        assertEquals("DEMO", capturedReason.getCode());
        assertEquals("InvalidData", capturedReason.getName());
        assertEquals("en", capturedReason.getLangCode());
        assertEquals("Data Invalid", capturedReason.getDescription());
    }
}
