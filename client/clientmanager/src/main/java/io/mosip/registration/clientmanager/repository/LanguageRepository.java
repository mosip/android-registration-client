package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.LanguageDao;
import io.mosip.registration.clientmanager.entity.Language;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LanguageRepository {

    private static final String TAG = LanguageRepository.class.getSimpleName();

    private Map<String, String> localCache = new HashMap<>();

    private LanguageDao languageDao;

    public LanguageRepository(LanguageDao languageDao) {
        this.languageDao = languageDao;
    }

    public void saveLanguage(JSONObject languageJson) throws JSONException {
        Language language = new Language(languageJson.getString("code"));
        language.setName(languageJson.getString("name"));
        language.setNativeName(languageJson.getString("nativeName"));
        language.setIsActive(languageJson.getBoolean("isActive"));
        language.setIsDeleted(languageJson.getBoolean("isDeleted"));
        this.languageDao.insertLanguage(language);
        localCache.put(language.getCode(), language.getNativeName());
    }

    public String getNativeName(String code) {
        String nativeName = localCache.get(code);
        if(nativeName != null)
            return nativeName;

        List<Language> languageList = languageDao.getAllActiveLanguage();
        for(Language language : languageList) {
            localCache.put(language.getCode(), language.getNativeName());
        }
        return localCache.get(code);
    }
}
