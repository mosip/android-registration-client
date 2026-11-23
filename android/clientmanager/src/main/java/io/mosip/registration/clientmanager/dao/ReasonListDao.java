package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.mosip.registration.clientmanager.entity.ReasonList;

@Dao
public abstract class ReasonListDao {
    // One row per code: older installs can still hold duplicates re-inserted by earlier syncs
    @Query("select * from reason_list WHERE lang_code = :langCode AND id IN " +
            "(select max(id) from reason_list WHERE lang_code = :langCode group by code)")
    public abstract List<ReasonList> getAllReasonList(String langCode);

    @Insert(entity = ReasonList.class, onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ReasonList reasonList);

    @Query("UPDATE reason_list SET name = :name, description = :description WHERE code = :code AND lang_code = :langCode")
    public abstract int updateByCodeAndLangCode(String code, String langCode, String name, String description);

    // id is auto-generated, so REPLACE never conflicts; update by code+lang and insert only when absent
    @Transaction
    public void upsert(ReasonList reasonList) {
        int updated = updateByCodeAndLangCode(reasonList.getCode(), reasonList.getLangCode(),
                reasonList.getName(), reasonList.getDescription());
        if (updated == 0) {
            insert(reasonList);
        }
    }
}
