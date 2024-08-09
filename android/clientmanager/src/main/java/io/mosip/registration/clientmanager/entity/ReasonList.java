package io.mosip.registration.clientmanager.entity;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This Entity Class contains list of reasons [Invalid Address, Gender-Photo Mismatch...]
 * that are listed during Registration Approval/Rejection with respect to language code .
 * The data for this table will come through sync from server master table.
 */

@Entity(tableName = "reason_list")
@Data
@NoArgsConstructor
public class ReasonList implements Serializable {
    private static final long serialVersionUID = -572990183711593868L;

    @PrimaryKey(autoGenerate = true)
    int id;

    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;
}
