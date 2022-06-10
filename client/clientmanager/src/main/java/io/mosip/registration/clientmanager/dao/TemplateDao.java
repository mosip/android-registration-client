package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.Template;

import java.util.List;

@Dao
public interface TemplateDao {

    @Query("select file_txt from template where template_type_code like :templateTypeCode and lang_code = :langCode and is_active=1")
    List<String> findAllTemplateText(String templateTypeCode, String langCode);

    @Query("select file_txt from template where name like :templateName and template_type_code like :templateTypeCode and lang_code = :langCode and is_active=1")
    List<String> findPreviewTemplateText(String templateName, String templateTypeCode, String langCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Template template);
}
