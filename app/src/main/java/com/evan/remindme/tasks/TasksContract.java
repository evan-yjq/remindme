package com.evan.remindme.tasks;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午6:03
 */
public interface TasksContract {

    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        void showClassifyTasks(Map<Classify,List<Task>> tasks);

        void showTimeTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showNoTasks();

        void showNoClassifyTasks();

        void showTaskMarkedTurnOn();

        void showTaskMarkedTurnOff();

        void showSuccessfullySavedMessage();

        void showLoadingTasksError();

        void showMessage(String message);

        void showDisplayingPopUpMenu();

        boolean isActive();

    }

    interface Presenter extends BasePresenter{

        boolean isFirstLoad();

        void setFirstLoad(boolean firstLoad);

        void result(int requestCode, int resultCode);

        void setDisplay(TasksDisplayType requestType);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        void turnOnTask(@NonNull Task turnOnTask);

        void turnOffTask(@NonNull Task turnOffTask);

        void openClassify(@NonNull Classify openClassify);

        void closeClassify(@NonNull Classify closeClassify);

        TasksDisplayType getDisplaying();
    }
}
