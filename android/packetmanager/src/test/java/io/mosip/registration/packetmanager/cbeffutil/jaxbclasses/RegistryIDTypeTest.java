package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class RegistryIDTypeTest extends TestCase {

    public void testGettersAndSetters() {
        RegistryIDType registry = new RegistryIDType();

        // Test organization
        String org = "TestOrg";
        registry.setOrganization(org);
        assertEquals(org, registry.getOrganization());

        // Test type
        String type = "TestType";
        registry.setType(type);
        assertEquals(type, registry.getType());

        // Test nulls
        registry.setOrganization(null);
        registry.setType(null);
        assertNull(registry.getOrganization());
        assertNull(registry.getType());
    }
}
