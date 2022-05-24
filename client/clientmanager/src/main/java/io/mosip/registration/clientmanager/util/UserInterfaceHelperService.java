package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class UserInterfaceHelperService {

    private static final String TAG = UserInterfaceHelperService.class.getSimpleName();
    private Context context;

    @Inject
    public UserInterfaceHelperService(Context context) {
        this.context = context;
    }


    public static boolean isFieldVisible(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean visible = true;
        if (fieldSpecDto.getVisible() != null && fieldSpecDto.getVisible().getEngine().equalsIgnoreCase("MVEL")
                && fieldSpecDto.getVisible().getExpr() != null) {
            visible = evaluateMvel(fieldSpecDto.getVisible().getExpr(), dataContext);
        }
        return visible;
    }

    public static boolean isRequiredField(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean required = fieldSpecDto.getRequired() == null ? false : fieldSpecDto.getRequired();
        if (fieldSpecDto.getRequiredOn() != null && !fieldSpecDto.getRequiredOn().isEmpty()) {
            Optional<RequiredDto> expression = fieldSpecDto.getRequiredOn().stream()
                    .filter(field -> "MVEL".equalsIgnoreCase(field.getEngine()) && field.getExpr() != null).findFirst();

            if (expression.isPresent()) {
                required = evaluateMvel(expression.get().getExpr(), dataContext);
            }
        }
        return required;
    }

    public static boolean evaluateMvel(String expression,  Map<String, Object> dataContext) {
        try {
            Map context = new HashMap();
            context.put("identity", dataContext);
            VariableResolverFactory resolverFactory = new MapVariableResolverFactory(context);
            return MVEL.evalToBoolean(expression, resolverFactory);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to evaluate mvel expression", t);
        }
        return false;
    }
}
