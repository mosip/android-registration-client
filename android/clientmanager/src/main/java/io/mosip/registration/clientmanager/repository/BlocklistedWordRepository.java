package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.BlocklistedWordDao;
import io.mosip.registration.clientmanager.entity.BlocklistedWord;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class BlocklistedWordRepository {

    private BlocklistedWordDao blocklistedWordDao;

    @Inject
    public BlocklistedWordRepository(BlocklistedWordDao blocklistedWordDao) {
        this.blocklistedWordDao = blocklistedWordDao;
    }

    public void saveBlocklistedWord(JSONObject jsonObject) throws JSONException {
        BlocklistedWord blocklistedWord = new BlocklistedWord(jsonObject.getString("word"));
        blocklistedWord.setIsActive(jsonObject.getBoolean("isActive"));
        blocklistedWordDao.insert(blocklistedWord);
    }
}
