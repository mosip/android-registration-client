package io.mosip.registration.clientmanager.util.advice;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TestAdvice {
    private static final String LOCAL_CLIENT_CRYPTO_SERVICE_ADVICE =
            "execution(* io.mosip.registration.clientmanager.service.crypto." +
                    "LocalClientCryptoServiceImpl.encrypt(..))";

    @Pointcut(LOCAL_CLIENT_CRYPTO_SERVICE_ADVICE)
    public void k(){}//pointcut name

    @Before("k()")//applying pointcut on before advice
    public void myAdvice() {
        System.out.println("advice called");
    }

}
