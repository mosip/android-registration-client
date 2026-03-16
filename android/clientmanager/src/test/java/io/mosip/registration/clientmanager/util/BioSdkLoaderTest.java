package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BioSdkLoader}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BioSdkLoaderTest {

    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    @Before
    public void setUp() {
        when(context.getAssets()).thenReturn(assetManager);
    }

    @Test
    public void testFindAllSdkFiles_nullContext_returnsEmptyList() {
        List<File> files = BioSdkLoader.findAllSdkFiles(null);
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    public void testIsValidDexAsset_directDexHeader_returnsTrue() throws Exception {
        String assetPath = "biosdk/sdk1.dex";
        byte[] data = "dex\n1234".getBytes(StandardCharsets.US_ASCII);
        when(assetManager.open(assetPath)).thenReturn(new ByteArrayInputStream(data));

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isValidDexAsset", context, assetPath);

        assertTrue(result);
    }

    @Test
    public void testIsValidDexAsset_zipWithClassesDex_returnsTrue() throws Exception {
        String assetPath = "biosdk/sdk2.zip";
        byte[] zipBytes = createZipWithClassesDex();

        when(assetManager.open(assetPath)).thenAnswer(invocation ->
                new ByteArrayInputStream(zipBytes));

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isValidDexAsset", context, assetPath);

        assertTrue(result);
    }

    @Test
    public void testIsValidDexAsset_invalidHeader_returnsFalse() throws Exception {
        String assetPath = "biosdk/invalid.bin";
        byte[] data = "ABCD".getBytes(StandardCharsets.US_ASCII);
        when(assetManager.open(assetPath)).thenReturn(new ByteArrayInputStream(data));

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isValidDexAsset", context, assetPath);

        assertFalse(result);
    }

    @Test
    public void testHasClassesDexInZip_noClassesDex_returnsFalse() throws Exception {
        String assetPath = "biosdk/no_classes.zip";
        byte[] zipBytes = createZipWithoutClassesDex();

        when(assetManager.open(assetPath)).thenAnswer(invocation ->
                new ByteArrayInputStream(zipBytes));

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "hasClassesDexInZip", context, assetPath);

        assertFalse(result);
    }

    @Test
    public void testCopyFileFromAssets_success() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"),
                "biosdk-test-" + System.currentTimeMillis());
        assertTrue(baseDir.mkdirs());

        when(context.getFilesDir()).thenReturn(baseDir);

        String folder = "biosdk";
        String fileName = "file.dex";
        String assetPath = folder + "/" + fileName;
        byte[] data = "dex\n1234".getBytes(StandardCharsets.US_ASCII);

        when(assetManager.open(assetPath)).thenReturn(new ByteArrayInputStream(data));
        when(assetManager.open(fileName)).thenReturn(new ByteArrayInputStream(data));

        File copied = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "copyFileFromAssets", context, folder, fileName);

        assertNotNull(copied);
        assertTrue(copied.exists());

        // also verify that the copied file is detected as a dex file
        boolean isDex = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isDexFile", copied);
        assertTrue(isDex);
    }

    @Test
    public void testCopyFileFromAssets_missingFile_returnsNull() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"),
                "biosdk-test-missing-" + System.currentTimeMillis());
        assertTrue(baseDir.mkdirs());

        when(context.getFilesDir()).thenReturn(baseDir);

        String folder = "biosdk";
        String fileName = "missing.dex";

        when(assetManager.open(anyString())).thenThrow(new java.io.IOException("not found"));

        File copied = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "copyFileFromAssets", context, folder, fileName);

        assertNull(copied);
    }

    @Test
    public void testIsDexFile_withDirectDexMagic_returnsTrue() throws Exception {
        File file = File.createTempFile("direct-dex-", ".dex");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("dex\n1234".getBytes(StandardCharsets.US_ASCII));
        }

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isDexFile", file);

        assertTrue(result);
    }

    @Test
    public void testIsDexFile_withZipAndClassesDex_returnsTrue() throws Exception {
        File file = File.createTempFile("zip-dex-", ".zip");
        try (FileOutputStream fos = new FileOutputStream(file);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry entry = new ZipEntry("classes.dex");
            zos.putNextEntry(entry);
            zos.write("content".getBytes(StandardCharsets.US_ASCII));
            zos.closeEntry();
        }

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isDexFile", file);

        assertTrue(result);
    }

    @Test
    public void testIsDexFile_invalidFile_returnsFalse() {
        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isDexFile", (File) null);

        assertFalse(result);
    }

    @Test
    public void testIsDexFile_nonZipNonDex_returnsFalse() throws Exception {
        File file = File.createTempFile("not-dex-", ".bin");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("ABCD".getBytes(StandardCharsets.US_ASCII));
        }

        boolean result = ReflectionTestUtils.invokeMethod(
                BioSdkLoader.class, "isDexFile", file);

        assertFalse(result);
    }

    @Test
    public void testLoadProvider_withInvalidArguments_returnsNull() {
        List<File> files = Collections.emptyList();

        assertNull(BioSdkLoader.loadProvider(null, "TestClass", files));
        assertNull(BioSdkLoader.loadProvider(context, null, files));
        assertNull(BioSdkLoader.loadProvider(context, "   ", files));
        assertNull(BioSdkLoader.loadProvider(context, "TestClass", null));
        assertNull(BioSdkLoader.loadProvider(context, "TestClass", files));
    }

    private byte[] createZipWithClassesDex() throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("classes.dex");
            zos.putNextEntry(entry);
            zos.write("dex-content".getBytes(StandardCharsets.US_ASCII));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private byte[] createZipWithoutClassesDex() throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("other.file");
            zos.putNextEntry(entry);
            zos.write("other-content".getBytes(StandardCharsets.US_ASCII));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}

