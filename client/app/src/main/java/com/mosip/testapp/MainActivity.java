package com.mosip.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click_start(View view) {
        openStart();
    }

    public void openStart() {
        OpenDialog od1 = new OpenDialog();
        od1.show(getSupportFragmentManager(),"od1");
    }
}