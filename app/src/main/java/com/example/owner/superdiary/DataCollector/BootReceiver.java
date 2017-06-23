package com.example.owner.superdiary.DataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.owner.superdiary.DAO.MyDBHelper;

import java.util.HashMap;
import java.util.Map;

// 开机自动启动后台服务
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context, LocationRecordService.class);
        Log.i("BootReceiver", "Receive boot broadcast");
        intent2.putExtra("time", System.currentTimeMillis());
        context.startService(intent2);

        Map<String, String> table_header = new HashMap<>();
        MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "bootTime", table_header,null, 1);
        Map<String, Object> record = new HashMap<>();
        mydb.insertNow(record);
    }
}