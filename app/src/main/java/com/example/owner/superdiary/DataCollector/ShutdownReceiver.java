package com.example.owner.superdiary.DataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.owner.superdiary.Utils.MyDBHelper;

import java.util.HashMap;
import java.util.Map;

public class ShutdownReceiver extends BroadcastReceiver {
    public ShutdownReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ShutdownReceiver", "Receive shutdown broadcast");

        Map<String, String> table_header = new HashMap<>();
        MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "shutdownTime", table_header,null, 1);
        Map<String, Object> record = new HashMap<>();
        mydb.insertNow(record);
        mydb.close();
    }
}