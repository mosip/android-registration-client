package io.mosip.registration.clientmanager.entity.id;

import androidx.room.ColumnInfo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CodeLanguageId implements Serializable {

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "lang_code")
    private String langCode;
}
