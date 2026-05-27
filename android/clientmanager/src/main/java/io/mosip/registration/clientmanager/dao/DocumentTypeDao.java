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

    @Query("select * from document_type where code in (:codes) and lang_code in (:langCodes)")
    List<DocumentType> findDocumentTypesByCodesAndLangCodes(List<String> codes, List<String> langCodes);
}
