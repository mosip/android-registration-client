package io.mosip.registration.packetmanager.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.ConfigService;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowLog;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    private static final String ACCOUNT = "PACKET_MANAGER_ACCOUNT";
    private static final String CONTAINER = "10001155851003120250220055513";
    private static final String SOURCE = "source";
    private static final String PROCESS = "process";
    private static final String OBJECT_NAME = "testObject";
    private static final String BASE_LOCATION = "/storage/emulated/0/testBase";

    private MockedStatic<ConfigService> configServiceMock;
    private MockedStatic<Environment> environmentMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ShadowLog.stream = System.out;
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        configServiceMock = mockStatic(ConfigService.class);
        environmentMock = mockStatic(Environment.class);

        configServiceMock.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("testBase");
        environmentMock.when(Environment::getExternalStorageDirectory).thenReturn(new File("/storage/emulated/0"));

        posixAdapterService = new PosixAdapterServiceImpl(mockContext, mockPacketCryptoService, mockObjectMapper);
    }

    @After
    public void tearDown() {
        if (configServiceMock != null) {
            configServiceMock.close();
        }
        if (environmentMock != null) {
            environmentMock.close();
        }
    }

    @Test
    public void testInitPosixAdapterServiceSuccess() throws Exception {
        File baseDir = new File(BASE_LOCATION);
        assertTrue(baseDir.exists());
    }

    @Test
    public void testInitPosixAdapterServiceFailureExternalStorageNotMounted() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
        PosixAdapterServiceImpl newService = new PosixAdapterServiceImpl(mockContext, mockPacketCryptoService, mockObjectMapper);
        assertNull(getFieldValue(newService, "BASE_LOCATION"));
    }

    @Test
    public void testPutObjectSuccessNewZip() throws Exception {
        InputStream mockData = new ByteArrayInputStream("test data".getBytes());
        boolean result = posixAdapterService.putObject(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, mockData);
        assertTrue(result);

        File containerZip = new File(BASE_LOCATION + File.separator + ACCOUNT, CONTAINER + ".zip");
        assertTrue(containerZip.exists());
    }

    @Test
    public void testPutObjectSuccessExistingZip() throws Exception {
        File containerFolder = new File(BASE_LOCATION, ACCOUNT);
        containerFolder.mkdirs();
        File containerZip = new File(containerFolder, CONTAINER + ".zip");
        try (FileOutputStream fos = new FileOutputStream(containerZip)) {
            fos.write("existing data".getBytes());
        }

        InputStream mockData = new ByteArrayInputStream("test data".getBytes());
        boolean result = posixAdapterService.putObject(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, mockData);
        assertTrue(result);
    }

    @Test
    public void testAddObjectMetaDataSuccess() throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        when(mockObjectMapper.writeValueAsString(any())).thenReturn("\"{}\"");
        when(mockObjectMapper.readValue(anyString(), eq(JSONObject.class))).thenReturn(new JSONObject());

        Map<String, Object> result = posixAdapterService.addObjectMetaData(ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME, metadata);
        assertNotNull(result);
        assertEquals(metadata, result);

        File containerZip = new File(BASE_LOCATION + File.separator + ACCOUNT, CONTAINER + ".zip");
        assertTrue(containerZip.exists());
    }

    @Test
    public void testRemoveContainerFailureAccountNotExists() {
        boolean result = posixAdapterService.removeContainer(ACCOUNT, CONTAINER, SOURCE, PROCESS);
        assertFalse(result);
    }

    @Test
    public void testRemoveContainerFailureContainerNotExists() {
        File accountFolder = new File(BASE_LOCATION, ACCOUNT);
        accountFolder.mkdirs();

        boolean result = posixAdapterService.removeContainer(ACCOUNT, CONTAINER, SOURCE, PROCESS);
        assertFalse(result);
    }

    @Test
    public void testPackFailureAccountNotExists() {
        String result = posixAdapterService.pack(ACCOUNT, CONTAINER, SOURCE, PROCESS, "refId");
        assertNull(result);
    }

    @Test
    public void testPackFailureCryptoException() throws Exception {
        File containerFolder = new File(BASE_LOCATION, ACCOUNT);
        containerFolder.mkdirs();
        File containerZip = new File(containerFolder, CONTAINER + ".zip");
        containerZip.createNewFile();

        when(mockPacketCryptoService.encrypt(eq("refId"), any(byte[].class))).thenThrow(new IOException("Crypto failure"));

        String result = posixAdapterService.pack(ACCOUNT, CONTAINER, SOURCE, PROCESS, "refId");
        assertNull(result);
    }

    @Test
    public void testGetMetaDataFailureNoMetadata() throws Exception {
        File containerFolder = new File(BASE_LOCATION, ACCOUNT);
        containerFolder.mkdirs();
        File containerZip = new File(containerFolder, CONTAINER + ".zip");
        try (FileOutputStream fos = new FileOutputStream(containerZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("otherFile.txt"));
            zos.write("no metadata".getBytes());
        }

        Method getMetaDataMethod = PosixAdapterServiceImpl.class.getDeclaredMethod("getMetaData", String.class, String.class, String.class, String.class, String.class);
        getMetaDataMethod.setAccessible(true);

        Map<String, Object> result = (Map<String, Object>) getMetaDataMethod.invoke(posixAdapterService, ACCOUNT, CONTAINER, SOURCE, PROCESS, OBJECT_NAME);
        assertNull(result);
    }

    @Test
    public void testGetAllExistingEntries() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("file1.txt"));
            zos.write("data1".getBytes());
            zos.putNextEntry(new ZipEntry("file2.txt"));
            zos.write("data2".getBytes());
        }

        Method getAllExistingEntriesMethod = PosixAdapterServiceImpl.class.getDeclaredMethod("getAllExistingEntries", InputStream.class);
        getAllExistingEntriesMethod.setAccessible(true);

        Map<ZipEntry, ByteArrayOutputStream> result = (Map<ZipEntry, ByteArrayOutputStream>) getAllExistingEntriesMethod.invoke(posixAdapterService, new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(2, result.size());
        assertTrue(result.keySet().stream().anyMatch(e -> e.getName().equals("file1.txt")));
        assertTrue(result.keySet().stream().anyMatch(e -> e.getName().equals("file2.txt")));
    }

    @Test
    public void testDeleteFileSuccess() throws Exception {
        File file = new File(BASE_LOCATION + File.separator + ACCOUNT, "tempFile.txt");
        file.getParentFile().mkdirs();
        file.createNewFile();

        Method deleteFileMethod = PosixAdapterServiceImpl.class.getDeclaredMethod("deleteFile", File.class);
        deleteFileMethod.setAccessible(true);

        boolean result = (boolean) deleteFileMethod.invoke(posixAdapterService, file);
        assertTrue(result);
        assertFalse(file.exists());
    }

    @Test
    public void testDeleteFileFailure() throws Exception {
        File mockFile = mock(File.class);
        when(mockFile.delete()).thenReturn(false);
        when(mockFile.getCanonicalFile()).thenReturn(mockFile);
        when(mockContext.deleteFile(anyString())).thenReturn(false);

        Method deleteFileMethod = PosixAdapterServiceImpl.class.getDeclaredMethod("deleteFile", File.class);
        deleteFileMethod.setAccessible(true);

        boolean result = (boolean) deleteFileMethod.invoke(posixAdapterService, mockFile);
        assertFalse(result);
    }

    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}