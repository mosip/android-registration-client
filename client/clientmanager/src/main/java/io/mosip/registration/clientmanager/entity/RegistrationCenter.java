package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.sql.Timestamp;

import io.mosip.registration.clientmanager.entity.id.RegistrationCenterId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for Registration Center details
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "registration_center")
@Data
public class RegistrationCenter extends RegistrationCommonFields {

    @Embedded
    @PrimaryKey
    private RegistrationCenterId registrationCenterId;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "cntrtyp_code")
    private String centerTypeCode;
    @ColumnInfo(name = "addr_line1")
    private String addressLine1;
    @ColumnInfo(name = "addr_line2")
    private String addressLine2;
    @ColumnInfo(name = "addr_line3")
    private String addressLine3;
    @ColumnInfo(name = "latitude")
    private String latitude;
    @ColumnInfo(name = "longitude")
    private String longitude;
    @ColumnInfo(name = "location_Code")
    private String locationCode;
    @ColumnInfo(name = "contact_phone")
    private String contactPhone;
    @ColumnInfo(name = "contact_person")
    private String contactPerson;
    @ColumnInfo(name = "number_of_kiosks")
    private Integer numberOfKiosks;
    @ColumnInfo(name = "working_hours")
    private String workingHours;
    @ColumnInfo(name = "per_kiosk_process_time")
    private Time perKioskProcessTime;
    @ColumnInfo(name = "center_start_time")
    private Time centerStartTime;
    @ColumnInfo(name = "center_end_time")
    private Time centerEndTime;
    @ColumnInfo(name = "lunch_start_time")
    private Time lunchStartTime;
    @ColumnInfo(name = "lunch_end_time")
    private Time lunchEndTime;
    @ColumnInfo(name = "time_zone")
    private String timeZone;
    @ColumnInfo(name = "holiday_loc_code")
    private String holidayLocationCode;
    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;
    @ColumnInfo(name = "del_dtimes")
    private Timestamp delDtimes;

}
