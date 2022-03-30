package io.mosip.registration.clientmanager.entity;

import java.sql.Timestamp;

public interface RegistrationAuditDates {
    Timestamp getAuditLogFromDateTime();

    Timestamp getAuditLogToDateTime();
}
