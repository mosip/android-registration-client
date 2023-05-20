package io.mosip.registration.keymanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "ca_cert_store")
public class CACertificateStore {

    /**
     * The field cert_id
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "cert_id")
    private String certId;

    /**
     * The field cert_id
     */
    @ColumnInfo(name = "cert_subject")
    private String certSubject;

    /**
     * The field cert_issuer
     */
    @ColumnInfo(name = "cert_issuer")
    private String certIssuer;

    /**
     * The field issuer_id
     */
    @ColumnInfo(name = "issuer_id")
    private String issuerId;

    /**
     * The field cert_not_nefore
     */
    @ColumnInfo(name = "cert_not_before")
    private Long certNotBefore;

    /**
     * The field cert_not_after
     */
    @ColumnInfo(name = "cert_not_after")
    private Long certNotAfter;

    /**
     * The field crl_uri
     */
    @ColumnInfo(name = "crl_uri")
    private String crlUri;

    /**
     * The field cert_data
     */
    @ColumnInfo(name = "cert_data")
    private String certData;

    /**
     * The field cert_thumbprint
     */
    @ColumnInfo(name = "cert_thumbprint")
    private String certThumbprint;

    /**
     * The field cert_serial_no
     */
    @ColumnInfo(name = "cert_serial_no")
    private String certSerialNo;

    /**
     * The field partner_domain
     */
    @ColumnInfo(name = "partner_domain")
    private String partnerDomain;

    @ColumnInfo(name = "cr_by")
    private String createdBy;

    /**
     * The field createdtimes
     */
    @ColumnInfo(name = "cr_dtimes")
    private Long createdtimes;

    /**
     * The field updatedBy
     */
    @ColumnInfo(name = "upd_by")
    private String updatedBy;

    /**
     * The field updatedtimes
     */
    @ColumnInfo(name = "upd_dtimes")
    private Long updatedtimes;

    /**
     * The field isDeleted
     */
    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * The field deletedtimes
     */
    @ColumnInfo(name = "del_dtimes")
    private Long deletedtimes;

}
