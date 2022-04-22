package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class to store registration block listed words
 */
@Entity(tableName = "blocklisted_word")
@Data
@EqualsAndHashCode(callSuper=false)
public class BlocklistedWord {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;
}
