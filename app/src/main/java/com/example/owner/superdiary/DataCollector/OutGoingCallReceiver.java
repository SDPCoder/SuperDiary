package com.example.owner.superdiary.DataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.owner.superdiary.Utils.MyDBHelper;

import java.util.HashMap;
import java.util.Map;

public class OutGoingCallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();
        Log.i("OutGoingCallReceiver","拨打电话了: " + number);

        Map<String, String> table_header = new HashMap<>();
        table_header.put("type", "TEXT");
        table_header.put("phoneNum", "TEXT");
        MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "calls", table_header,null, 1);
        Map<String, Object> record = new HashMap<>();
        record.put("phoneNum", number);
        record.put("type", "out");
        mydb.insertNow(record);
        mydb.close();
    }
}