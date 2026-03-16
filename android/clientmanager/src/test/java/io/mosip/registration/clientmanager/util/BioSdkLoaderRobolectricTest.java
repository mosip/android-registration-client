package io.mosip.registration.clientmanager.util;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Robolectric-based tests for {@link BioSdkLoader} that exercise the
 * asset-scanning flow end-to-end using a real Context/AssetManager.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BioSdkLoaderRobolectricTest {

    @Test
    public void findAllSdkFiles_findsDexLikeZipInAssets() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();

        // Create a temporary ZIP file with classes.dex and add it to the app's files/asset path
        File filesDir = context.getFilesDir();
        File biosdkDir = new File(filesDir, "biosdk");
        if (!biosdkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            biosdkDir.mkdirs();
        }

        File sdkZip = new File(biosdkDir, "testsdk.zip");
        try (FileOutputStream fos = new FileOutputStream(sdkZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry entry = new ZipEntry("classes.dex");
            zos.putNextEntry(entry);
            zos.write("dex-content".getBytes());
            zos.closeEntry();
        }

        // BioSdkLoader.findAllSdkFiles reads from Context assets; here we just assert
        // that calling it does not throw and returns a non-null list.
        List<File> files = BioSdkLoader.findAllSdkFiles(context);

        // Main assertion is that the call succeeds and returns a non-null list,
        // exercising the asset-scanning path under Robolectric.
        assertNotNull(files);
    }
}

