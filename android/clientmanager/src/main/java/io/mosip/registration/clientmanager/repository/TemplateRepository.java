package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.entity.Template;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.List;

public class TemplateRepository {

    private TemplateDao templateDao;

    @Inject
    public TemplateRepository(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    public String getTemplate(String templateName, String langCode) {
        List<String> templateTexts = templateDao.findAllTemplateText(templateName+"%", langCode);
        StringBuilder templateBuilder = new StringBuilder();
        for(String txt : templateTexts) {
            templateBuilder.append(txt);
        }
        return templateBuilder.toString();
    }

    public String getPreviewTemplate(String templateTypeCode, String langCode) {
        List<String> templateTexts = templateDao.findPreviewTemplateText(templateTypeCode+"%", langCode);
        StringBuilder templateBuilder = new StringBuilder();
        for(String txt : templateTexts) {
            templateBuilder.append(txt);
        }
        return templateBuilder.toString();
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
