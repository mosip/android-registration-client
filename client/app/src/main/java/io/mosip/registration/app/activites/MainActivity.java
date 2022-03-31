package io.mosip.registration.app.activites;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;


public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click_sync_masterdata(View view) {
        Toast.makeText(this, "Synced masterdata successfully", Toast.LENGTH_LONG).show();
    }

    public void click_new_registration(View view) {
        Intent intent = new Intent(this, DemographicsActivity.class);
        startActivity(intent);
    }

    public void click_upload_packet(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }
}