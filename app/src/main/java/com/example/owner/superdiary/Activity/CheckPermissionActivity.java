package com.example.owner.superdiary.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.owner.superdiary.R;
import com.example.owner.superdiary.Activity.MainActivity.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class CheckPermissionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
//                            Toast.makeText(CheckPermissionActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CheckPermissionActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Handler mHandler = new Handler();
                            Toast.makeText(CheckPermissionActivity.this, "Permission Denied. App will finish in 3 secs...", Toast.LENGTH_SHORT).show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                        }
                    }
                });
    }
}