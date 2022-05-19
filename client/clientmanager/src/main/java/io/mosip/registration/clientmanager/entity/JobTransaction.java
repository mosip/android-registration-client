package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Entity class for sync job transactions
 *
 * @author Anshul vanawat
 */

@Entity(tableName = "job_transaction")
@Data
@AllArgsConstructor
public class JobTransaction {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "job_id")
    private int jobId;
    @ColumnInfo(name = "last_sync_time")
    private Long lastSyncTime;
}
