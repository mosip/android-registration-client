package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import io.mosip.registration.clientmanager.entity.JobTransaction;
import io.mosip.registration.clientmanager.repository.JobTransactionRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class JobTransactionServiceImplTest {

    @Mock
    private JobTransactionRepository jobTransactionRepository;

    @InjectMocks
    private JobTransactionServiceImpl jobTransactionService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    public void logJobTransaction_WhenJobTransactionIsNotFound_Test() {

        int jobId = 1;
        long syncTime = 1000L;

        when(jobTransactionRepository.getJobTransaction(jobId)).thenReturn(null); // JobTransaction not found

        jobTransactionService.LogJobTransaction(jobId, syncTime);

        verify(jobTransactionRepository).createJobTransaction(any(JobTransaction.class)); // Verifying createJobTransaction is called
    }

    @Test
    public void logJobTransaction_WhenJobTransactionIsFound_Test() {

        int jobId = 1;
        long syncTime = 1000L;
        JobTransaction existingJobTransaction = new JobTransaction(jobId, 0L);

        when(jobTransactionRepository.getJobTransaction(jobId)).thenReturn(existingJobTransaction); // JobTransaction found

        jobTransactionService.LogJobTransaction(jobId, syncTime);

        verify(jobTransactionRepository).updateJobTransaction(jobId, syncTime); // Verifying updateJobTransaction is called
    }

    @Test
    public void logJobTransaction_WhenJobTransactionIsNull_Test() {

        int jobId = 2;
        long syncTime = 2000L;

        when(jobTransactionRepository.getJobTransaction(jobId)).thenReturn(null); // No transaction found

        jobTransactionService.LogJobTransaction(jobId, syncTime);

        verify(jobTransactionRepository, times(1)).createJobTransaction(any(JobTransaction.class)); // Ensure create is called once
    }

    @Test
    public void getLastSyncTime_WhenJobTransactionDoesNotExist_Test() {

        int jobId = 1;
        when(jobTransactionRepository.getJobTransaction(jobId)).thenReturn(null);

        long actualLastSyncTime = jobTransactionService.getLastSyncTime(jobId);

        assertEquals(0, actualLastSyncTime);
        verify(jobTransactionRepository, times(1)).getJobTransaction(jobId);  // Ensure repository method was called once
    }
}
