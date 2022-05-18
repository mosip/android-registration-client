package io.mosip.registration.clientmanager.spi;

import java.sql.Timestamp;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */
public interface SyncJobTransactionService {

	void LogJobSyncTransaction(String JobId, Timestamp syncTime);

}