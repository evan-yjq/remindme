package com.evan.remindme.data.source.remote;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.SettingsDataSource;
import com.evan.remindme.settings.SettingDisplay;
import com.evan.remindme.settings.SettingKey;
import com.evan.remindme.settings.domain.model.Setting;
import com.evan.remindme.tasks.TasksDisplayType;
import com.evan.remindme.util.Objects;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/30
 * Time: 下午6:45
 */
public class SettingRemoteDataSource implements SettingsDataSource{

    private static SettingRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<String,Setting>SETTINGS_SERVICE_DATA;

    static {
        SETTINGS_SERVICE_DATA = new LinkedHashMap<>(1);
        addSetting(SettingKey.DEFAULT_TASKS_DISPLAY_TYPE.toString(),"默认分类显示"
                , "true", SettingDisplay.CAPTION_SWITCH_ITEM.toString());
    }

    private static void addSetting(String id, String key, String value,String display){
        Setting setting = new Setting(id, key, value,display);
        SETTINGS_SERVICE_DATA.put(id,setting);
    }

    public static SettingRemoteDataSource getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SettingRemoteDataSource();
        }
        return INSTANCE;
    }

    private SettingRemoteDataSource(){}

    @Override
    public void getSetting(@NonNull String id, @NonNull GetSettingCallback callback) {
        Setting setting = SETTINGS_SERVICE_DATA.get(id);
        if (setting!=null) {
            callback.onSettingLoaded(setting);
        }else{
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveSetting(@NonNull Setting setting) {
        SETTINGS_SERVICE_DATA.put(setting.getId(),setting);
    }

    @Override
    public void deleteSetting(@NonNull String id) {
        SETTINGS_SERVICE_DATA.remove(id);
    }

    @Override
    public void deleteAllSetting() {
        SETTINGS_SERVICE_DATA.clear();
    }

    @Override
    public void getSettings(@NonNull LoadSettingsCallback callback) {
        callback.onSettingsLoaded(Objects.Lists.newArrayList(SETTINGS_SERVICE_DATA.values()));
    }

    @Override
    public void editSetting(@NonNull Setting setting) {
        Setting s = new Setting(setting);
        SETTINGS_SERVICE_DATA.put(setting.getId(),s);
    }

    @Override
    public void refreshSettings() {
        //
    }
}
