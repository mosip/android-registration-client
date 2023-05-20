package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.Language;

import java.util.List;

@Dao
public interface LanguageDao {


    @Query("select native_name from language where code = :code and is_active=1")
    String getNativeName(String code);

    @Query("select * from language where is_active=1")
    List<Language> getAllActiveLanguage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLanguage(Language language);
}
