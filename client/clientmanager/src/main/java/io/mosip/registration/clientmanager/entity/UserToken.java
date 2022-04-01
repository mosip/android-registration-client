package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity(tableName = "user_token")
@Data
@AllArgsConstructor
public class UserToken {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

    @ColumnInfo(name = "t_expiry")
    private long tExpiry;

    @ColumnInfo(name = "r_expiry")
    private long rExpiry;
}


