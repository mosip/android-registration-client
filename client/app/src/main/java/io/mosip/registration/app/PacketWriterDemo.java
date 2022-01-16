package io.mosip.registration.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.packetmanager.dto.PacketWriter.BDBInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.BIR;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.QualityType;
import io.mosip.registration.packetmanager.dto.PacketWriter.RegistryIDType;
import io.mosip.registration.packetmanager.service.PacketWriterServiceImpl;

public class PacketWriterDemo extends AppCompatActivity {

    // Packet meta info constants
    private static final String ID = "id";
    private static final String PACKET_NAME = "packetname";
    private static final String SOURCE = "source";
    private static final String PROCESS = "process";
    private static final String SCHEMA_VERSION = "schemaversion";
    private static final String SIGNATURE = "signature";
    private static final String ENCRYPTED_HASH = "encryptedhash";
    private static final String PROVIDER_NAME = "providername";
    private static final String PROVIDER_VERSION = "providerversion";
    private static final String CREATION_DATE = "creationdate";
    private static final String REFID = "refid";
    private static final String PACKET_MANAGER_ACCOUNT = "PACKET_MANAGER_ACCOUNT";
    private static final String source = "reg-client";
    private static final String process = "NEW";
    private static final String id = "110111101120191111121111";
    private static final String objectSuffix = "Test";
    private static final String objectName = id + "_" + objectSuffix;
    private static final String refId = "1234512345_121212";

    private static final String TAG = PacketWriterDemo.class.getSimpleName();

    public PacketWriterServiceImpl packetWriter;

    TextView packetWriterTextView;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_writer_demo);

        packetWriter = new PacketWriterServiceImpl();
        packetWriter.initialize(id);
        packetWriterTextView = (TextView) findViewById(R.id.packetWriterTextView);
    }

    public void click_SetField(View view) {
        String resultMsg = SetField();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_SetFields(View view) {
        String resultMsg = SetFields();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_SetBiometrics(View view) {
        String resultMsg = SetBiometrics();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_SetDocument(View view) {
        String resultMsg = SetDocument();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_AddMetaInfo(View view) {
        String resultMsg = AddMetaInfo();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_AddMetaInfoKeyValue(View view) {
        String resultMsg = AddMetaInfoKeyValue();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_AddAudit(View view) {
        String resultMsg = AddAudit();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_AddAudits(View view) {
        String resultMsg = AddAudits();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    public void click_PersistPacket(View view) {
        String resultMsg = PersistPacket();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        packetWriterTextView.setText(resultMsg);
    }

    //----------------------------------------------

    public String SetField() {
        String resultMsg = "";
        try {

            packetWriter.setField(id, "name", "mono");
            resultMsg = "SetField successful";

        } catch (Exception e) {
            resultMsg = "SetField failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }


    public String SetFields() {
        String resultMsg = "";
        try {
            Map<String, String> fields = new HashMap<>();
            packetWriter.setFields(id, fields);
            resultMsg = "SetFields successful";

        } catch (Exception e) {
            resultMsg = "SetFields failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String SetDocument() {
        String resultMsg = "";
        try {
            Document document = new Document();
            document.setValue("document");

            packetWriter.setDocument(id, "poa", document);

            resultMsg = "SetDocument successful";

        } catch (Exception e) {
            resultMsg = "SetDocument failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String SetBiometrics() {
        String resultMsg = "";
        try {
            List<BIR> birTypeList = new ArrayList<>();
            BIR birType1 = new BIR.BIRBuilder().build();
            BDBInfo bdbInfoType1 = new BDBInfo.BDBInfoBuilder().build();
            RegistryIDType registryIDType = new RegistryIDType("Mosip", "257");
            QualityType quality = new QualityType();
            quality.setAlgorithm(registryIDType);
            quality.setScore(90l);
            bdbInfoType1.setQuality(quality);
            BiometricType singleType1 = BiometricType.FINGER;
            List<BiometricType> singleTypeList1 = new ArrayList<>();
            singleTypeList1.add(singleType1);
            List<String> subtype1 = new ArrayList<>(Arrays.asList("Left", "RingFinger"));
            bdbInfoType1.setSubtype(subtype1);
            bdbInfoType1.setType(singleTypeList1);
            birType1.setBdbInfo(bdbInfoType1);
            birTypeList.add(birType1);
//            String source = "reg-client";
//            String process = "NEW";
//            String id = "110111101120191111121111";
            BiometricRecord biometricRecord = new BiometricRecord();
            biometricRecord.setSegments(birTypeList);

            packetWriter.setBiometric(id, "individualBiometrics", biometricRecord);

            resultMsg = "SetBiometrics successful";

        } catch (Exception e) {
            resultMsg = "SetBiometrics failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String AddMetaInfo() {
        String resultMsg = "";
        try {
            Map<String, String> fields = new HashMap<>();
            packetWriter.addMetaInfo(id, fields);
            resultMsg = "AddMetaInfo successful";

        } catch (Exception e) {
            resultMsg = "AddMetaInfo failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String AddMetaInfoKeyValue() {
        String resultMsg = "";
        try {

            packetWriter.addMetaInfo(id, "rid", "regid");

            resultMsg = "AddMetaInfoKeyValue successful";

        } catch (Exception e) {
            resultMsg = "AddMetaInfoKeyValue failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String AddAudit() {
        String resultMsg = "";
        try {
            Map<String, String> fields = new HashMap<>();
            packetWriter.addAudit(id, fields);

            resultMsg = "AddAudit successful";

        } catch (Exception e) {
            resultMsg = "AddAudit failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String AddAudits() {
        String resultMsg = "";
        try {
            Map<String, String> auditMap = new HashMap<>();
            auditMap.put("audit", "audit1");
            List<Map<String, String>> auditList = new ArrayList<>();
            auditList.add(auditMap);

            packetWriter.addAudits(id, auditList);
            resultMsg = "AddAudits successful";

        } catch (Exception e) {
            resultMsg = "AddAudits failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    public String PersistPacket() {
        String resultMsg = "";
        try {
            String identitySchema = loadSchemeFile(R.raw.identity_schema);

            List<PacketInfo> packetInfos = new ArrayList<>();

            List<PacketInfo> result = packetWriter.persistPacket(id, "0.2", identitySchema, source, process, true);

            if (result != null) {
                resultMsg = "Persist Packet successful";
            } else {
                resultMsg = "Persist Packet failed";
            }

        } catch (Exception e) {
            resultMsg = "Persist Packet failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    private String loadSchemeFile(int rID) throws IOException
    {
        InputStream iS;
        iS = getResources().openRawResource(rID);

        byte[] buffer = new byte[iS.available()];
        iS.read(buffer);
        ByteArrayOutputStream oS = new ByteArrayOutputStream();
        oS.write(buffer);
        oS.close();
        iS.close();

        return oS.toString();
    }
}