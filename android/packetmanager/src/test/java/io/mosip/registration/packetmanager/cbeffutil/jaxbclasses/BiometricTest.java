package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.LEFT_SLAB;
import org.junit.Test;

import java.util.List;

public class BiometricTest {

    @Test
    public void testGettersAndSetters() {
        Biometric bio = Biometric.LEFT_INDEX;
        assertEquals("leftIndex", bio.getAttributeName());

        assertEquals(BiometricType.FINGER, bio.getBiometricType());

        assertEquals("LF_INDEX", bio.getMdmConstant());
        // Do not mutate mdmConstant here, as it affects other tests

        assertEquals(LEFT_SLAB, bio.getModalityShortName());

        assertEquals("FINGERPRINT_SLAB_LEFT", bio.getModalityName());
    }

    @Test
    public void testGetDefaultAttributes() {
        List<String> attrs = Biometric.getDefaultAttributes(LEFT_SLAB);
        assertTrue(attrs.contains("leftIndex"));
        assertTrue(attrs.contains("leftMiddle"));
        assertTrue(attrs.contains("leftRing"));
        assertTrue(attrs.contains("leftLittle"));

        attrs = Biometric.getDefaultAttributes("FINGERPRINT_SLAB_LEFT");
        assertTrue(attrs.contains("leftIndex"));
        assertTrue(attrs.contains("leftMiddle"));
        assertTrue(attrs.contains("leftRing"));
        assertTrue(attrs.contains("leftLittle"));

        attrs = Biometric.getDefaultAttributes("NonExistent");
        assertTrue(attrs.isEmpty());
    }

    @Test
    public void testGetModalityNameByAttribute() {
        assertEquals("FINGERPRINT_SLAB_LEFT", Biometric.getModalityNameByAttribute("leftIndex"));
        assertNull(Biometric.getModalityNameByAttribute("notExist"));
    }

    @Test
    public void testGetSingleTypeByAttribute() {
        assertEquals(BiometricType.FINGER, Biometric.getSingleTypeByAttribute("leftIndex"));
        assertNull(Biometric.getSingleTypeByAttribute("notExist"));
    }

    @Test
    public void testGetBiometricByAttribute() {
        assertEquals(Biometric.LEFT_INDEX, Biometric.getBiometricByAttribute("leftIndex"));
        assertEquals(Biometric.LEFT_INDEX, Biometric.getBiometricByAttribute("LF_INDEX"));
        assertNull(Biometric.getBiometricByAttribute("notExist"));
    }

    @Test
    public void testGetBiometricByMDMConstant() {
        assertEquals(Biometric.LEFT_INDEX, Biometric.getBiometricByMDMConstant("LF_INDEX"));
        assertNull(Biometric.getBiometricByMDMConstant("notExist"));
    }
    
    @Test
    public void testGetFormatType() {
        assertEquals(CbeffConstant.FORMAT_TYPE_FINGER, Biometric.getFormatType(BiometricType.FINGER));
        assertEquals(CbeffConstant.FORMAT_TYPE_FACE, Biometric.getFormatType(BiometricType.FACE));
        assertEquals(CbeffConstant.FORMAT_TYPE_FACE, Biometric.getFormatType(BiometricType.EXCEPTION_PHOTO));
        assertEquals(CbeffConstant.FORMAT_TYPE_IRIS, Biometric.getFormatType(BiometricType.IRIS));
    }
}
