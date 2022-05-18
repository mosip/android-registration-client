package io.mosip.registration.keymanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.mosip.registration.keymanager.entity.CACertificateStore;

import java.util.List;

@Dao
public interface CACertificateStoreDao {

    @Query("select * from ca_cert_store c where c.cert_thumbprint = :arg0 and partner_domain = :arg1")
    CACertificateStore findByCertThumbprintAndPartnerDomain(String arg0, String arg1);


    @Query("select * from ca_cert_store c where c.partner_domain = :arg0")
    List<CACertificateStore> findByPartnerDomain(String arg0);

    @Query("select * from ca_cert_store c where c.cert_subject = :arg0")
    List<CACertificateStore> findByCertSubject(String arg0);

    @Insert()
    void insert(CACertificateStore caCertificateStore);
}
