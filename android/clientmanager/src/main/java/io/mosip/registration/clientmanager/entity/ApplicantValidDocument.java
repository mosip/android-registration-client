package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;

@Data
@Entity(primaryKeys = {"app_type_code", "doc_type_code", "doc_cat_code"}, tableName = "applicant_valid_doc")
public class ApplicantValidDocument {

    @NonNull
    @ColumnInfo(name = "app_type_code")
    private String appTypeCode;

    @NonNull
    @ColumnInfo(name = "doc_type_code")
    private String docTypeCode;

    @NonNull
    @ColumnInfo(name = "doc_cat_code")
    private String docCatCode;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;
}
