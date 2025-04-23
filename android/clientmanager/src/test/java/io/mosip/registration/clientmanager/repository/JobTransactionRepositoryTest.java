package io.mosip.registration.clientmanager.repository;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.JobTransactionDao;
import io.mosip.registration.clientmanager.entity.JobTransaction;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JobTransactionRepositoryTest {

    private Context appContext;
    private ClientDatabase clientDatabase;
    private JobTransactionRepository jobTransactionRepository;
    private JobTransactionDao jobTransactionDao;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();
        jobTransactionDao = clientDatabase.jobTransactionDao();
        jobTransactionRepository = new JobTransactionRepository(jobTransactionDao);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void testCreateJobTransaction() {
        JobTransaction jobTransaction = new JobTransaction(1, 123456789L);
        jobTransaction.setJobId(1);
        jobTransaction.setLastSyncTime(123456789L);

        jobTransactionRepository.createJobTransaction(jobTransaction);

        List<JobTransaction> transactions = jobTransactionRepository.getAllTransactions();
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(1, transactions.get(0).getJobId());
        Assert.assertEquals(123456789L, (long) transactions.get(0).getLastSyncTime());
    }

    @Test
    public void testGetJobTransaction() {
        JobTransaction jobTransaction = new JobTransaction(2, 987654321L);
        jobTransaction.setJobId(2);
        jobTransaction.setLastSyncTime(987654321L);
        jobTransactionRepository.createJobTransaction(jobTransaction);

        JobTransaction retrievedTransaction = jobTransactionRepository.getJobTransaction(2);
        Assert.assertNotNull(retrievedTransaction);
        Assert.assertEquals(2, retrievedTransaction.getJobId());
        Assert.assertEquals(987654321L, (long) retrievedTransaction.getLastSyncTime());
    }

    @Test
    public void testUpdateJobTransaction() {
        JobTransaction jobTransaction = new JobTransaction(3, 111111111L);
        jobTransaction.setJobId(3);
        jobTransaction.setLastSyncTime(111111111L);
        jobTransactionRepository.createJobTransaction(jobTransaction);

        jobTransactionRepository.updateJobTransaction(3, 222222222L);
        JobTransaction updatedTransaction = jobTransactionRepository.getJobTransaction(3);

        Assert.assertNotNull(updatedTransaction);
        Assert.assertEquals(3, updatedTransaction.getJobId());
        Assert.assertEquals(222222222L, (long) updatedTransaction.getLastSyncTime());
    }
}
