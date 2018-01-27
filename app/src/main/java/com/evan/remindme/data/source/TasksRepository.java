package com.evan.remindme.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.*;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午6:50
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * 这个变量具有包本地可见性，所以可以从测试中访问它.
     */
    Map<String, Task> mCachedTasks;

    /**
     * 将缓存标记为无效，在下一次请求数据时强制更新
     */
    boolean mCacheIsDirty = false;

    //防止直接实例化。
    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * 返回这个类的单个实例，如果需要的话创建它。
     *
     * @param tasksRemoteDataSource 网络数据源
     * @param tasksLocalDataSource  设备存储数据源
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * 用于在下次调用时强制创建一个新实例。
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void turnOnTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.turnOnTask(task);
        mTasksLocalDataSource.turnOnTask(task);

        Task turnOnTask = new Task(task);
        turnOnTask.setTurnOn(true);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), turnOnTask);

    }

    @Override
    public void turnOnTask(@NonNull String taskId) {
        checkNotNull(taskId);
        turnOnTask(Objects.requireNonNull(getTaskWithId(taskId)));
    }

    @Override
    public void turnOffTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.turnOffTask(task);
        mTasksLocalDataSource.turnOffTask(task);

        Task turnOffTask = new Task(task);
        turnOffTask.setTurnOn(false);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), turnOffTask);
    }

    @Override
    public void turnOffTask(@NonNull String taskId) {
        checkNotNull(taskId);
        turnOffTask(Objects.requireNonNull(getTaskWithId(taskId)));
    }

    @Override
    public void updateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.updateTask(task);
        mTasksLocalDataSource.updateTask(task);

        Task updateTask = new Task(task);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), updateTask);
    }

    /**
     * 从缓存，本地数据源（SQLite）或远程数据源获取任务，先取其一。
     * 注意：如果所有数据源都无法获取数据，则触发LoadTasksCallback.onDataNotAvailable（）。
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        //如果可用且不肮脏，则立即响应缓存
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if (mCacheIsDirty) {
            // 如果缓存脏了，我们需要从网络中获取新的数据。
            getTasksFromRemoteDataSource(callback);
        }else {
            // 查询本地存储（如果可用）。如果不行，则查询网络。
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        Task cachedTask = getTaskWithId(taskId);

        //如果可用，立即响应缓存
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        //从服务器加载/保持如果需要。

        //本地存储其中是否有Task，如果没有，从网上加载
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                // 在内存缓存更新，以保持应用程序界面最新
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        // 在内存缓存更新，以保持应用程序界面最新
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(), task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));

        mCachedTasks.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task : tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }
}
