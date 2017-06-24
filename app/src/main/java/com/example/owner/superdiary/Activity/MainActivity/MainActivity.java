package com.example.owner.superdiary.Activity.MainActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.owner.superdiary.Activity.AboutActivity;
import com.example.owner.superdiary.Activity.IntroductionActivity;
import com.example.owner.superdiary.DataCollector.LocationRecordService;
import com.example.owner.superdiary.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements PickDateFragment.OnDateSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private ViewPager viewPager;

    private LocationRecordService ds;
    private ServiceConnection sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();
        initView();
        initEvent();
    }

    private void initService() {
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MainActivity.this.ds=((LocationRecordService.MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(this, LocationRecordService.class);
        startService(intent);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tl);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl);

        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        String[] lvs = {"介绍", "关于"};
        lvLeftMenu = (ListView) findViewById(R.id.lv);
        ArrayAdapter arrayAdapter;
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
    }

    private void initEvent() {
        //创建返回键 并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, IntroductionActivity.class);
                    startActivity(intent);
                } else
                if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void OnDateSelected(Calendar date) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        CardDisplayFragment one = (CardDisplayFragment)((MyAdapter)viewPager.getAdapter()).getItem(0);
        one.loadData(date);
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        String[] tabNames = {"日记一览", "历史日记"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        CardDisplayFragment one = new CardDisplayFragment();
        PickDateFragment two = new PickDateFragment();

        @Override
        public Fragment getItem(int position) {
            if(0 == position){
                return one;
            }else{
                return two;
            }
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
    }
}