package io.mosip.registration.keymanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    private Context mockContext;
    private AssetManager mockAssetManager;
    private InputStream mockInputStream;

    @Before
    public void setUp() {
        // Clear properties before each test
        Properties props = getProperties();
        props.clear();

        mockContext = mock(Context.class);
        mockAssetManager = mock(AssetManager.class);
        mockInputStream = new ByteArrayInputStream("testKey=testValue\n".getBytes());
    }

    @After
    public void tearDown() {
        // Clear properties after each test
        getProperties().clear();
    }

    @Test
    public void testGetProperty_LoadsPropertiesAndReturnsValue() throws Exception {
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open("config.properties")).thenReturn(mockInputStream);

        String value = ConfigService.getProperty("testKey", mockContext);

        assertEquals("testValue", value);
        // Should not reload properties if already loaded
        String value2 = ConfigService.getProperty("testKey", mockContext);
        assertEquals("testValue", value2);
        verify(mockAssetManager, times(1)).open("config.properties");
    }

    @Test
    public void testGetProperty_ReturnsNullForMissingKey() throws Exception {
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open("config.properties")).thenReturn(mockInputStream);

        String value = ConfigService.getProperty("missingKey", mockContext);

        assertNull(value);
    }

    @Test
    public void testGetProperty_IOException_LogsError() throws Exception {
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open("config.properties")).thenThrow(new IOException("Test IO Exception"));

        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            String value = ConfigService.getProperty("anyKey", mockContext);
            assertNull(value);
            logMock.verify(() -> Log.e(eq("Registration-client"), eq("Failed to load properties file"), any(IOException.class)));
        }
    }

    // Helper to access private static properties field via reflection
    private Properties getProperties() {
        try {
            java.lang.reflect.Field field = ConfigService.class.getDeclaredField("properties");
            field.setAccessible(true);
            return (Properties) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
