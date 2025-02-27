package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.res.AssetManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RunWith(RobolectricTestRunner.class)
public class ConfigServiceTest {

    @Mock
    private Context mockContext;

    @Mock
    private AssetManager mockAssetManager;

    private final String PROPERTY_KEY = "default.provider.version";
    private final String PROPERTY_VALUE = "testValue";
    private final String CONFIG_FILE = "packetmanagerconfig.properties";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Mock input stream with sample properties
        String fakeProperties = PROPERTY_KEY + "=" + PROPERTY_VALUE;
        InputStream mockInputStream = new ByteArrayInputStream(fakeProperties.getBytes());

        // Mock behavior of asset manager
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open(CONFIG_FILE)).thenReturn(mockInputStream);
    }

    @Test
    public void testGetProperty_ReturnsCorrectValue() {
        String result = ConfigService.getProperty(PROPERTY_KEY, mockContext);
        assertEquals(PROPERTY_VALUE, result);
    }

    @Test
    public void testGetProperty_ReturnsNullForMissingKey() {
        String result = ConfigService.getProperty("nonExistentKey", mockContext);
        assertNull(result);
    }

    @Test
    public void testGetProperty_FileLoadingError_LogsError() throws Exception {
        when(mockAssetManager.open(CONFIG_FILE)).thenThrow(new IOException("File not found"));

        String result = ConfigService.getProperty(PROPERTY_KEY, mockContext);
        assertNull(result); // Should return null when file loading fails
    }
}
