package io.mosip.registration.app.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;

import java.util.ArrayList;
import java.util.List;

public class AcknowledgementActivity extends DaggerAppCompatActivity {

    private static final String TAG = AcknowledgementActivity .class.getSimpleName();

    private WebView webView;
    private List<PrintJob> printJobs = new ArrayList<>();

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
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request)
            {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                createWebPrintJob(view);
                webView = null;
            }
        });

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ack_slip);

        Log.i(TAG, "Starting activity was successfull");

        try {
            String htmlDocument = getIntent().getStringExtra("content");
            webView.loadDataWithBaseURL(null, htmlDocument,
                    "text/HTML", "UTF-8", null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set the acknowledgement content", e);
        }
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
