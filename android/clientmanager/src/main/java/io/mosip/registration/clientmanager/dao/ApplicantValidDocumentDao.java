package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;

import java.util.List;

@Dao
public interface ApplicantValidDocumentDao {

    @Query("select doc_type_code from applicant_valid_doc where app_type_code=:applicantTypeCode and doc_cat_code=:docCategoryCode")
    List<String> findAllDocTypesByDocCategoryAndApplicantType(String applicantTypeCode, String docCategoryCode);

    @Query("select distinct doc_type_code from applicant_valid_doc where doc_cat_code=:docCategoryCode")
    List<String> findAllDocTypesByDocCategory(String docCategoryCode);

    @Query("select name from document_type where code=:code and lang_code=:langCode")
    List<String> findAllDocTypesByLanguageCode(String code,String langCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ApplicantValidDocument applicantValidDocument);

    @Query("select name from document_type where code=:code")
    List<String> findAllDocTypesByCode(String code);
}
