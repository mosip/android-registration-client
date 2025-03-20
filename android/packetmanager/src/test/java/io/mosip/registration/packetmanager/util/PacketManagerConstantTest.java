package io.mosip.registration.packetmanager.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PacketManagerConstantTest {

    @Test
    public void testIdentityFilename() {
        assertEquals("ID", PacketManagerConstant.IDENTITY_FILENAME);
    }

    @Test
    public void testIdentityFilenameWithExt() {
        assertEquals("ID.json", PacketManagerConstant.IDENTITY_FILENAME_WITH_EXT);
    }

    @Test
    public void testAuditFilename() {
        assertEquals("audit", PacketManagerConstant.AUDIT_FILENAME);
    }

    @Test
    public void testAuditFilenameWithExt() {
        assertEquals("audit.json", PacketManagerConstant.AUDIT_FILENAME_WITH_EXT);
    }

    @Test
    public void testPacketMetaFilename() {
        assertEquals("packet_meta_info.json", PacketManagerConstant.PACKET_META_FILENAME);
    }

    @Test
    public void testPacketDataHashFilename() {
        assertEquals("packet_data_hash.txt", PacketManagerConstant.PACKET_DATA_HASH_FILENAME);
    }

    @Test
    public void testPacketOperHashFilename() {
        assertEquals("packet_operations_hash.txt", PacketManagerConstant.PACKET_OPER_HASH_FILENAME);
    }

    @Test
    public void testCbeffFileFormat() {
        assertEquals("cbeff", PacketManagerConstant.CBEFF_FILE_FORMAT);
    }

    @Test
    public void testCbeffVersion() {
        assertEquals(1.0, PacketManagerConstant.CBEFF_VERSION, 0);
    }

    @Test
    public void testCbeffSchemaFilePath() {
        assertEquals("cbeff.xsd", PacketManagerConstant.CBEFF_SCHEMA_FILE_PATH);
    }

    @Test
    public void testCbeffFilename() {
        assertEquals("%s_bio_CBEFF", PacketManagerConstant.CBEFF_FILENAME);
    }

    @Test
    public void testCbeffFilenameWithExt() {
        assertEquals("%s_bio_CBEFF.xml", PacketManagerConstant.CBEFF_FILENAME_WITH_EXT);
    }

    @Test
    public void testFormatTypes() {
        assertEquals(9, PacketManagerConstant.FORMAT_TYPE_IRIS);
        assertEquals(8, PacketManagerConstant.FORMAT_TYPE_FACE);
        assertEquals(7, PacketManagerConstant.FORMAT_TYPE_FINGER);
    }

    @Test
    public void testMetaApplicationId() {
        assertEquals("applicationId", PacketManagerConstant.META_APPLICATION_ID);
    }

    @Test
    public void testPacketKeeperExceptionMsg() {
        assertEquals("Packet keeper exception occured.", PacketManagerConstant.PACKET_KEEPER_EXCEPTION_MSG);
    }
}
