package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;


public class LoginActivity extends DaggerAppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    SyncRestUtil syncRestFactory;

    @Inject
    SyncRestService syncRestService;

    @Inject
    LoginService loginService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.app_name);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginButton.setEnabled(validateLogin(username, password));
            }
        };

        findViewById(R.id.info_logo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
        });

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                //validate form
                if(validateLogin(username, password)){
                    doLogin(username, password, loadingProgressBar);
                }
                loginButton.setEnabled(false);
            }
        });
    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(LoginActivity.this, R.string.username_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(LoginActivity.this, R.string.password_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!loginService.isValidUserId(username)) {
            Toast.makeText(LoginActivity.this, R.string.invalid_username, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(final String username,final String password, final ProgressBar loadingProgressBar){
        //TODO check if the machine is online, if offline check password hash locally
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if(response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if(error == null) {
                        try {
                            loginService.saveAuthToken(wrapper.getResponse());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            return;
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to save auth token", e);
                        }
                    }
                    Log.e(TAG, response.raw().toString());
                    Toast.makeText(LoginActivity.this, error == null ? getString(R.string.login_failed) : error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
