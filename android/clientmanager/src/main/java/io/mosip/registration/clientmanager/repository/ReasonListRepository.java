package io.mosip.registration.clientmanager.repository;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.ReasonListDao;
import io.mosip.registration.clientmanager.entity.ReasonList;


public class ReasonListRepository {
    private ReasonListDao reasonListDao;

    @Inject
    public ReasonListRepository(ReasonListDao reasonListDao) {
        this.reasonListDao = reasonListDao;
    }

    public List<ReasonList> getAllReasonList(String langCode) {
        return this.reasonListDao.getAllReasonList(langCode);
    }

    public void saveReasonList(JSONObject reasonListJson) throws JSONException {
        ReasonList reasonList = new ReasonList();
        reasonList.setCode(reasonListJson.getString("code"));
        reasonList.setName(reasonListJson.getString("name"));
        reasonList.setLangCode(reasonListJson.getString("langCode"));
        reasonList.setDescription(reasonListJson.getString("description"));
        Log.i(getClass().getSimpleName(), reasonList.toString());
        reasonListDao.insert(reasonList);
    }
}
