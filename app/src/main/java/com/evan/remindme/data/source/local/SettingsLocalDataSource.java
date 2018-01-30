package com.evan.remindme.data.source.local;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.SettingsDataSource;
import com.evan.remindme.data.source.dao.SettingDao;
import com.evan.remindme.settings.domain.model.Setting;
import com.evan.remindme.util.AppExecutors;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/30
 * Time: 下午7:00
 */
public class SettingsLocalDataSource implements SettingsDataSource{

    private static volatile SettingsLocalDataSource INSTANCE;

    private SettingDao mSettingDao;

    private AppExecutors mAppExecutors;

    private SettingsLocalDataSource(@NonNull AppExecutors appExecutors,
                                    @NonNull SettingDao settingDao){
        mAppExecutors = appExecutors;
        mSettingDao = settingDao;
    }

    public static SettingsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull SettingDao settingDao){
        if (INSTANCE == null){
            synchronized (SettingsLocalDataSource.class){
                if (INSTANCE == null){
                    INSTANCE = new SettingsLocalDataSource(appExecutors,settingDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getSetting(@NonNull final String id, @NonNull final GetSettingCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Setting setting = mSettingDao.load(id);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (setting != null){
                            callback.onSettingLoaded(setting);
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
    public void saveSetting(@NonNull final Setting setting) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSettingDao.insert(setting);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteSetting(@NonNull final String id) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSettingDao.deleteByKey(id);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllSetting() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSettingDao.deleteAll();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSettings(@NonNull final LoadSettingsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Setting> settings = mSettingDao.loadAll();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!settings.isEmpty()) {
                            callback.onSettingsLoaded(settings);
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
    public void editSetting(@NonNull final Setting setting) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSettingDao.update(setting);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void refreshSettings() {
        //
    }
}
