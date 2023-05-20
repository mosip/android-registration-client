package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;

@Data
@Entity(primaryKeys = {"code", "lang_code"}, tableName = "document_type")
public class DocumentType {

    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name="name")
    private String name;

    @ColumnInfo(name="description")
    private String description;

    @ColumnInfo(name="is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name="is_active")
    private Boolean isActive;
}
