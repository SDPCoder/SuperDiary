package com.example.owner.superdiary.Activity.MainActivity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.owner.superdiary.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;


public class fragment2 extends Fragment {
    OnDateSelectedListener mListener;

    View fragment = null;
    MaterialCalendarView calendarView = null;
    Button todayButton = null;
    fragment1 one;

    public void setFragmentOne(fragment1 one) {
        this.one = one;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment == null) {
            fragment = View.inflate(getActivity(), R.layout.activity_main_fragment_two, null);
            calendarView = (MaterialCalendarView) fragment.findViewById(R.id.calendar);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            calendarView.setDateSelected(Calendar.getInstance(), true);
            calendarView.setOnDateChangedListener(new com.prolificinteractive.materialcalendarview.OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay selectedDate, boolean selected) {
                    Calendar date = selectedDate.getCalendar();
                    mListener.OnDateSelected(date);
                }
            });
            todayButton = (Button) fragment.findViewById(R.id.todayButton);
            todayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar date = Calendar.getInstance();
                    calendarView.clearSelection();
                    calendarView.setDateSelected(Calendar.getInstance(), true);
                    mListener.OnDateSelected(date);
                }
            });
        }
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDateSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface OnDateSelectedListener {
        public void OnDateSelected(Calendar date);
    }
}