package com.evan.remindme.data.source.remote;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.Objects;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午10:07
 */
public class TasksRemoteDataSource implements TasksDataSource{

    private static TasksRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa","默认");
        addTask("Finish bridge in Tacoma","新建");
        addTask("test","test");
    }

    private static void addTask(String title,String sortName) {
        Task newTask = new Task(title,sortName,new Date());
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    // 防止直接实例化。
    private TasksRemoteDataSource() {}

    @Override
    public void turnOnTask(@NonNull Task task) {
        updateTask(task);
    }

    @Override
    public void turnOnTask(@NonNull String taskId) {

    }

    @Override
    public void turnOffTask(@NonNull Task task) {
        updateTask(task);
    }

    @Override
    public void turnOffTask(@NonNull String taskId) {

    }

    @Override
    public void updateTask(@NonNull Task task) {
        Task updateTask = new Task(task);
        TASKS_SERVICE_DATA.put(task.getId(), updateTask);
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        callback.onTasksLoaded(Objects.Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        callback.onTaskLoaded(task);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }
}
