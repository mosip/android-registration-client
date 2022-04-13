package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.IdentitySchema;

@Dao
public interface IdentitySchemaDao {

    @Query("select * from identity_schema order by schema_version desc limit 1")
    IdentitySchema findLatestSchema();

    @Query("select * from identity_schema where schema_version = :schemaVer")
    IdentitySchema findIdentitySchema(Double schemaVer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIdentitySchema(IdentitySchema identitySchema);

}
