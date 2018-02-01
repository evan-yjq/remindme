package com.evan.remindme.addedittask.domain.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.evan.remindme.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

import static com.evan.remindme.addedittask.TasksCircleType.CIRCLE_M;
import static com.evan.remindme.addedittask.TasksCircleType.CIRCLE_W;
import static com.evan.remindme.addedittask.TasksCircleType.CIRCLE_Y;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午9:11
 */
public class SelectDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable selectionDrawable;
    private int circleType;
    private CalendarDay day;

    public SelectDecorator(CalendarDay day, Activity context,int circle){
        this.day = day;
        selectionDrawable = context.getResources().getDrawable(R.drawable.calendar_select);
        this.circleType = circle;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (day.isBefore(this.day)||day.equals(this.day))
            return false;
        day.copyTo(calendar);
        switch (circleType){
            case CIRCLE_W:
                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                this.day.copyTo(calendar);
                return weekDay == calendar.get(Calendar.DAY_OF_WEEK);
            case CIRCLE_M:
                int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
                this.day.copyTo(calendar);
                return monthDay == calendar.get(Calendar.DAY_OF_MONTH);
            case CIRCLE_Y:
                int yearDay = calendar.get(Calendar.DAY_OF_YEAR);
                this.day.copyTo(calendar);
                return yearDay == calendar.get(Calendar.DAY_OF_YEAR);
            default:
                return false;
        }
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(selectionDrawable);
    }
}
