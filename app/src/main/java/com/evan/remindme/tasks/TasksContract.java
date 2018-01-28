package com.evan.remindme.tasks;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.sorts.domain.model.Sort;
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

        void showSortTasks(Map<Sort,List<Task>> tasks);

        void showTimeTasks(List<Task> tasks);

        void showAddTask(String name,Long id);

        void showTaskDetailsUi(String taskId);

        void showNoTasks();

        void showNoSortTasks();

        void showTaskMarkedTurnOn();

        void showTaskMarkedTurnOff();

        void showSuccessfullySavedMessage();

        void showLoadingTasksError();

//        void showDisplayBySortLabel();

//        void showDisplayByTimeLabel();

        void showMessage(String message);

        void showDisplayingPopUpMenu();

        boolean isActive();

    }

    interface Presenter extends BasePresenter{

        void save(@NonNull Task task);

        void result(int requestCode, int resultCode);

        void setDisplay(TasksDisplayType requestType);

        void loadTasks(boolean forceUpdate);

        void addNewTask(String name);

        void openTaskDetails(@NonNull Task requestedTask);

        void turnOnTask(@NonNull Task turnOnTask);

        void turnOffTask(@NonNull Task turnOffTask);

        void openSort(@NonNull Sort openSort);

        void closeSort(@NonNull Sort closeSort);

        TasksDisplayType getDisplaying();
    }
}
