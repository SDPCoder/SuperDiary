package com.example.owner.superdiary.Activity.MainActivity;

import android.app.Application;
import android.support.v4.view.ViewPager;

public class Date extends Application{
    public ViewPager viewPager;
    public int year=1970;
    public int month=1;
    public int day=1;
    @Override
    public void onCreate(){
        super.onCreate();
    }
}