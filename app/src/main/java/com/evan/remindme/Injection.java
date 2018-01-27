/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evan.remindme;

import android.content.Context;
import android.support.annotation.NonNull;
import com.evan.remindme.data.source.SortsRepository;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.data.source.local.DaoSession;
import com.evan.remindme.data.source.local.SortsLocalDataSource;
import com.evan.remindme.data.source.local.TasksLocalDataSource;
import com.evan.remindme.data.source.remote.SortsRemoteDataSource;
import com.evan.remindme.data.source.remote.TasksRemoteDataSource;
import com.evan.remindme.tasks.domain.display.DisplayFactory;
import com.evan.remindme.tasks.domain.usecase.GetTasks;
import com.evan.remindme.tasks.domain.usecase.SaveTask;
import com.evan.remindme.tasks.domain.usecase.TurnOffTask;
import com.evan.remindme.tasks.domain.usecase.TurnOnTask;
import com.evan.remindme.util.AppExecutors;
import com.evan.remindme.util.GreenDaoUtils;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TasksDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        DaoSession database = GreenDaoUtils.getSingleTon().getmDaoSession(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(new AppExecutors(),
                        database.getTaskDao()));
    }

    public static SortsRepository provideSortsRepository(@NonNull Context context){
        checkNotNull(context);
        DaoSession database = GreenDaoUtils.getSingleTon().getmDaoSession(context);
        return SortsRepository.getInstance(SortsRemoteDataSource.getInstance(),
                SortsLocalDataSource.getInstance(new AppExecutors(),
                        database.getSortDao()));

    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideTasksRepository(context), provideSortsRepository(context),
                new DisplayFactory());
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

//    public static GetTask provideGetTask(@NonNull Context context) {
//        return new GetTask(Injection.provideTasksRepository(context));
//    }

//    public static SaveTask provideSaveTask(@NonNull Context context) {
//        return new SaveTask(Injection.provideTasksRepository(context));
//    }


    public static TurnOnTask provideTurnOnTask(@NonNull Context context) {
        return new TurnOnTask(Injection.provideTasksRepository(context));
    }

    public static TurnOffTask provideTurnOffTasks(@NonNull Context context) {
        return new TurnOffTask(Injection.provideTasksRepository(context));
    }
    public static SaveTask provideSaveTasks(@NonNull Context context) {
        return new SaveTask(Injection.provideTasksRepository(context));
    }

//    public static DeleteTask provideDeleteTask(@NonNull Context context) {
//        return new DeleteTask(Injection.provideTasksRepository(context));
//    }

//    public static GetStatistics provideGetStatistics(@NonNull Context context) {
//        return new GetStatistics(Injection.provideTasksRepository(context));
//    }
}
