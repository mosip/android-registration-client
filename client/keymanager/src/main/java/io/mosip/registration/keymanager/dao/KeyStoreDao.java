package io.mosip.registration.keymanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.keymanager.entity.KeyStore;

@Dao
public interface KeyStoreDao {

    @Query("select * from key_store k where k.alias = :arg0")
    KeyStore findOneKeyStoreByAlias(String arg0);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(KeyStore keyStore);
}
