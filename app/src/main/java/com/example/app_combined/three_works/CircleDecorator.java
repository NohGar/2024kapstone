package com.example.app_combined.three_works;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.example.app_combined.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class CircleDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;
    private TextView textView;
    public CircleDecorator(Collection<CalendarDay> dates, Activity context) {
        drawable = context.getDrawable(R.drawable.ic_circle3);

        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

}