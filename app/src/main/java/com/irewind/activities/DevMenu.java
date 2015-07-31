package com.irewind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.irewind.R;

public class DevMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_menu);
    }


    public void CamRec(View v) {
        Intent i = new Intent(DevMenu.this, CameraRecActivity.class);
        startActivity(i);

    }

    public void FakeBeacon(View v) {
        Intent i = new Intent(DevMenu.this, FakeBeaconActivity.class);
        startActivity(i);
    }

    public void FakeCamera(View v) {
        Intent i = new Intent(DevMenu.this, FakeCameraActivity.class);
        startActivity(i);
    }

}
