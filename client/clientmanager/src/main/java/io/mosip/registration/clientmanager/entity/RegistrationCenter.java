package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;

/**
 * The Entity Class for Registration Center details
 *
 * @author Anshul Vanawat
 */
@Entity(primaryKeys = {"id", "lang_code"}, tableName = "registration_center")
@Data
public class RegistrationCenter {

    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "center_type_code")
    private String centerTypeCode;

    @ColumnInfo(name = "addressLine1")
    private String addressLine1;

    @ColumnInfo(name = "addressLine2")
    private String addressLine2;

    @ColumnInfo(name = "addressLine3")
    private String addressLine3;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "location_code")
    private String locationCode;

    @ColumnInfo(name = "holiday_loc_code")
    private String holidayLocationCode;

    @ColumnInfo(name = "contact_phone")
    private String contactPhone;

    @ColumnInfo(name = "working_hours")
    private String workingHours;

    @ColumnInfo(name = "kiosk_count")
    private Integer numberOfKiosks;

    @ColumnInfo(name = "per_kiosk_process_time")
    private String perKioskProcessTime;

    @ColumnInfo(name = "center_start_time")
    private String centerStartTime;

    @ColumnInfo(name = "center_end_time")
    private String centerEndTime;

    @ColumnInfo(name = "time_zone")
    private String timeZone;

    @ColumnInfo(name = "contact_person")
    private String contactPerson;

    @ColumnInfo(name = "lunch_start_time")
    private String lunchStartTime;

    @ColumnInfo(name = "lunch_end_time")
    private String lunchEndTime;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;
}
