package com.example.owner.superdiary.DataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.owner.superdiary.Utils.MyDBHelper;

import java.util.HashMap;
import java.util.Map;

public class UnlockReceiver extends BroadcastReceiver {
    public UnlockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("UnlockReceiver", "Receive unlock broadcast");

        Map<String, String> table_header = new HashMap<>();
        MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "unlockTime", table_header,null, 1);
        Map<String, Object> record = new HashMap<>();
        mydb.insertNow(record);
        mydb.close();
    }
}