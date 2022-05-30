package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.uispec.ConditionalBioAttrDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class UserInterfaceHelperService {

    private static final String TAG = UserInterfaceHelperService.class.getSimpleName();
    private static final String MVEL_CONTEXT_KEY = "identity";
    private static final String MVEL_ENGINE_TYPE = "MVEL";
    private Context context;

    @Inject
    public UserInterfaceHelperService(Context context) {
        this.context = context;
    }


    public static boolean isFieldVisible(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean visible = true;
        if (fieldSpecDto.getVisible() != null && fieldSpecDto.getVisible().getEngine().equalsIgnoreCase(MVEL_ENGINE_TYPE)
                && fieldSpecDto.getVisible().getExpr() != null) {
            visible = evaluateMvel(fieldSpecDto.getVisible().getExpr(), dataContext);
        }
        return visible;
    }

    public static boolean isRequiredField(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean required = fieldSpecDto.getRequired() == null ? false : fieldSpecDto.getRequired();
        if (fieldSpecDto.getRequiredOn() != null && !fieldSpecDto.getRequiredOn().isEmpty()) {
            Optional<RequiredDto> expression = fieldSpecDto.getRequiredOn().stream()
                    .filter(field -> MVEL_ENGINE_TYPE.equalsIgnoreCase(field.getEngine()) && field.getExpr() != null).findFirst();

            if (expression.isPresent()) {
                required = evaluateMvel(expression.get().getExpr(), dataContext);
            }
        }
        return required;
    }

    public static List<String> getRequiredBioAttributes(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        if(!isRequiredField(fieldSpecDto, dataContext))
            return Collections.EMPTY_LIST;

        if(fieldSpecDto.getConditionalBioAttributes() != null) {
            ConditionalBioAttrDto selectedCondition = getConditionalBioAttributes(fieldSpecDto, dataContext);
            if(selectedCondition != null)
                return selectedCondition.getBioAttributes();
        }
        return fieldSpecDto.getBioAttributes();
    }

    public static ConditionalBioAttrDto getConditionalBioAttributes(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        if(fieldSpecDto.getConditionalBioAttributes() == null || fieldSpecDto.getConditionalBioAttributes().isEmpty())
            return null;

        Optional<ConditionalBioAttrDto> result = fieldSpecDto.getConditionalBioAttributes().stream().filter(c ->
                c.getAgeGroup().equalsIgnoreCase((String) dataContext.getOrDefault(RegistrationConstants.AGE_GROUP,
                        RegistrationConstants.DEFAULT_AGE_GROUP)))
                .findFirst();

        if(!result.isPresent()) {
            result = fieldSpecDto.getConditionalBioAttributes().stream().filter(c ->
                    (c.getAgeGroup().equalsIgnoreCase(
                            (String) dataContext.getOrDefault(RegistrationConstants.AGE_GROUP,
                                    RegistrationConstants.DEFAULT_AGE_GROUP))) ||
                            (c.getAgeGroup().equalsIgnoreCase("ALL"))).findFirst();
        }
        return result.isPresent() ? result.get() : null;
    }

    public static boolean evaluateMvel(String expression,  Map<String, Object> dataContext) {
        try {
            Map context = new HashMap();
            context.put(MVEL_CONTEXT_KEY, dataContext);
            VariableResolverFactory resolverFactory = new MapVariableResolverFactory(context);
            return MVEL.evalToBoolean(expression, resolverFactory);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to evaluate mvel expression", t);
        }
        return false;
    }
}
