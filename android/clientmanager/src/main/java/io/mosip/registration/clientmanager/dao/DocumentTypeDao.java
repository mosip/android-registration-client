package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import io.mosip.registration.clientmanager.entity.DocumentType;

@Dao
public interface DocumentTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DocumentType documentType);
}
