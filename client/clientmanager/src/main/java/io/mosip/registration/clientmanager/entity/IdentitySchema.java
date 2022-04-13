package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "identity_schema")
public class IdentitySchema {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name="id")
    private String id;

    @ColumnInfo(name = "schema_version")
    private Double schemaVersion;

    @ColumnInfo(name = "file_length")
    private Long fileLength;

    @ColumnInfo(name = "file_hash")
    private String fileHash;

}
