package com.example.owner.superdiary.Activity.MainActivity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.owner.superdiary.R;
import com.example.owner.superdiary.Activity.MainActivity.Date;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;


public class fragment2 extends Fragment {
    View fragment = null;
    MaterialCalendarView calendarView = null;
    Button todayButton = null;
    fragment1 one;

    public void setFragmentOne(fragment1 one) {
        this.one = one;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment == null) {
            fragment = View.inflate(getActivity(), R.layout.fragment_two, null);
            calendarView = (MaterialCalendarView) fragment.findViewById(R.id.calendar);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            calendarView.setDateSelected(Calendar.getInstance(), true);
            calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay selectedDate, boolean selected) {
                    Calendar date = selectedDate.getCalendar();
                    one.setDate(date);
                    one.loadData(date);

                    Date sharedData = ((Date)getActivity().getApplication());
                    ViewPager vp = sharedData.viewPager;
                    vp.setCurrentItem(vp.getCurrentItem() - 1);
                }
            });
            todayButton = (Button) fragment.findViewById(R.id.todayButton);
            todayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar date = Calendar.getInstance();
                    one.setDate(date);
                    one.loadData(date);

                    calendarView.clearSelection();
                    calendarView.setDateSelected(Calendar.getInstance(), true);
                    Date sharedData = ((Date)getActivity().getApplication());
                    ViewPager vp = sharedData.viewPager;
                    vp.setCurrentItem(vp.getCurrentItem() - 1);
                }
            });
        }
        return fragment;
    }
}