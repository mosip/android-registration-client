package io.mosip.registration.packetmanager.util;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    private Context mockContext;
    private AssetManager mockAssetManager;
    private MockedStatic<Log> logMock;

    @Before
    public void setUp() {
        Properties props = getProperties();
        props.clear();

        mockContext = mock(Context.class);
        mockAssetManager = mock(AssetManager.class);
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        logMock = Mockito.mockStatic(Log.class);
    }

    @After
    public void tearDown() {
        if (logMock != null) {
            logMock.close();
        }
    }

    @Test
    public void getProperty_withValidKey_returnsCorrectValue() throws Exception {
        String key = "packet.manager.account.name";
        String expectedValue = "PACKET_MANAGER_ACCOUNT";
        String propertiesContent = key + "=" + expectedValue + "\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        String actualValue = ConfigService.getProperty(key, mockContext);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void getProperty_withPropertiesFileNotFound_returnsNull() throws Exception {
        String key = "packet.manager.account.names";
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenThrow(new IOException());

        String actualValue = ConfigService.getProperty(key, mockContext);

        assertEquals(null, actualValue);
    }

    @Test
    public void getProperty_withAlreadyLoadedProperties_returnsCorrectValue() throws Exception {
        String key = "packet.manager.account.name";
        String expectedValue = "PACKET_MANAGER_ACCOUNT";
        String propertiesContent = key + "=" + expectedValue + "\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        ConfigService.getProperty(key, mockContext);
        String actualValue = ConfigService.getProperty(key, mockContext);

        assertEquals(expectedValue, actualValue);
        verify(mockAssetManager, times(1)).open("packetmanagerconfig.properties");
    }

    @Test
    public void getProperty_withNonExistentKey_returnsNull() throws Exception {
        String key = "non.existent.key";
        String propertiesContent = "packet.manager.account.name=PACKET_MANAGER_ACCOUNT\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        String actualValue = ConfigService.getProperty(key, mockContext);

        assertEquals(null, actualValue);
    }

    @Test
    public void getProperty_whenIOExceptionOccurs_logsErrorAndReturnsNull() throws Exception {
        String key = "packet.manager.account.names";
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenThrow(new IOException("Test IO Exception"));

        String actualValue = ConfigService.getProperty(key, mockContext);

        assertNull(actualValue);
        logMock.verify(() -> Log.e(eq("Registration-client"), eq("Failed to load properties file"), any(IOException.class)));
    }

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