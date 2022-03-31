package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.spi.PacketWriterService;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewActivity extends DaggerAppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private static final String NEW_LINE = "\n";
    private TextView textView = null;

    private static final String source = "REGISTRATION_CLIENT";
    private static String process = "";
    private static String schemaVersion = "";
    private static String RID = "";

    @Inject
    public PacketWriterService packetWriterService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.process = intent.getStringExtra("process");
        this.schemaVersion = intent.getStringExtra("schemaVersion");
        this.RID = intent.getStringExtra("RID");
        startActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.process = "NEW";
        this.schemaVersion = "0.1";
        this.RID = generateRID();
        startActivity();
    }

    //TODO replace the logic with valid RID generator
    private String generateRID() {
        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        return String.format("100011007710031%s", timestamp);
    }

    private void startActivity() {
        setContentView(R.layout.activity_preview);
        textView = findViewById(R.id.registration_preview);

        final Button button = findViewById(R.id.createpacket);
        button.setOnClickListener( v -> {
            button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            submitForm();
        });

        textView.append("Preview of Registration" + NEW_LINE);
        textView.append("==============================================================" + NEW_LINE);
        textView.append("Registration Process : " + process + NEW_LINE);
        textView.append("Registration ID : " + RID + NEW_LINE);
        textView.append("TODO - show all the captured fields : " + NEW_LINE);
    }

    private void submitForm() {
        packetWriterService.addAudits(RID, getAudits());
        String identitySchema = loadJSONFromResource(R.raw.identity_schema);
        List<PacketInfo> result = packetWriterService.persistPacket(RID, schemaVersion, identitySchema, source, process, true);

        textView.append("==============================================================" + NEW_LINE);
        textView.append((result == null || result.isEmpty()) ?
                "Failed to create registration packet" : "Packet created successfully" + NEW_LINE);

        textView.append("==============================================================" + NEW_LINE);
        textView.append("TODO : Started to sync RID" + NEW_LINE);
        syncRID();

        textView.append("==============================================================" + NEW_LINE);
        textView.append("TODO : Uploading packet to server" + NEW_LINE);
        uploadPacketToServer();

        goToHome();
    }

    private void syncRID() {

    }

    private void uploadPacketToServer() {

    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        goToHome();
        return true;
    }

    public String loadJSONFromResource(int resourceNumber) {
        String json = null;
        try(InputStream is = getApplicationContext().getResources().openRawResource(resourceNumber)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to load ui spec json", ex);
        }
        return json;
    }


    //TODO fetch the saved audits and add them in the packet
    public List<Map<String, String>> getAudits() {
        Map<String, String> auditEntry = new HashMap<>();
        auditEntry.put("date", "date");
        auditEntry.put("message", "message");
        auditEntry.put("actor", "operator");
        List<Map<String, String>> audits = new ArrayList<>();
        audits.add(auditEntry);
        return audits;
    }

}
