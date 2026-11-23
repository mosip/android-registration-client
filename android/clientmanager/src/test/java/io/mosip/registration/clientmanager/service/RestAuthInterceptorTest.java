package io.mosip.registration.clientmanager.service;

import android.content.Context;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestAuthInterceptorTest {

    @Mock
    Context mockContext;

    @Mock
    SessionManager mockSessionManager;

    @Mock
    Interceptor.Chain mockChain;

    @Mock
    Request mockRequest;

    @Mock
    Request.Builder mockRequestBuilder;

    @Mock
    Response mockResponse;

    private MockedStatic<SessionManager> sessionManagerMock;

    @Before
    public void setUp() {
        sessionManagerMock = Mockito.mockStatic(SessionManager.class);
        sessionManagerMock.when(() -> SessionManager.getSessionManager(mockContext))
                .thenReturn(mockSessionManager);
    }

    @After
    public void tearDown() {
        sessionManagerMock.close();
    }

    @Test
    public void testIntercept_withAuthToken_addsHeader() throws Exception {
        when(mockSessionManager.fetchAuthToken()).thenReturn("token123");
        when(mockChain.request()).thenReturn(mockRequest);
        when(mockRequest.newBuilder()).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.addHeader(anyString(), anyString())).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.build()).thenReturn(mockRequest);
        when(mockChain.proceed(mockRequest)).thenReturn(mockResponse);

        RestAuthInterceptor interceptor = new RestAuthInterceptor(mockContext);
        Response response = interceptor.intercept(mockChain);

        verify(mockRequestBuilder).addHeader(eq("Cookie"), eq("Authorization=token123"));
        verify(mockChain).proceed(mockRequest);
        assertEquals(mockResponse, response);
    }

}
