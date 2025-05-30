package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;
import java.time.LocalDateTime;
import java.util.Arrays;

public class BIRInfoTypeTest extends TestCase {

    public void testGettersAndSetters() {
        BIRInfoType info = new BIRInfoType();

        String creator = "creator";
        String index = "index";
        byte[] payload = new byte[] {1, 2, 3};
        boolean integrity = true;
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime notValidBefore = creationDate.minusDays(1);
        LocalDateTime notValidAfter = creationDate.plusDays(1);

        info.setCreator(creator);
        info.setIndex(index);
        info.setPayload(payload);
        info.setIntegrity(integrity);
        info.setCreationDate(creationDate);
        info.setNotValidBefore(notValidBefore);
        info.setNotValidAfter(notValidAfter);

        assertEquals(creator, info.getCreator());
        assertEquals(index, info.getIndex());
        assertTrue(Arrays.equals(payload, info.getPayload()));
        assertTrue(info.isIntegrity());
        assertEquals(creationDate, info.getCreationDate());
        assertEquals(notValidBefore, info.getNotValidBefore());
        assertEquals(notValidAfter, info.getNotValidAfter());

        // Test false integrity
        info.setIntegrity(false);
        assertFalse(info.isIntegrity());

        // Test nulls
        info.setCreator(null);
        info.setIndex(null);
        info.setPayload(null);
        info.setCreationDate(null);
        info.setNotValidBefore(null);
        info.setNotValidAfter(null);

        assertNull(info.getCreator());
        assertNull(info.getIndex());
        assertNull(info.getPayload());
        assertNull(info.getCreationDate());
        assertNull(info.getNotValidBefore());
        assertNull(info.getNotValidAfter());
    }
}
