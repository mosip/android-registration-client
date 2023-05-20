package io.mosip.registration.keymanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "key_store")
public class KeyStore {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "alias")
    private String alias;

    @ColumnInfo(name = "master_alias")
    private String masterAlias;

    @ColumnInfo(name = "certificate_data")
    private String certificateData;

    @ColumnInfo(name = "created_on")
    private Long createdOn;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

}