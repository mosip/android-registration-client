package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Optional;

import io.mosip.registration.clientmanager.entity.FileSignature;

@Dao
public interface FileSignatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FileSignature fileSignature);

    @Query("select * from file_signature where file_name = :fileName")
    Optional<FileSignature> findByFileName(String fileName);
}
