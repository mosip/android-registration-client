package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * The Entity class for sync job transactions
 *
 * @author Anshul vanawat
 */

@Entity(tableName = "sync_job_transaction")
@Data
public class SyncJobTransaction {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "job_id")
    private String jobId;
    @ColumnInfo(name = "last_sync_time")
    private Long syncTime;
}
