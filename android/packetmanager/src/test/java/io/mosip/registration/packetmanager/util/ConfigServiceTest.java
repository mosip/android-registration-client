package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(RobolectricTestRunner.class)
public class ConfigServiceTest {

    @Mock
    private Context mockContext;

    @Mock
    private AssetManager mockAssetManager;

    @Mock
    private Resources mockResources;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockContext.getResources()).thenReturn(mockResources);
    }

    @Test
    public void testGetProperty_ValidKey() throws Exception {
        // Arrange
        String key = "objectstore.adapter.name";
        String expectedValue = "testValue";
        String propertiesContent = key + "=" + expectedValue + "\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes("UTF-8"));

        // Mock Assets with common file names
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);
        when(mockAssetManager.open("config.properties")).thenReturn(inputStream);
        when(mockAssetManager.open(anyString())).thenReturn(inputStream);

        // Mock Resources as a fallback
        when(mockResources.openRawResource(anyInt())).thenReturn(inputStream);

        // Act
        String result = ConfigService.getProperty(key, mockContext);

        // Debug
        verify(mockAssetManager, times(0)).open("packetmanagerconfig.properties");
        verify(mockAssetManager, times(0)).open("config.properties");
        verify(mockAssetManager, times(0)).open(anyString());
        verify(mockResources, times(0)).openRawResource(anyInt());
        System.out.println("Result from ConfigService: " + result);

        // Assert
        assertEquals("Property value should match expected", expectedValue, result);
    }

    @Test
    public void testGetProperty_InvalidKey() throws Exception {

        String key = "objectstore.name";
        String propertiesContent = "testKey=testValue";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        when(mockAssetManager.open("packetmanagerconfig.properties")).thenReturn(inputStream);
        String result = ConfigService.getProperty(key, mockContext);
        assertEquals(null, result);
    }

    @Test
    public void testGetProperty_IOException() throws Exception {

        String key = "objectstore.adapter.name";
        when(mockAssetManager.open("packetmanagerconfig.properties")).thenThrow(new IOException("File not found"));

        String result = ConfigService.getProperty(key, mockContext);
        assertEquals(null, result);
    }
}