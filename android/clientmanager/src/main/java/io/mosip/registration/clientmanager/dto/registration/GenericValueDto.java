package io.mosip.registration.clientmanager.dto.registration;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericValueDto {

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "code")
    @NonNull
    private String code;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @Override
    public String toString() {
        return getName();
    }
}
