package com.evan.remindme.addedittask;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.tasks.domain.model.Task;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:28
 */
public interface AddEditTasksContract {

    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        boolean isActive();

        void showMessage(String message);

        void setTitle(String title);

        void setSelectRepeat(int repeat);

        void setSelectCircle(int circle);

        void setSelectSort(Sort sort);

        void setSelectSort(int i);

        void setSortSpinner(List<Sort> sorts);

        void showTimePickerDialog(Date date);

        void setDate(Date date);

        void showTasksList();
    }

    interface Presenter extends BasePresenter {

        void save(String title,Date time);

        void getDateDialog();

        void populateTask();

        boolean isDataMissing();

        int getCircleType();

        int getRepeatType();

        void setCircleType(int circle);

        void setRepeatType(int repeat);

        void saveSort(@NonNull String name);

        Long getSortId();

        void setSortId(Long id);

    }

}
