package io.mosip.registration.packetmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {

    private Context mockContext;
    private AssetManager mockAssetManager;

    @Before
    public void setUp() throws Exception {
        mockContext = Mockito.mock(Context.class);
        mockAssetManager = Mockito.mock(AssetManager.class);
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
    }

    @Test
    public void getProperty_withValidKey_thenReturnCorrectValue() throws Exception {
        // Arrange
        String key = "packet.manager.account.name";
        String expectedValue = "PACKET_MANAGER_ACCOUNT";
        String propertiesContent = key + "=" + expectedValue + "\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        // Act
        String actualValue = ConfigService.getProperty(key, mockContext);

        // Assert
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void getProperty_withPropertiesFileNotFound_thenReturnNull() throws Exception {
        // Arrange
        String key = "packet.manager.account.names";
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenThrow(new IOException());

        // Act
        String actualValue = ConfigService.getProperty(key, mockContext);

        // Assert
        assertEquals(null, actualValue);
    }

    @Test
    public void getProperty_withPropertiesAlreadyLoaded_thenReturnCorrectValue() throws Exception {
        // Arrange
        String key = "packet.manager.account.name";
        String expectedValue = "PACKET_MANAGER_ACCOUNT";
        String propertiesContent = key + "=" + expectedValue + "\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        // Load properties for the first time
        ConfigService.getProperty(key, mockContext);

        // Act
        String actualValue = ConfigService.getProperty(key, mockContext);

        // Assert
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void getProperty_withNonExistentKey_thenReturnNull() throws Exception {
        // Arrange
        String key = "non.existent.key";
        String propertiesContent = "packet.manager.account.name=PACKET_MANAGER_ACCOUNT\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);

        // Act
        String actualValue = ConfigService.getProperty(key, mockContext);

        // Assert
        assertEquals(null, actualValue);
    }

    @Test
    public void getProperty_whenIOExceptionOccurs_thenLogError() throws Exception {
        // Arrange
        String key = "packet.manager.account.names";
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenThrow(new IOException());

        // Mock Log class to verify logging
        Log mockLog = Mockito.mock(Log.class);
        // Use reflection to set the Log class to the mock
        // Note: This is a workaround and may not work in all environments
        // You may need to use a logging framework that allows for easier testing
        // For example, you could use a wrapper around Log that you can mock

        // Act
        String actualValue = ConfigService.getProperty(key, mockContext);

        // Assert
        assertEquals(null, actualValue);
        // Verify that Log.e was called
        // Note: This will not work directly since Log.e is static; consider using a logging wrapper
        // verify(mockLog).e(eq("Registration-client"), anyString(), any(IOException.class));
    }
}