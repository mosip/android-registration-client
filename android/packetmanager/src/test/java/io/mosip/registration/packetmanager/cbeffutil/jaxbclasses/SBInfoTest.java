package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class SBInfoTest extends TestCase {

    public void testSBInfoBuilderAndGetter() {
        RegistryIDType format = new RegistryIDType();
        SBInfo.SBInfoBuilder builder = new SBInfo.SBInfoBuilder();
        builder.setFormatOwner(format);
        SBInfo sbInfo = builder.build();
        assertEquals(format, sbInfo.getFormat());
    }

    public void testSBInfoBuilderWithNull() {
        SBInfo.SBInfoBuilder builder = new SBInfo.SBInfoBuilder();
        builder.setFormatOwner(null);
        SBInfo sbInfo = builder.build();
        assertNull(sbInfo.getFormat());
    }
}
