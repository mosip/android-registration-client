package io.mosip.registration.clientmanager.repository;

import android.util.Log;
import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.entity.Template;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class TemplateRepository {

    private TemplateDao templateDao;

    @Inject
    public TemplateRepository(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    public void saveTemplate(JSONObject templateJson) throws JSONException {
        Template template = new Template(templateJson.getString("id"),
                templateJson.getString("langCode"));
        template.setTemplateTypeCode(templateJson.getString("templateTypeCode"));
        template.setFileText(templateJson.getString("fileText"));
        template.setFileFormatCode(templateJson.getString("fileFormatCode"));
        template.setName(templateJson.getString("name"));
        template.setIsDeleted(templateJson.getBoolean("isDeleted"));
        template.setIsActive(templateJson.getBoolean("isActive"));
        templateDao.insert(template);
    }
}
