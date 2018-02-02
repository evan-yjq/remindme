package com.evan.remindme.addedittask;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.allclassify.domain.model.Classify;

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

        void setSelectClassify(Classify classify);

        void setSelectClassify(int i);

        void setClassifySpinner(List<Classify> classifies);

        void showTimePickerDialog(Date date);

        void setDate(Date date);

        void showTasksList();
    }

    interface Presenter extends BasePresenter {

        void save(String title);

        void getDateDialog();

        void populateTask();

        boolean isDataMissing();

        int getCircleType();

        int getRepeatType();

        void setCircleType(int circle);

        void setRepeatType(int repeat);

        void saveClassify(@NonNull String name);

        Long getClassifyId();

        void setClassifyId(Long id);

        Date getDate();

        void setDate(Date date);
    }

}
