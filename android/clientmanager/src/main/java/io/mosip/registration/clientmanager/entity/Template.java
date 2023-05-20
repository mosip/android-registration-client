package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;

@Entity(primaryKeys = {"id", "lang_code"}, tableName = "template")
@Data
public class Template {

    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "file_format_code")
    private String fileFormatCode;
    @ColumnInfo(name = "model")
    private String model;
    @ColumnInfo(name = "file_txt")
    private String fileText;
    @ColumnInfo(name = "module_id")
    private String moduleId;
    @ColumnInfo(name = "module_name")
    private String moduleName;
    @ColumnInfo(name = "template_type_code")
    private String templateTypeCode;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="is_active")
    private Boolean isActive;
    @ColumnInfo(name="is_deleted")
    private Boolean isDeleted;
}
