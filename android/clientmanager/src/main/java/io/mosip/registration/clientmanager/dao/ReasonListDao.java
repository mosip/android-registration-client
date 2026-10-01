package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.ReasonList;

@Dao
public interface ReasonListDao {
    @Query("select * from reason_list WHERE lang_code = :langCode")
    List<ReasonList> getAllReasonList(String langCode);

    @Insert(entity = ReasonList.class, onConflict = OnConflictStrategy.REPLACE)
    void insert(ReasonList reasonList);

    @Query("DELETE FROM reason_list WHERE code = :code AND lang_code = :langCode")
    void deleteByCodeAndLangCode(String code, String langCode);

}
