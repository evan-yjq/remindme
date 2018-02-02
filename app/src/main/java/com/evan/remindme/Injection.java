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
import com.evan.remindme.addedittask.domain.usecase.DeleteTask;
import com.evan.remindme.addedittask.domain.usecase.GetTask;
import com.evan.remindme.allclassify.domain.usecase.GetAllClassify;
import com.evan.remindme.data.source.SettingsRepository;
import com.evan.remindme.data.source.ClassifyRepository;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.data.source.dao.DaoSession;
import com.evan.remindme.data.source.local.SettingsLocalDataSource;
import com.evan.remindme.data.source.local.ClassifyLocalDataSource;
import com.evan.remindme.data.source.local.TasksLocalDataSource;
import com.evan.remindme.data.source.remote.SettingRemoteDataSource;
import com.evan.remindme.data.source.remote.ClassifyRemoteDataSource;
import com.evan.remindme.data.source.remote.TasksRemoteDataSource;
import com.evan.remindme.settings.domain.usecase.EditSetting;
import com.evan.remindme.settings.domain.usecase.GetSetting;
import com.evan.remindme.settings.domain.usecase.GetSettings;
import com.evan.remindme.allclassify.domain.usecase.DeleteClassify;
import com.evan.remindme.allclassify.domain.usecase.SaveClassify;
import com.evan.remindme.tasks.domain.usecase.*;
import com.evan.remindme.tasks.domain.usecase.GetClassify;
import com.evan.remindme.tasks.domain.display.DisplayFactory;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;
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

    public static ClassifyRepository provideClassifyRepository(@NonNull Context context){
        checkNotNull(context);
        DaoSession database = GreenDaoUtils.getSingleTon().getmDaoSession(context);
        return ClassifyRepository.getInstance(ClassifyRemoteDataSource.getInstance(),
                ClassifyLocalDataSource.getInstance(new AppExecutors(),
                        database.getClassifyDao()));

    }

    public static SettingsRepository provideSettingsRepository(@NonNull Context context){
        checkNotNull(context);
        DaoSession database = GreenDaoUtils.getSingleTon().getmDaoSession(context);
        return SettingsRepository.getInstance(SettingsLocalDataSource.getInstance(new AppExecutors(),database.getSettingDao()),
                SettingRemoteDataSource.getInstance());
    }

    public static EditSetting provideEditSetting(@NonNull Context context){
        return new EditSetting(provideSettingsRepository(context));
    }

    public static GetSettings provideGetSettings(@NonNull Context context){
        return new GetSettings(provideSettingsRepository(context));
    }

    public static GetSetting provideGetSetting(@NonNull Context context){
        return new GetSetting(provideSettingsRepository(context));
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideTasksRepository(context), new DisplayFactory(),
                provideUseCaseHandler(),provideGetAllClassify(context),
                provideSaveClassify(context));
    }

    public static GetClassify provideGetClassify(@NonNull Context context){
        return new GetClassify(provideClassifyRepository(context));
    }

    public static SaveClassify provideSaveClassify(@NonNull Context context){
        return new SaveClassify(provideClassifyRepository(context));
    }

    public static DeleteClassify provideDeleteClassify(@NonNull Context context){
        return new DeleteClassify(provideClassifyRepository(context));
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetAllClassify provideGetAllClassify(@NonNull Context context){
        return new GetAllClassify(provideClassifyRepository(context));
    }

    public static TurnOnTask provideTurnOnTask(@NonNull Context context) {
        return new TurnOnTask(provideTasksRepository(context));
    }

    public static TurnOffTask provideTurnOffTasks(@NonNull Context context) {
        return new TurnOffTask(provideTasksRepository(context));
    }

    public static SaveTask provideSaveTasks(@NonNull Context context) {
        return new SaveTask(provideTasksRepository(context));
    }

    public static OpenClassify provideOpenClassify(@NonNull Context context){
        return new OpenClassify(provideClassifyRepository(context));
    }

    public static CloseClassify provideCloseClassify(@NonNull Context context){
        return new CloseClassify(provideClassifyRepository(context));
    }

    public static DeleteTask provideDeleteTask(@NonNull Context context) {
        return new DeleteTask(provideTasksRepository(context));
    }

    public static GetTask provideGetTask(@NonNull Context context){
        return new GetTask(provideTasksRepository(context));
    }

//    public static GetStatistics provideGetStatistics(@NonNull Context context) {
//        return new GetStatistics(Injection.provideTasksRepository(context));
//    }
}
