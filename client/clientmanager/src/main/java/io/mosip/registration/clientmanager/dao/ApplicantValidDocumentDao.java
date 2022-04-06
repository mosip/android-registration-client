package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;

@Dao
public interface ApplicantValidDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ApplicantValidDocument applicantValidDocument);
}
