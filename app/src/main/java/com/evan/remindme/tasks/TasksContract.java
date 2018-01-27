package com.evan.remindme.tasks;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
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

        void showSortTasks(Map<String,List<Task>> tasks);

        void showTimeTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showNoTasks();

        void showNoSortTasks();

        void showTaskMarkedTurnOn();

        void showTaskMarkedTurnOff();

        void showSuccessfullySavedMessage();

        void showLoadingTasksError();

        void showDisplayBySortLabel();

        void showDisplayByTimeLabel();

        void showDisplayingPopUpMenu();

        boolean isActive();

    }

    interface Presenter extends BasePresenter{

        void save(@NonNull Task task);

        void result(int requestCode, int resultCode);

        void setDisplay(TasksDisplayType requestType);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        void turnOnTask(@NonNull Task turnOnTask);

        void turnOffTask(@NonNull Task turnOffTask);

        List<Task> openCloseSort(List<Task>tasks,long sortId);

        TasksDisplayType getDisplaying();
    }
}
