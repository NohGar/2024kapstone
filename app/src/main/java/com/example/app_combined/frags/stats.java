package com.example.app_combined.frags;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_combined.R;
import com.example.app_combined.three_works.CircleDecorator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class stats extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃을 inflate하고 View 객체 반환
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // BarChart 초기화
        BarChart barChart = view.findViewById(R.id.barchart1);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 30));
        entries.add(new BarEntry(1, 60));
        entries.add(new BarEntry(2, 45));
        entries.add(new BarEntry(4, 33));
        entries.add(new BarEntry(5, 52));
        entries.add(new BarEntry(6, 98));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(new int[]{
                R.color.recent_day3, R.color.recent_day2, R.color.recent_day1,
                R.color.recent_day3, R.color.recent_day2, R.color.recent_day1,
        }, requireContext());

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        data.setBarWidth(0.4f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(4);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] labels = new String[]{
                    "지난달", " ", " ", " ",
                    "이번달", " ", " ", " ",
            };

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (value >= 0 && value < labels.length) {
                    return labels[(int) value];
                }
                return "";
            }
        });

        barChart.invalidate(); // 그래프 새로고침

        // MaterialCalendarView 초기화
        MaterialCalendarView calendarView = view.findViewById(R.id.calendarView);

        CalendarDay date1 = CalendarDay.from(2024, 10, 12);
        CalendarDay date2 = CalendarDay.from(2024, 10, 15);
        CalendarDay date3 = CalendarDay.from(2024, 10, 21);

        CircleDecorator decorator = new CircleDecorator(Arrays.asList(date1, date2, date3), requireActivity());
        calendarView.addDecorator(decorator);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                createPopupWindow(view, date);
            }
        });

        return view;
    }

    private void createPopupWindow(View rootView, CalendarDay date) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popUpView = inflater.inflate(R.layout.mainpopup, null);

        TextView dateTextView = popUpView.findViewById(R.id.popup_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(date.getDate());
        dateTextView.setText(formattedDate);

        int width = (int) getResources().getDimension(R.dimen.popup_width);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

        // 팝업창이 열릴 때 배경을 흐리게 설정
        ViewGroup rootLayout = getActivity().findViewById(android.R.id.content);
        rootLayout.setAlpha(0.7f);  // 배경 흐리게 하기 (50% 투명도)

        // 팝업창 닫을 때 배경을 원래대로 복구
        popupWindow.setOnDismissListener(() -> {
            rootLayout.setAlpha(1.0f);  // 배경 원래대로 복구
        });

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }
}