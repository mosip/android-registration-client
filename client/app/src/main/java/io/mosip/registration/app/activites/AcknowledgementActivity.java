package io.mosip.registration.app.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AcknowledgementActivity extends DaggerAppCompatActivity {

    private static final String TAG = AcknowledgementActivity .class.getSimpleName();

    private WebView webView;
    private List<PrintJob> printJobs = new ArrayList<>();

    @Inject
    AuditManagerService auditManagerService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity();
    }

    private void startActivity() {
        setContentView(R.layout.activity_ack);
        webView = findViewById(R.id.registration_ack);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ack_slip);

        final Button printButton = findViewById(R.id.printslip);
        printButton.setOnClickListener( v -> {
            auditManagerService.audit(AuditEvent.PRINT_ACKNOWLEDGEMENT, Components.REGISTRATION);
            createWebPrintJob(webView);

            long fromDate = System.currentTimeMillis() - 3600000;
            List<Audit> audits = auditManagerService.getAuditLogs(fromDate);

            for (Audit audit : audits) {
                Log.i("Audit", audit.toString());
            }

        });

        try {
            String htmlDocument = getIntent().getStringExtra("content");
            webView.loadDataWithBaseURL(null, htmlDocument,
                    "text/HTML", "UTF-8", null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set the acknowledgement content", e);
        }
        auditManagerService.audit(AuditEvent.LOADED_ACKNOWLEDGEMENT_SCREEN, Components.REGISTRATION);
    }

    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter("MyDocument");

        String jobName = getString(R.string.app_name) + " Print Test";

        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }

}
