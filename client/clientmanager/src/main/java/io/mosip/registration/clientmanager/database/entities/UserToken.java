package io.mosip.registration.clientmanager.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
public class UserToken {
    @PrimaryKey
    @NonNull
    public String id;
    public String version;
    public String responseTime;
    public String metadata;
    public String response;
    public String errors;
}


