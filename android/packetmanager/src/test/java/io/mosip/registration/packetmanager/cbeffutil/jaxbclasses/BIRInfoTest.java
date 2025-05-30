package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;
import java.time.LocalDateTime;
import java.util.Arrays;

public class BIRInfoTest extends TestCase {

    public void testBIRInfoBuilderAndFields() {
        String creator = "testCreator";
        String index = "testIndex";
        byte[] payload = new byte[] {1, 2, 3};
        Boolean integrity = Boolean.TRUE;
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime notValidBefore = creationDate.minusDays(1);
        LocalDateTime notValidAfter = creationDate.plusDays(1);

        BIRInfo.BIRInfoBuilder builder = new BIRInfo.BIRInfoBuilder()
                .withCreator(creator)
                .withIndex(index)
                .withPayload(payload)
                .withIntegrity(integrity)
                .withCreationDate(creationDate)
                .withNotValidBefore(notValidBefore)
                .withNotValidAfter(notValidAfter);

        BIRInfo birInfo = builder.build();

        // Use reflection to access private fields for coverage
        try {
            java.lang.reflect.Field fCreator = BIRInfo.class.getDeclaredField("creator");
            java.lang.reflect.Field fIndex = BIRInfo.class.getDeclaredField("index");
            java.lang.reflect.Field fPayload = BIRInfo.class.getDeclaredField("payload");
            java.lang.reflect.Field fIntegrity = BIRInfo.class.getDeclaredField("integrity");
            java.lang.reflect.Field fCreationDate = BIRInfo.class.getDeclaredField("creationDate");
            java.lang.reflect.Field fNotValidBefore = BIRInfo.class.getDeclaredField("notValidBefore");
            java.lang.reflect.Field fNotValidAfter = BIRInfo.class.getDeclaredField("notValidAfter");

            fCreator.setAccessible(true);
            fIndex.setAccessible(true);
            fPayload.setAccessible(true);
            fIntegrity.setAccessible(true);
            fCreationDate.setAccessible(true);
            fNotValidBefore.setAccessible(true);
            fNotValidAfter.setAccessible(true);

            assertEquals(creator, fCreator.get(birInfo));
            assertEquals(index, fIndex.get(birInfo));
            assertTrue(Arrays.equals(payload, (byte[]) fPayload.get(birInfo)));
            assertEquals(integrity, fIntegrity.get(birInfo));
            assertEquals(creationDate, fCreationDate.get(birInfo));
            assertEquals(notValidBefore, fNotValidBefore.get(birInfo));
            assertEquals(notValidAfter, fNotValidAfter.get(birInfo));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    public void testBIRInfoBuilderDefaults() {
        // Test builder with no fields set
        BIRInfo birInfo = new BIRInfo.BIRInfoBuilder().build();
        try {
            java.lang.reflect.Field fCreator = BIRInfo.class.getDeclaredField("creator");
            java.lang.reflect.Field fIndex = BIRInfo.class.getDeclaredField("index");
            java.lang.reflect.Field fPayload = BIRInfo.class.getDeclaredField("payload");
            java.lang.reflect.Field fIntegrity = BIRInfo.class.getDeclaredField("integrity");
            java.lang.reflect.Field fCreationDate = BIRInfo.class.getDeclaredField("creationDate");
            java.lang.reflect.Field fNotValidBefore = BIRInfo.class.getDeclaredField("notValidBefore");
            java.lang.reflect.Field fNotValidAfter = BIRInfo.class.getDeclaredField("notValidAfter");

            fCreator.setAccessible(true);
            fIndex.setAccessible(true);
            fPayload.setAccessible(true);
            fIntegrity.setAccessible(true);
            fCreationDate.setAccessible(true);
            fNotValidBefore.setAccessible(true);
            fNotValidAfter.setAccessible(true);

            assertNull(fCreator.get(birInfo));
            assertNull(fIndex.get(birInfo));
            assertNull(fPayload.get(birInfo));
            assertNull(fIntegrity.get(birInfo));
            assertNull(fCreationDate.get(birInfo));
            assertNull(fNotValidBefore.get(birInfo));
            assertNull(fNotValidAfter.get(birInfo));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}
