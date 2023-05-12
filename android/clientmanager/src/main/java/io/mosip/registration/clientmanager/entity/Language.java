package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "language")
public class Language {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "native_name")
    private String nativeName;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;
}
