package io.mosip.registration.clientmanager.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import io.mosip.registration.clientmanager.dao.DynamicFieldDao;
import io.mosip.registration.clientmanager.entity.DynamicField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DynamicFieldRepository {

    private static final String TAG = DynamicFieldRepository.class.getSimpleName();

    private DynamicFieldDao dynamicFieldDao;

    @Inject
    public DynamicFieldRepository(DynamicFieldDao dynamicFieldDao) {
        this.dynamicFieldDao = dynamicFieldDao;
    }

    public List<String> getDynamicValues(@NonNull String fieldName, String langCode) {
        List<String> values = new ArrayList<>();
        try {
            DynamicField dynamicField = this.dynamicFieldDao.findDynamicFieldByName(fieldName, langCode);
            JSONArray list = new JSONArray(dynamicField.getValueJson());
            for(int i =0; i< list.length(); i++) {
                values.add(list.getJSONObject(i).getString("value"));
            }
        } catch (JSONException e) {
           Log.e("", "failed to parse dynamic field value json", e);
        }
        return values;
    }

    public void saveDynamicField(JSONObject fieldJson) throws JSONException {
        DynamicField dynamicField = new DynamicField(fieldJson.getString("id"));
        dynamicField.setDataType(fieldJson.getString("dataType"));
        dynamicField.setName(fieldJson.getString("name"));
        dynamicField.setLangCode(fieldJson.getString("langCode"));
        dynamicField.setIsActive(fieldJson.getBoolean("isActive"));

        //[{"code":"FR","value":"أجنبي","active":false},{"code":"NFR","value":"غير أجنبي","active":false}]
        dynamicField.setValueJson(fieldJson.getJSONArray("fieldVal").toString());
        dynamicFieldDao.insert(dynamicField);
    }
}
