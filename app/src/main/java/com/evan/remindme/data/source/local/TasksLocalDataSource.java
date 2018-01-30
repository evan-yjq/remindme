package com.evan.remindme.data.source.local;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.dao.TaskDao;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.AppExecutors;

import java.util.List;


import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午10:31
 */
public class TasksLocalDataSource implements TasksDataSource {

    private static volatile TasksLocalDataSource INSTANCE;

    private TaskDao mTaskDao;

    private AppExecutors mAppExecutors;

    private TasksLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TaskDao taskDao){
        mAppExecutors = appExecutors;
        mTaskDao = taskDao;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TaskDao taskDao){
        if (INSTANCE == null){
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksLocalDataSource(appExecutors, taskDao);
                }
            }
        }
        return INSTANCE;
    }

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
    public void updateTask(@NonNull final Task task) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.update(task);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

//    @Override
//    public void getSorts(@NonNull final LoadSortsCallback callback) {
//        final String SQL = "SELECT DISTINCT "+TaskDao.Properties.Sort.columnName+" FROM "+TaskDao.TABLENAME;
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                final ArrayList<String> sorts = new ArrayList<String>();
//                Cursor c = mTaskDao.getDatabase().rawQuery(SQL, null);
//                try {
//                    if (c != null) {
//                        if (c.moveToFirst()) {
//                            do {
//                                sorts.add(c.getString(0));
//                            } while (c.moveToNext());
//                        }
//                    }
//                }finally {
//                    if (c != null) {
//                        c.close();
//                    }
//                }
//                mAppExecutors.mainThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (sorts.isEmpty()){
//                            callback.onDataNotAvailable();
//                        }else{
//                            callback.onSortsLoaded(sorts);
//                        }
//                    }
//                });
//            }
//        };
//        mAppExecutors.diskIO().execute(runnable);
//    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Task>tasks = mTaskDao.loadAll();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()){
                            callback.onDataNotAvailable();
                        }else{
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTaskDao.load(taskId);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null){
                            callback.onTaskLoaded(task);
                        }else{
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTask(@NonNull final Task task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.insert(task);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteTask(@NonNull final String taskId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteByKey(taskId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllTasks() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteAll();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
