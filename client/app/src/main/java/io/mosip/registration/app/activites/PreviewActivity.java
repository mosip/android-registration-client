package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.app.util.FileUtility;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewActivity extends DaggerAppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName();

    private WebView webView;

    @Inject
    RegistrationService registrationService;

    @Inject
    SyncRestUtil syncRestFactory;

    @Inject
    SyncRestService syncRestService;

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    TemplateService templateService;

    private String webViewContent;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        webViewContent = null;
        super.onCreate(savedInstanceState);
        startActivity();
    }

    private void startActivity() {
        webViewContent = null;
        setContentView(R.layout.activity_preview);
        webView = findViewById(R.id.registration_preview);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.preview_title);

        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            webViewContent = templateService.getTemplate(registrationDto, true);
            FileUtility.SaveFileInAppStorage(getApplicationContext(), registrationService.getRegistrationDto().getRId(), webViewContent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set the preview content", e);
            //TODO go to home on exception
        }

        final EditText usernameEditText = findViewById(R.id.packet_auth_username);
        final EditText passwordEditText = findViewById(R.id.packet_auth_pwd);
        final ProgressBar loadingProgressBar = findViewById(R.id.auth_loading);
        final Button button = findViewById(R.id.createpacket);
        button.setOnClickListener(v -> {
            auditManagerService.audit(AuditEvent.CREATE_PACKET_AUTH, Components.REGISTRATION);
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (validateLogin(username, password)) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                doPacketAuth(username, password, loadingProgressBar);
            }
        });

        webView.loadDataWithBaseURL(null, webViewContent, "text/HTML", "UTF-8", null);
        auditManagerService.audit(AuditEvent.LOADED_REGISTRATION_PREVIEW, Components.REGISTRATION);
    }


    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, R.string.username_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        //TODO check if the username is logged-in user and mapped to correct registration center
        return true;
    }

    private void doPacketAuth(final String username, final String password, final ProgressBar loadingProgressBar) {
        //TODO check if the machine is online, if offline check password hash locally
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if (error == null) {
                        submitForm(username);
                        return;
                    }
                    Log.e(TAG, response.raw().toString());
                    Toast.makeText(PreviewActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(PreviewActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                auditManagerService.audit(AuditEvent.CREATE_PACKET_AUTH_FAILED, Components.REGISTRATION);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PreviewActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitForm(String makerName) {
        try {
            String rId = registrationService.getRegistrationDto().getRId();
            registrationService.submitRegistrationDto(makerName);
            Intent intent = new Intent(PreviewActivity.this, AcknowledgementActivity.class);
            intent.putExtra(ClientConstants.R_ID, rId);
            startActivity(intent);
            Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.CREATE_PACKET_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed on registration submission", e);
            Toast.makeText(this, R.string.registration_fail, Toast.LENGTH_LONG).show();
        }
    }
}
