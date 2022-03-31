package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class UserToken {
    @PrimaryKey
    @NonNull
    public String id;
    public String version;
    public String responsetime;
    public String metadata;
    public String response;
    public String errors;
}


