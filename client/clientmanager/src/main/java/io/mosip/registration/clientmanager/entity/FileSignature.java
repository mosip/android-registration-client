package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * @author Anshul vanawat
 */

@Entity(tableName = "file_signature")
@Data
public class FileSignature {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "signature")
    private String signature;

    @ColumnInfo(name = "content_length")
    private Integer contentLength;

    @ColumnInfo(name = "encrypted")
    private Boolean encrypted;
}
