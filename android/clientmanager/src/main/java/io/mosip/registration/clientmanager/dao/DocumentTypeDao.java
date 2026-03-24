package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.DocumentType;

import java.util.List;

@Dao
public interface DocumentTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DocumentType documentType);

    @Query("select * from document_type where code=:code and lang_code=:langCode limit 1")
    DocumentType findByCodeAndLangCode(String code, String langCode);

    @Query("select * from document_type where code=:code limit 1")
    List<DocumentType> findByCode(String code);
}
