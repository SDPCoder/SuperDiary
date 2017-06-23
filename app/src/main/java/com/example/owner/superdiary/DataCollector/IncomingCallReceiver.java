package com.example.owner.superdiary.DataCollector;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.owner.superdiary.DAO.MyDBHelper;

import java.util.HashMap;
import java.util.Map;

public class IncomingCallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra("incoming_number");
        //如果是来电
        TelephonyManager tm =
                (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i("IncomingCallReceiver","来电: " + number);
                Map<String, String> table_header = new HashMap<>();
                table_header.put("type", "TEXT");
                table_header.put("phoneNum", "TEXT");
                MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "calls", table_header,null, 1);
                Map<String, Object> record = new HashMap<>();
                record.put("phoneNum", number);
                record.put("type", "in");
                mydb.insertNow(record);
                mydb.close();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                break;
        }

    }
}