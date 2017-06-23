package com.example.owner.superdiary.DataCollector;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.owner.superdiary.DAO.MyDBHelper;

import java.lang.Math;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class LocationRecordService extends Service{
    private final String serviceName = "LocationRecordService";
    private MyBinder binder = new MyBinder();
    LocationManager mLocationmanger;
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    LocationProvider provider;
    Location cur_location = null;
    MyDBHelper mydb;
    int duration;
    Handler mhandler = new Handler();

    public LocationRecordService() {
        Log.i(serviceName, "Initializing");
    }

    public class MyBinder extends Binder {
        public LocationRecordService getService() {
            return LocationRecordService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initStepCountService() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Toast.makeText(getApplicationContext(), "Walking", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(sensorEventListener, stepSensor, Sensor.TYPE_STEP_COUNTER, SensorManager.SENSOR_DELAY_UI);
    }

    private boolean isKitkatWithStepSensor() {
        // BEGIN_INCLUDE(iskitkatsensor)
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = this.getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        // END_INCLUDE(iskitkatsensor)
    }

    private void initLocService() {
        mLocationmanger = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationmanger.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = mLocationmanger.getProvider(LocationManager.GPS_PROVIDER);
        } else if (mLocationmanger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = mLocationmanger.getProvider(LocationManager.NETWORK_PROVIDER);
        } else {
            Toast.makeText(LocationRecordService.this, "Error: No location provider enabled!", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cur_location = mLocationmanger.getLastKnownLocation(provider.getName());
        duration = 0;
        initDurationAutoIncreaser();
        mLocationmanger.removeUpdates(mLocationListener);
        mLocationmanger.requestLocationUpdates(provider.getName(), 0, 100, mLocationListener);
    }

    private void initDurationAutoIncreaser() {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                duration += 1;
                mhandler.postDelayed(this, 60*1000);
            }
        }, 60*1000);
    }

    void saveCurrentLocationToDB() {
        Log.i("LocationListener", "Saving location longitude:" + String.valueOf(cur_location.getLongitude()) +
                " latitude:" + String.valueOf(cur_location.getLatitude() + " duration:" + String.valueOf(duration)));
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("longitude", cur_location.getLongitude());
        record.put("latitude", cur_location.getLatitude());
        record.put("duration", duration);
        mydb.insertNow(record);
    }
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            if (cur_location != null) {
                saveCurrentLocationToDB();
            }
            cur_location = loc;
            duration = 0;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        private double distance(Location loc1, Location loc2) {
            double long1, lat1, long2, lat2; // t:经度 x:维度
            long1 = loc1.getLongitude();
            lat1 = loc1.getLatitude();
            long2 = loc2.getLongitude();
            lat2 = loc2.getLatitude();

            double a, b, R;
            R = 6378137; // 地球半径
            lat1 = lat1 * Math.PI / 180.0;
            lat2 = lat2 * Math.PI / 180.0;
            a = lat1 - lat2;
            b = (long1 - long2) * Math.PI / 180.0;
            double d;
            double sa2, sb2;
            sa2 = Math.sin(a / 2.0);
            sb2 = Math.sin(b / 2.0);
            d = 2
                    * R
                    * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                    * Math.cos(lat2) * sb2 * sb2));
            return d;
        }
    };

    @Override
    public void onDestroy() {
        Log.i(serviceName, "destroying services");
        if (cur_location != null)
            saveCurrentLocationToDB();
        mydb.close();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocService();
        initStepCountService();
        initDBHelper();
    }

    private void initDBHelper() {
        Map<String, String> table_header = new HashMap<>();
        table_header.put("latitude", "REAL");
        table_header.put("longitude", "REAL");
        table_header.put("duration", "INTEGER");
        mydb = new MyDBHelper(getApplicationContext(), "superdiary_test001.db", "location", table_header,null, 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "SuperDiary Service is running", Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), "currentTime:" + new Date(intent.getLongExtra("time", 0)).toString(), Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }
}