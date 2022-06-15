package io.mosip.registration.clientmanager.spi;

import java.sql.Timestamp;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */
public interface JobTransactionService {

	void LogJobTransaction(int jobId, long syncTime);

	long getLastSyncTime(int jobId);

}