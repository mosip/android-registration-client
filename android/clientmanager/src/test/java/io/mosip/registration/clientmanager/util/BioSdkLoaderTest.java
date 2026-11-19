package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import dalvik.system.DexClassLoader;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.MatchDecision;
import io.mosip.kernel.biometrics.model.QualityCheck;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BioSdkLoaderTest {

    @Mock private Context mockContext;
    @Mock private AssetManager mockAssets;

    private File tempFilesDir;

    @Before
    public void setUp() throws IOException {
        tempFilesDir = Files.createTempDirectory("bio_sdk_test").toFile();
        when(mockContext.getAssets()).thenReturn(mockAssets);
    }

    @After
    public void tearDown() {
        deleteRecursively(tempFilesDir);
    }

    // ═══════════════════════════════════════════════════════
    //  findAllSdkFiles — null context
    // ═══════════════════════════════════════════════════════

    @Test
    public void findAllSdkFiles_nullContext_returnsEmptyList() {
        List<File> result = BioSdkLoader.findAllSdkFiles(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    //  findAllSdkFiles — asset listing paths
    // ═══════════════════════════════════════════════════════

    @Test
    public void findAllSdkFiles_emptyAssetLists_returnsEmptyList() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[0]);
        when(mockAssets.list("")).thenReturn(new String[0]);

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_nullAssetLists_returnsEmptyList() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(null);
        when(mockAssets.list("")).thenReturn(null);

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_nonSdkExtensions_areSkipped() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"readme.txt", "config.json", "image.png"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_assetWithInvalidMagic_skippedWithWarning() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(new byte[]{0x00, 0x01, 0x02, 0x03}));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_dexMagicAsset_copySucceeds_returnsFile() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(64);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))   // isValidDexAsset
                .thenReturn(new ByteArrayInputStream(dexBytes));  // copyFileFromAssets

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);

        assertEquals(1, result.size());
        assertTrue(result.get(0).exists());
    }

    @Test
    public void findAllSdkFiles_zipAssetWithClassesDex_returnsFile() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.jar"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] zipBytes = zipWith("classes.dex", "dummy".getBytes());
        // 3 calls: isValidDexAsset (header), hasClassesDexInZip (full scan), copyFileFromAssets
        when(mockAssets.open("biosdk/sdk.jar"))
                .thenReturn(new ByteArrayInputStream(zipBytes))
                .thenReturn(new ByteArrayInputStream(zipBytes))
                .thenReturn(new ByteArrayInputStream(zipBytes));

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);

        assertEquals(1, result.size());
    }

    @Test
    public void findAllSdkFiles_zipAssetWithoutClassesDex_returnsEmptyList() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.jar"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] zipBytes = zipWith("SomeClass.class", "bytecode".getBytes());
        // 2 calls: isValidDexAsset (header), hasClassesDexInZip (no classes.dex found)
        when(mockAssets.open("biosdk/sdk.jar"))
                .thenReturn(new ByteArrayInputStream(zipBytes))
                .thenReturn(new ByteArrayInputStream(zipBytes));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_copyPrimaryFails_fallbackSucceeds_returnsFile() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(64);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))          // isValidDexAsset
                .thenThrow(new IOException("primary missing"));            // primary copy fails
        when(mockAssets.open("sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes));           // fallback succeeds

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);

        assertEquals(1, result.size());
    }

    @Test
    public void findAllSdkFiles_copyBothPathsFail_returnsEmptyList() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(8);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))          // isValidDexAsset
                .thenThrow(new IOException("primary missing"));            // primary copy fails
        when(mockAssets.open("sdk.dex"))
                .thenThrow(new IOException("fallback missing"));           // fallback fails

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_rootFolderAsset_returnsFile() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{});
        when(mockAssets.list("")).thenReturn(new String[]{"root.dex"});

        // Root folder: assetPath = "root.dex" (no folder prefix)
        byte[] dexBytes = dexMagicBytes(64);
        when(mockAssets.open("root.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))
                .thenReturn(new ByteArrayInputStream(dexBytes));

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);

        assertEquals(1, result.size());
    }

    @Test
    public void findAllSdkFiles_mixedExtensions_onlyValidOnesCopied() throws IOException {
        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"notes.txt", "sdk.dex", "data.xml"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(64);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))
                .thenReturn(new ByteArrayInputStream(dexBytes));

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);

        assertEquals(1, result.size());
    }

    @Test
    public void findAllSdkFiles_assetOpenThrowsIOException_returnsEmptyList() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});
        when(mockAssets.open("biosdk/sdk.dex")).thenThrow(new IOException("cannot open"));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_apkExtension_isIncludedInCheck() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.apk"});
        when(mockAssets.list("")).thenReturn(new String[]{});
        when(mockAssets.open("biosdk/sdk.apk"))
                .thenReturn(new ByteArrayInputStream(new byte[]{0x00, 0x01, 0x02, 0x03}));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_zipExtension_isIncludedInCheck() throws IOException {
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.zip"});
        when(mockAssets.list("")).thenReturn(new String[]{});
        when(mockAssets.open("biosdk/sdk.zip"))
                .thenReturn(new ByteArrayInputStream(new byte[]{0x00, 0x01, 0x02, 0x03}));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    //  loadProvider — guard clauses
    // ═══════════════════════════════════════════════════════

    @Test
    public void loadProvider_nullContext_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(null, "com.example.Sdk",
                Collections.singletonList(new File("test.dex"))));
    }

    @Test
    public void loadProvider_nullClassName_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(mockContext, null,
                Collections.singletonList(new File("test.dex"))));
    }

    @Test
    public void loadProvider_emptyClassName_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(mockContext, "",
                Collections.singletonList(new File("test.dex"))));
    }

    @Test
    public void loadProvider_blankClassName_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(mockContext, "   ",
                Collections.singletonList(new File("test.dex"))));
    }

    @Test
    public void loadProvider_nullSdkFilesList_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk", null));
    }

    @Test
    public void loadProvider_emptySdkFilesList_returnsNull() {
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk", Collections.emptyList()));
    }

    // ═══════════════════════════════════════════════════════
    //  loadProvider — isDexFile filtering
    // ═══════════════════════════════════════════════════════

    @Test
    public void loadProvider_nonExistentFile_skippedReturnsNull() {
        File ghost = new File("does_not_exist_xyz_987.dex");
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                Collections.singletonList(ghost)));
    }

    @Test
    public void loadProvider_fileSmallerThanFourBytes_isDexFileFalse_returnsNull() throws IOException {
        // isDexFile: file.length() < 4 → returns false before any I/O
        File tiny = writeTempFile("tiny", ".dex", new byte[]{0x01, 0x02});
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                Collections.singletonList(tiny)));
    }

    @Test
    public void loadProvider_invalidMagicFourBytes_isDexFileFalse_returnsNull() throws IOException {
        // isDexFile: ZipException → magic check → not "dex\n" → false
        File f = writeTempFile("invalid", ".dex", new byte[]{0x00, 0x01, 0x02, 0x03});
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                Collections.singletonList(f)));
    }

    @Test
    public void loadProvider_validZipWithoutClassesDex_isDexFileFalse_returnsNull() throws IOException {
        // isDexFile: ZipFile opened, no "classes.dex" entry → false
        File f = writeTempFile("no_dex", ".jar", zipWith("SomeClass.class", "data".getBytes()));
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                Collections.singletonList(f)));
    }

    @Test
    public void loadProvider_validZipWithClassesDex_isDexFileTrue_dexClassLoaderFails_returnsNull() throws IOException {
        // isDexFile returns true; DexClassLoader mocked → loadClass throws ClassNotFoundException
        // → caught by ClassNotFoundException catch (Log.d, 2-arg, safe) → returns null
        File f = writeTempFile("with_dex", ".jar", zipWith("classes.dex", "content".getBytes()));
        when(mockContext.getCodeCacheDir()).thenReturn(tempFilesDir);
        try (MockedConstruction<DexClassLoader> ignored = Mockito.mockConstruction(
                DexClassLoader.class, (mock, ctx) ->
                        when(mock.loadClass(anyString())).thenThrow(new ClassNotFoundException("not found")))) {
            assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                    Collections.singletonList(f)));
        }
    }

    @Test
    public void loadProvider_dexMagicFile_isDexFileTrue_dexClassLoaderFails_returnsNull() throws IOException {
        // isDexFile: ZipException → "dex\n" magic → true;
        // DexClassLoader mocked → loadClass throws ClassNotFoundException → caught → returns null
        File f = writeTempFile("direct_dex", ".dex", dexMagicBytes(100));
        when(mockContext.getCodeCacheDir()).thenReturn(tempFilesDir);
        try (MockedConstruction<DexClassLoader> ignored = Mockito.mockConstruction(
                DexClassLoader.class, (mock, ctx) ->
                        when(mock.loadClass(anyString())).thenThrow(new ClassNotFoundException("not found")))) {
            assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                    Collections.singletonList(f)));
        }
    }

    @Test
    public void loadProvider_multipleInvalidFiles_allSkipped_returnsNull() throws IOException {
        File f1 = writeTempFile("bad1", ".dex", new byte[]{0x00, 0x01, 0x02, 0x03});
        File f2 = writeTempFile("bad2", ".dex", new byte[]{0x00, 0x01, 0x02, 0x03});
        assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                Arrays.asList(f1, f2)));
    }

    @Test
    public void loadProvider_mixValidAndInvalidFiles_returnsNull() throws IOException {
        // First file: non-existent (skipped); second: valid ZIP with classes.dex →
        // DexClassLoader mocked → loadClass throws ClassNotFoundException → returns null
        File nonExistent = new File("ghost_file.dex");
        File validZip = writeTempFile("valid", ".jar", zipWith("classes.dex", "data".getBytes()));
        when(mockContext.getCodeCacheDir()).thenReturn(tempFilesDir);
        try (MockedConstruction<DexClassLoader> ignored = Mockito.mockConstruction(
                DexClassLoader.class, (mock, ctx) ->
                        when(mock.loadClass(anyString())).thenThrow(new ClassNotFoundException("not found")))) {
            assertNull(BioSdkLoader.loadProvider(mockContext, "com.example.Sdk",
                    Arrays.asList(nonExistent, validZip)));
        }
    }

    // ═══════════════════════════════════════════════════════
    //  loadProvider — successful class loading paths
    // ═══════════════════════════════════════════════════════

    @Test
    public void loadProvider_loadClassReturnsIBioApiV2_returnsProvider() throws IOException {
        // Covers lines 52-56: loadClass succeeds, instance is IBioApiV2
        File f = writeTempFile("with_dex", ".jar", zipWith("classes.dex", "content".getBytes()));
        when(mockContext.getCodeCacheDir()).thenReturn(tempFilesDir);
        try (MockedConstruction<DexClassLoader> ignored = Mockito.mockConstruction(
                DexClassLoader.class, (mock, ctx) ->
                        when(mock.loadClass(anyString())).thenAnswer(inv -> TestProvider.class))) {
            IBioApiV2 result = BioSdkLoader.loadProvider(mockContext,
                    TestProvider.class.getName(), Collections.singletonList(f));
            assertNotNull(result);
            assertTrue(result instanceof IBioApiV2);
        }
    }

    @Test
    public void loadProvider_loadClassReturnsNonIBioApiV2_continuesAndReturnsNull() throws IOException {
        // Covers lines 52-54 (false branch) + 58: loadClass returns String, not IBioApiV2
        File f = writeTempFile("with_dex", ".jar", zipWith("classes.dex", "content".getBytes()));
        when(mockContext.getCodeCacheDir()).thenReturn(tempFilesDir);
        try (MockedConstruction<DexClassLoader> ignored = Mockito.mockConstruction(
                DexClassLoader.class, (mock, ctx) ->
                        when(mock.loadClass(anyString())).thenAnswer(inv -> String.class))) {
            assertNull(BioSdkLoader.loadProvider(mockContext, "java.lang.String",
                    Collections.singletonList(f)));
        }
    }

    // ═══════════════════════════════════════════════════════
    //  findAllSdkFiles — additional branch coverage
    // ═══════════════════════════════════════════════════════

    @Test
    public void findAllSdkFiles_assetWithShortContent_isSkipped() throws IOException {
        // Covers line 120 false branch: is.read(header) returns < 4
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(new byte[]{0x64, 0x65})); // only 2 bytes

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_outputFileAlreadyExists_isReplacedDuringCopy() throws IOException {
        // Covers lines 164-166: outputFile.exists() true → setWritable + delete before copy
        File biosdkDir = new File(tempFilesDir, "biosdk");
        biosdkDir.mkdirs();
        new File(biosdkDir, "sdk.dex").createNewFile(); // pre-create output file

        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(64);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes))
                .thenReturn(new ByteArrayInputStream(dexBytes));

        List<File> result = BioSdkLoader.findAllSdkFiles(mockContext);
        assertEquals(1, result.size());
    }

    @Test
    public void findAllSdkFiles_mkdirsFailure_returnsEmpty() throws IOException {
        // Covers lines 159-160: biosdkDir.mkdirs() fails because getFilesDir() is a file, not dir
        File blockingFile = writeTempFile("blocking", ".dat", new byte[]{0x01});

        when(mockContext.getFilesDir()).thenReturn(blockingFile); // a FILE, not a directory
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        byte[] dexBytes = dexMagicBytes(8);
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexBytes));

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    @Test
    public void findAllSdkFiles_copiedFileIsNotValidDex_notAddedToList() throws IOException {
        // Covers line 99 false branch: copiedFile exists but isDexFile returns false
        File biosdkDir = new File(tempFilesDir, "biosdk");
        biosdkDir.mkdirs();

        when(mockContext.getFilesDir()).thenReturn(tempFilesDir);
        when(mockAssets.list("biosdk")).thenReturn(new String[]{"sdk.dex"});
        when(mockAssets.list("")).thenReturn(new String[]{});

        // isValidDexAsset call: valid DEX magic → true
        // copyFileFromAssets call: non-DEX content → isDexFile returns false
        when(mockAssets.open("biosdk/sdk.dex"))
                .thenReturn(new ByteArrayInputStream(dexMagicBytes(8)))      // isValidDexAsset
                .thenReturn(new ByteArrayInputStream(new byte[]{0,0,0,0,0,0,0,0})); // copy (invalid)

        assertTrue(BioSdkLoader.findAllSdkFiles(mockContext).isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    //  helpers
    // ═══════════════════════════════════════════════════════

    public static class TestProvider implements IBioApiV2 {
        @Override public SDKInfo init(Map<String, String> p) { return null; }
        @Override public Response<QualityCheck> checkQuality(BiometricRecord r, List<BiometricType> t, Map<String, String> p) { return null; }
        @Override public Response<MatchDecision[]> match(BiometricRecord r, BiometricRecord[] g, List<BiometricType> t, Map<String, String> p) { return null; }
        @Override public Response<BiometricRecord> extractTemplate(BiometricRecord r, List<BiometricType> t, Map<String, String> p) { return null; }
        @Override public Response<BiometricRecord> segment(BiometricRecord r, List<BiometricType> t, Map<String, String> p) { return null; }
        @Override public BiometricRecord convertFormat(BiometricRecord r, String s, String t, Map<String, String> p, Map<String, String> q, List<BiometricType> u) { return null; }
        @Override public Response<BiometricRecord> convertFormatV2(BiometricRecord r, String s, String t, Map<String, String> p, Map<String, String> q, List<BiometricType> u) { return null; }
    }

    private static byte[] dexMagicBytes(int size) {
        byte[] bytes = new byte[Math.max(size, 8)];
        bytes[0] = 0x64; bytes[1] = 0x65; bytes[2] = 0x78; bytes[3] = 0x0A; // "dex\n"
        return bytes;
    }

    private static byte[] zipWith(String entryName, byte[] content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(content);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private static File writeTempFile(String prefix, String suffix, byte[] content) throws IOException {
        File f = File.createTempFile(prefix, suffix);
        f.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(content);
        }
        return f;
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) for (File c : children) deleteRecursively(c);
        }
        file.delete();
    }
}
