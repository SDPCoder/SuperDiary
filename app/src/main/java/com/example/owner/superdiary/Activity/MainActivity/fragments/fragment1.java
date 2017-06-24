package com.example.owner.superdiary.Activity.MainActivity.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;

import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.owner.superdiary.Activity.CheckPasswordActivity;
import com.example.owner.superdiary.DAO.MyDBHelper;
import com.example.owner.superdiary.Activity.PasswordSettingActivityOne;
import com.example.owner.superdiary.Activity.PasswordSettingActivityTwo;
import com.example.owner.superdiary.Activity.PictureActivity;
import com.example.owner.superdiary.R;
import com.example.owner.superdiary.Activity.EditActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class fragment1 extends Fragment {
    View fragment = null;
    CardView bootTime_card, shutdownTime_card, unlockTime_card, moodIndex_card;
    CardView frequentContact_card, mapview_card;
    TextView bootTime_text, shutdownTime_text, unlockTime_text, moodIndex_text;
    TextView frequentContact_text;
    TextureMapView mapView;
    Context context;
    Handler mhandler;
    MyDBHelper bootTimeDB, shutdownTimeDB, unlockTimeDB, callsDB, moodDB, locDB;
    String today;
    Calendar date;
    TextView dateTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment == null) {
            context = getActivity().getApplicationContext();
            SDKInitializer.initialize(context);
            fragment = View.inflate(getActivity(), R.layout.fragment_one, null);
            getReferences();
            initDB();
            initMap();
            date = Calendar.getInstance();
            today = String.valueOf(date.get(Calendar.YEAR)) + "_" + String.valueOf(date.get(Calendar.MONTH) + 1) + "_" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
            dateTitle.setText(String.valueOf(date.get(Calendar.YEAR)) + "年 " + String.valueOf(date.get(Calendar.MONTH) + 1) + "月 " + String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + "日");
        }

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DiaryPwd", MODE_PRIVATE);
        FloatingActionButton fab1 = (FloatingActionButton) fragment.findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) fragment.findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) fragment.findViewById(R.id.fab3);

        final FloatingActionMenu menuRed = (FloatingActionMenu) fragment.findViewById(R.id.menu_red);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                String curPwd = sharedPreferences.getString(today, null);
                Bundle bundle = new Bundle();
                bundle.putString("today", today);
                if (curPwd == null) {
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    bundle.putString("curPwd", curPwd);
                    bundle.putInt("fab", 1);
                    Intent intent = new Intent(getActivity(), CheckPasswordActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                String curPwd = sharedPreferences.getString(today, null);
                Bundle bundle = new Bundle();
                bundle.putString("today", today);
                if (curPwd == null) {
                    Intent intent = new Intent(getActivity(), PictureActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    bundle.putString("curPwd", curPwd);
                    bundle.putInt("fab", 2);
                    Intent intent = new Intent(getActivity(), CheckPasswordActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                String curPwd = sharedPreferences.getString(today, null);
                Bundle bundle = new Bundle();
                bundle.putString("today", today);
                Log.i("today", today);
                if (curPwd == null) {
                    Intent intent = new Intent(getActivity(), PasswordSettingActivityOne.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), PasswordSettingActivityTwo.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        return fragment;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void loadData(Calendar date) {
        today = String.valueOf(date.get(Calendar.YEAR)) + "_" + String.valueOf(date.get(Calendar.MONTH) + 1) + "_" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        dateTitle.setText(String.valueOf(date.get(Calendar.YEAR)) + "年 " + String.valueOf(date.get(Calendar.MONTH) + 1) + "月 " + String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + "日");
        Log.i("loadData", "Called, date: " + today);
        loadBootTime(date);
        loadShutdownTime(date);
        loadUnlockTime(date);
        loadMoodIndex(date);
        loadFrequentContact(date);
        loadStayLoc(date);
    }
    @Override
    public void onStart() {
        super.onStart();
        loadData(this.date);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void getReferences() {
        bootTime_text = (TextView) fragment.findViewById(R.id.bootTime_text);
        shutdownTime_text = (TextView) fragment.findViewById(R.id.shutdownTime_text);
        unlockTime_text = (TextView) fragment.findViewById(R.id.unlockTime_text);
        moodIndex_text = (TextView) fragment.findViewById(R.id.moodIndex_text);
        frequentContact_text = (TextView) fragment.findViewById(R.id.frequentContact_text);
        dateTitle = (TextView) fragment.findViewById(R.id.dateTitle);
        mapView = (TextureMapView) fragment.findViewById(R.id.bmapView);

        bootTime_card = (CardView) fragment.findViewById(R.id.bootTime_card);
        shutdownTime_card = (CardView) fragment.findViewById(R.id.shutdownTime_card);
        unlockTime_card = (CardView) fragment.findViewById(R.id.unlockTime_card);
        moodIndex_card = (CardView) fragment.findViewById(R.id.moodIndex_card);
        frequentContact_card = (CardView) fragment.findViewById(R.id.frequentContact_card);
        mapview_card = (CardView) fragment.findViewById(R.id.mapview_card);

        mhandler = new Handler();
    }

    private void initDB() {
        Map<String, String> table_header = new HashMap<>();
        bootTimeDB = new MyDBHelper(context, "superdiary_test001.db", "bootTime", table_header, null, 1);

        Map<String, String> table_header2 = new HashMap<>();
        shutdownTimeDB = new MyDBHelper(context, "superdiary_test001.db", "shutdownTime", table_header2, null, 1);

        Map<String, String> table_header3 = new HashMap<>();
        unlockTimeDB = new MyDBHelper(context, "superdiary_test001.db", "unlockTime", table_header3, null, 1);

        Map<String, String> table_header4 = new HashMap<>();
        table_header4.put("type", "TEXT");
        table_header4.put("phoneNum", "TEXT");
        callsDB = new MyDBHelper(context, "superdiary_test001.db", "calls", table_header4, null, 1);

        Map<String, String> table_header5 = new HashMap<>();
        table_header5.put("emotion", "INTEGER");
        moodDB = new MyDBHelper(context, "superdiary_test001.db", "emotion", table_header5, null, 1);

        Map<String, String> table_header6 = new HashMap<>();
        table_header6.put("latitude", "REAL");
        table_header6.put("longitude", "REAL");
        table_header6.put("duration", "INTEGER");
        locDB = new MyDBHelper(context, "superdiary_test001.db", "location", table_header6, null, 1);
    }

    private void loadData() {
        Calendar today = Calendar.getInstance();
        loadData(today);
    }

    private void loadBootTime(Calendar date) {
        List<Map<String, Object>> records = bootTimeDB.query(date);
        if (records.size() == 0) {
            bootTime_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，起床时间卡片被隐藏", Toast.LENGTH_SHORT).show();
            return;
        }

        long earliestTime = Long.MAX_VALUE;
        for (Map<String, Object> record : records) {
            long time = (long) Long.valueOf((String) record.get("time"));
            if (time < earliestTime)
                earliestTime = time;
        }
        Calendar bootTime = Calendar.getInstance();
        bootTime.setTime(new Date(earliestTime));

        int h = bootTime.get(Calendar.HOUR_OF_DAY);
        int m = bootTime.get(Calendar.MINUTE);
        String str = "";

        if (h < 6) str = "凌晨 ";
        else if (h < 12) str = "上午 ";
        else if (h == 12) str = "中午 ";
        else if (h < 18) str = "下午 ";
        else if (h == 18) str = "傍晚 ";
        else str = "晚上 ";

        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        bootTime_text.setText(str + format.format(bootTime.getTime()));
        bootTime_card.setVisibility(View.VISIBLE);
    }

    private void loadShutdownTime(Calendar date) {
        List<Map<String, Object>> records = shutdownTimeDB.query(date);
        if (records.size() == 0) {
            shutdownTime_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，睡觉时间卡片被隐藏", Toast.LENGTH_SHORT).show();

            return;
        }

        long latestTime = Long.MIN_VALUE;
        for (Map<String, Object> record : records) {
            long time = (long) Long.valueOf((String) record.get("time"));
            if (time > latestTime)
                latestTime = time;
        }
        Calendar shutdownTime = Calendar.getInstance();
        shutdownTime.setTime(new Date(latestTime));

        int h = shutdownTime.get(Calendar.HOUR_OF_DAY);
        int m = shutdownTime.get(Calendar.MINUTE);
        String str = "";

        if (h < 6) str = "凌晨 ";
        else if (h < 12) str = "上午 ";
        else if (h == 12) str = "中午 ";
        else if (h < 18) str = "下午 ";
        else if (h == 18) str = "傍晚 ";
        else str = "晚上 ";

        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        shutdownTime_text.setText(str + format.format(shutdownTime.getTime()));
        shutdownTime_card.setVisibility(View.VISIBLE);
    }

    private void loadUnlockTime(Calendar date) {
        List<Map<String, Object>> records = unlockTimeDB.query(date);
        if (records.size() == 0) {
            unlockTime_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，解锁次数卡片被隐藏", Toast.LENGTH_SHORT).show();

            return;
        }

        unlockTime_card.setVisibility(View.VISIBLE);
        unlockTime_text.setText(String.valueOf(records.size()) + "次");
    }

    private void loadMoodIndex(Calendar date) {
        List<Map<String, Object>> records = moodDB.query(date);
        if (records.size() == 0) {
            moodIndex_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，心情指数卡片被隐藏", Toast.LENGTH_SHORT).show();

            return;
        }
        int happy = 0, normal = 0, sad = 0;
        for (Map<String, Object> record : records) {
            int moodValue = (int) record.get("emotion");
            switch (moodValue) {
                case 100:
                    happy += 1;
                    break;
                case 0:
                    normal += 1;
                    break;
                case -100:
                    sad += 1;
                    break;
            }
        }

        moodIndex_text.setText(String.valueOf(Math.floor((100 * happy + 50 * normal + 0 * sad) / (happy + normal + sad))));
        moodIndex_card.setVisibility(View.VISIBLE);

    }

    private void loadFrequentContact(Calendar date) {
        List<Map<String, Object>> records = callsDB.query(date);
        if (records.size() == 0) {
            frequentContact_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，今日联系最多的人卡片被隐藏", Toast.LENGTH_SHORT).show();

            return;
        }

        String frequentContactName = "";
        HashMap<String, Integer> num_and_times = new HashMap<>();
        for (Map<String, Object> record : records) {
            String phoneNum = (String) record.get("phoneNum");
            if (num_and_times.get(phoneNum) == null)
                num_and_times.put(phoneNum, 1);
            else
                num_and_times.put(phoneNum, num_and_times.get(phoneNum) + 1);
        }

        String max_phoneNum = "";
        int max_times = Integer.MIN_VALUE;
        for (String phoneNum : num_and_times.keySet()) {
            int times = num_and_times.get(phoneNum);
            if (times > max_times) {
                max_times = times;
                max_phoneNum = phoneNum;
            }
        }

        ContentResolver cr = getActivity().getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNum_cp = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .replace(" ", "").replace("-", "");
            if (phoneNum_cp.equals(max_phoneNum)) {
                frequentContactName = name;
                break;
            }
        }

        if (frequentContactName.equals("")) {
            frequentContact_text.setText("陌生人:" + max_phoneNum);
        } else {
            frequentContact_text.setText(frequentContactName);
        }

        frequentContact_card.setVisibility(View.VISIBLE);
    }

    private void loadStayLoc(Calendar date) {
        List<Map<String, Object>> records = locDB.query(date);
        if (records.size() == 0) {
            mapview_card.setVisibility(View.GONE);
//            Toast.makeText(context, "由于数据库中尚无数据，今日逗留地点卡片被隐藏", Toast.LENGTH_SHORT).show();

            return;
        }

        int max_duration = Integer.MIN_VALUE;
        double max_longitude = 0, max_latitude = 0;
        for (Map<String, Object> record : records) {
            int duration = (int) record.get("duration");
            if (duration > max_duration) {
                max_longitude = (double) record.get("longitude");
                max_latitude = (double) record.get("latitude");
                max_duration = duration;
            }
        }

        final double final_max_longitude = max_longitude;
        final double final_max_latitude = max_latitude;
        Log.i("mapLongitude", String.valueOf(final_max_longitude));
        Log.i("mapLatitude", String.valueOf(final_max_latitude));
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLocOnMapView(final_max_longitude, final_max_latitude);
            }
        }, 500);

        mapview_card.setVisibility(View.VISIBLE);
    }

    private void showLocOnMapView(double longitude, double latitude) {
        updateMap(longitude, latitude);
        centerView(longitude, latitude);
    }

    private void initMap() {
        Bitmap bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.mipmap.pointer),
                100, 100, true);
        BitmapDescriptor bitmapD = BitmapDescriptorFactory.fromBitmap(bitmap);

        mapView.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, bitmapD);
        mapView.getMap().setMyLocationConfigeration(config);

        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
        UiSettings settings=mapView.getMap().getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setCompassEnabled(false);
    }

    private LatLng getTransformedLoc(double longitude, double latitude) {
        CoordinateConverter mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(new LatLng(latitude, longitude));
        return mConverter.convert();
    }

    private void updateMap(double longitude, double latitude) {
        MyLocationData.Builder data = new MyLocationData.Builder();
        LatLng desLatLng = getTransformedLoc(longitude, latitude);
        data.latitude(desLatLng.latitude);
        data.longitude(desLatLng.longitude);
        mapView.getMap().setMyLocationData(data.build());

        float zoomLevel = 19;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        mapView.getMap().setMapStatus(u);
    }

    private void centerView(double longitude, double latitude) {
        MapStatus mMapstatus = new MapStatus.Builder().target(getTransformedLoc(longitude, latitude)).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapstatus);
        mapView.getMap().setMapStatus(mapStatusUpdate);
    }
}