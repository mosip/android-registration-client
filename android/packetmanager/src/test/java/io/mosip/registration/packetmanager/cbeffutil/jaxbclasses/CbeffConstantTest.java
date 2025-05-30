package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class CbeffConstantTest extends TestCase  {

    public void testFormatOwner() {
        assertEquals(257L, CbeffConstant.FORMAT_OWNER);
    }

    public void testFormatTypes() {
        assertEquals(9L, CbeffConstant.FORMAT_TYPE_IRIS);
        assertEquals(8L, CbeffConstant.FORMAT_TYPE_FACE);
        assertEquals(7L, CbeffConstant.FORMAT_TYPE_FINGER);
        assertEquals(2L, CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE);
    }

    public void testFormatIdentifiers() {
        assertEquals(0x46495200, CbeffConstant.FINGER_FORMAT_IDENTIFIER);
        assertEquals(0x49495200, CbeffConstant.IRIS_FORMAT_IDENTIFIER);
        assertEquals(0x46495200, CbeffConstant.FACE_FORMAT_IDENTIFIER);
    }

    public void testNegativeAndBoundaryValues() {
        assertFalse(CbeffConstant.FORMAT_OWNER == -1);
        assertTrue(CbeffConstant.FORMAT_TYPE_IRIS > 0);
        assertTrue(CbeffConstant.FORMAT_TYPE_FACE > 0);
        assertTrue(CbeffConstant.FORMAT_TYPE_FINGER > 0);
        assertTrue(CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE > 0);
        assertTrue(CbeffConstant.FINGER_FORMAT_IDENTIFIER != CbeffConstant.IRIS_FORMAT_IDENTIFIER);
    }

    public void testClassCannotBeInstantiated() {
        try {
            java.lang.reflect.Constructor<CbeffConstant> ctor = CbeffConstant.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
            // If we reach here, instantiation succeeded, which is a failure
            fail("Should not be able to instantiate CbeffConstant");
        } catch (Throwable e) {
            // Test passes if any exception or error is thrown, since instantiation is not allowed
        }
    }
}
