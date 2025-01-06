package io.mosip.registration.clientmanager.util;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public class UserInterfaceHelperServiceTest {

    @Test
    public void evaluateMvel_TrueExpression_Test() {
        Map<String, Object> dataContext = new HashMap<>();
        dataContext.put("age", 25);

        boolean result = UserInterfaceHelperService.evaluateMvel("identity.age > 18", dataContext);

        assertTrue(result);
    }

    @Test
    public void evaluateMvel_FalseExpression_Test() {

        Map<String, Object> dataContext = new HashMap<>();
        dataContext.put("age", 15);

        boolean result = UserInterfaceHelperService.evaluateMvel("identity.age > 18", dataContext);

        assertFalse(result);
    }

}
