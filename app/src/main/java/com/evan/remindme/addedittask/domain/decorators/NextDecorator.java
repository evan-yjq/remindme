package com.evan.remindme.addedittask.domain.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.evan.remindme.R;
import com.evan.remindme.addedittask.TasksCircleType;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

import static com.evan.remindme.addedittask.TasksCircleType.*;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午10:47
 */
public class NextDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable nextDrawable;
    private int circleType;
    private CalendarDay day;

    private boolean next = true;

    public NextDecorator(CalendarDay day,Activity context,int circle) {
        nextDrawable = context.getResources().getDrawable(R.drawable.calendar_today);
        this.day = day;
        this.circleType = circle;
    }


    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if ((day.isAfter(CalendarDay.today())||day.equals(CalendarDay.today()))
                &&(day.isAfter(this.day)||day.equals(this.day))){
            day.copyTo(calendar);
            switch (circleType){
                case CIRCLE_W:
                    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                    this.day.copyTo(calendar);
                    if (weekDay == calendar.get(Calendar.DAY_OF_WEEK)&&next){
                        next = false;
                        return true;
                    }else return false;
                case CIRCLE_M:
                    int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
                    this.day.copyTo(calendar);
                    if (monthDay == calendar.get(Calendar.DAY_OF_MONTH)&&next){
                        next = false;
                        return true;
                    }else return false;
                case CIRCLE_Y:
                    int yearDay = calendar.get(Calendar.DAY_OF_YEAR);
                    this.day.copyTo(calendar);
                    if (yearDay == calendar.get(Calendar.DAY_OF_YEAR)&&next){
                        next = false;
                        return true;
                    }else return false;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(nextDrawable);
    }
}
