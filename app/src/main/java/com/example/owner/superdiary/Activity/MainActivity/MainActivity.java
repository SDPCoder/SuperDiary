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
import com.example.owner.superdiary.Utils.Date;
import com.example.owner.superdiary.DataCollector.LocationRecordService;
import com.example.owner.superdiary.R;
import com.example.owner.superdiary.Activity.MainActivity.fragments.fragment1;
import com.example.owner.superdiary.Activity.MainActivity.fragments.fragment2;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements fragment2.OnDateSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"介绍", "关于"};
    private String[] tabNames = {"日记一览", "历史日记"};
    private ArrayAdapter arrayAdapter;
    private ViewPager viewPager;

    private LocationRecordService ds;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.ds=((LocationRecordService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void my_bindServices() {
        Intent intent = new Intent(this, LocationRecordService.class);
        startService(intent);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews(); //获取控件

        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);

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

        //类似listview
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        ((Date)getApplication()).viewPager = viewPager;

        my_bindServices();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl);
        lvLeftMenu = (ListView) findViewById(R.id.lv);
        viewPager = (ViewPager) findViewById(R.id.vp);
    }

    @Override
    public void OnDateSelected(Calendar date) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        fragment1 one = (fragment1)((MyAdapter)viewPager.getAdapter()).getItem(0);
        one.setDate(date);
        one.loadData(date);
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        fragment1 one = new fragment1();
        fragment2 two = new fragment2();

        @Override
        public Fragment getItem(int position) {
            two.setFragmentOne(one);

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