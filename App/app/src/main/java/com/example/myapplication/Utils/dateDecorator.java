package com.example.myapplication.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class dateDecorator implements DayViewDecorator {

    private final Context context;
    private List<CalendarDay> datesToMark;

    public dateDecorator(Context context, Collection<CalendarDay> datesToMark) {
        this.context = context;
        this.datesToMark = new ArrayList<>(datesToMark);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Return true if the day should be decorated (highlighted)
        return datesToMark.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Customize the appearance of the decorated day, e.g., change the background color
        view.setSelectionDrawable(context.getResources().getDrawable(R.drawable.date_selector));
    }
}
