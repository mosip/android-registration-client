package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "process_spec")
public class ProcessSpec {
    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "id_version")
    private double idVersion;

    @ColumnInfo(name = "order_num")
    private int orderNum;

    @ColumnInfo(name = "is_sub_process")
    private boolean isSubProcess;

    @ColumnInfo(name= "flow")
    private String flow;

    @ColumnInfo(name = "is_active")
    private boolean isActive;
}
