package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.BlocklistedWord;

import java.util.List;

@Dao
public interface BlocklistedWordDao {

    @Query("select * from blocklisted_word where lower(:inputValue) like ('%' || lower(word) || '%')")
    List<BlocklistedWord> findAllBlocklistedWords(String inputValue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlocklistedWord blocklistedWord);

}
