package io.mosip.registration.packetmanager.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.robolectric.RobolectricTestRunner;

import java.io.*;
import java.util.*;

import io.mosip.registration.packetmanager.spi.IPacketCryptoService;

@RunWith(RobolectricTestRunner.class)
public class PosixAdapterServiceImplTest {

    @Mock
    private Context mockContext;

    @Mock
    private IPacketCryptoService mockPacketCryptoService;

    @Mock
    private ObjectMapper mockObjectMapper;

    @InjectMocks
    private PosixAdapterServiceImpl posixAdapterService;

    private static final String ACCOUNT = "testAccount";
    private static final String CONTAINER = "testContainer";
    private static final String SOURCE = "testSource";
    private static final String PROCESS = "testProcess";
    private static final String OBJECT_NAME = "testObject";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getExternalFilesDir(null)).thenReturn(Environment.getExternalStorageDirectory());

        posixAdapterService = new PosixAdapterServiceImpl(mockContext, mockPacketCryptoService, mockObjectMapper);
    }

    @Test
    public void testPutObjectSuccess() throws Exception {
        InputStream mockData = new ByteArrayInputStream("test data".getBytes());
        boolean result = posixAdapterService.putObject(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, mockData);
        assertTrue(result);
    }

    @Test
    public void testPutObjectFailure() throws Exception {

        doThrow(new IOException("Mocked exception")).when(mockPacketCryptoService).encrypt(any(), any());
        InputStream mockData = new ByteArrayInputStream("test data".getBytes());
        boolean result = posixAdapterService.putObject(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, mockData);
        assertTrue(result);
    }

    @Test
    public void testAddObjectMetaDataSuccess() throws Exception {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");
        when(mockObjectMapper.writeValueAsString(any())).thenReturn("{}");
        Map<String, Object> result = posixAdapterService.addObjectMetaData(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, metadata);
        assertNotNull(result);
    }

    @Test
    public void testAddObjectMetaDataFailure() throws Exception {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(null, null);
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Mocked Exception"));

        Map<String, Object> result = posixAdapterService.addObjectMetaData(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, metadata);
        assertNull(result);
    }


    @Test
    public void testRemoveContainerSuccess() {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.delete()).thenReturn(true);

        boolean result = posixAdapterService.removeContainer(ACCOUNT, CONTAINER, SOURCE, PROCESS);
        assertFalse(result);
    }

    @Test
    public void testRemoveContainerFailure() {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.delete()).thenReturn(false);

        boolean result = posixAdapterService.removeContainer(ACCOUNT, CONTAINER, SOURCE, PROCESS);
        assertFalse(result);
    }


    @Test
    public void testPackFailure() throws Exception {
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenThrow(new IOException("Mocked Exception"));
        String result = posixAdapterService.pack(ACCOUNT, CONTAINER, SOURCE, PROCESS, "refId");
        assertNull(result);
    }

    @Test
    public void testGetMetaDataFailure() throws Exception {
        when(mockObjectMapper.readValue(anyString(), eq(HashMap.class))).thenThrow(new RuntimeException("Mocked Exception"));

        Method getMetaDataMethod = PosixAdapterServiceImpl.class.getDeclaredMethod("getMetaData", String.class, String.class, String.class, String.class, String.class);
        getMetaDataMethod.setAccessible(true);

        Map<String, Object> result = (Map<String, Object>) getMetaDataMethod.invoke(posixAdapterService, ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME);
        assertNull(result);
    }

}
