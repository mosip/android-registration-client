package io.mosip.registration.clientmanager.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import io.mosip.registration.clientmanager.dao.DynamicFieldDao;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
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

    public List<GenericValueDto> getDynamicValues(@NonNull String fieldName, String langCode) {
        List<GenericValueDto> values = new ArrayList<>();
        try {
            DynamicField dynamicField = this.dynamicFieldDao.findDynamicFieldByName(fieldName, langCode);
            if(dynamicField == null)
                return values;
            JSONArray list = new JSONArray(dynamicField.getValueJson());
            for(int i =0; i< list.length(); i++) {
                values.add(new GenericValueDto(list.getJSONObject(i).getString("value"),
                        list.getJSONObject(i).getString("code"), langCode));
            }
        } catch (JSONException e) {
           Log.e("", "failed to parse dynamic field value json", e);
        }
        return values;
    }

    public List<GenericValueDto> getDynamicValuesByCode(@NonNull String fieldName, String code) {
        List<GenericValueDto> values = new ArrayList<>();
        List<DynamicField> dynamicFields = this.dynamicFieldDao.findAllDynamicValuesByName(fieldName);
        if(dynamicFields == null || dynamicFields.isEmpty())
            return values;

        dynamicFields.forEach( field -> {
            try {
                JSONArray list = new JSONArray(field.getValueJson());
                for(int i =0; i< list.length(); i++) {
                    JSONObject element = list.getJSONObject(i);
                    if(element.getString("code").equals(code)) {
                        values.add(new GenericValueDto(element.getString("value"),
                                element.getString("code"), field.getLangCode()));
                    }
                }
            } catch (JSONException e) {
                Log.e("", "failed to parse dynamic field value json", e);
            }
        });
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
