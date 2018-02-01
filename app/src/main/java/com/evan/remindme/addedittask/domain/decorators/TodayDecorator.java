package com.evan.remindme.addedittask.domain.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.evan.remindme.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午7:52
 */
public class TodayDecorator implements DayViewDecorator {
    private CalendarDay date;
    private final Drawable backgroundDrawable;

    public TodayDecorator(Activity context){
        date = CalendarDay.today();
        backgroundDrawable = context.getResources().getDrawable(R.drawable.calendar_today);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(backgroundDrawable);
    }
}
