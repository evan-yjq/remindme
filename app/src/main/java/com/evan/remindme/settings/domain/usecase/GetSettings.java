package com.evan.remindme.settings.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SettingsDataSource;
import com.evan.remindme.data.source.SettingsRepository;
import com.evan.remindme.settings.domain.model.Setting;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/30
 * Time: 下午8:17
 */
public class GetSettings extends UseCase<GetSettings.RequestValues,GetSettings.ResponseValue>{

    private final SettingsRepository mSettingsRepository;

    public GetSettings(@NonNull SettingsRepository settingsRepository){
        mSettingsRepository = checkNotNull(settingsRepository,"settingsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (requestValues.isForceUpdate()){
            mSettingsRepository.refreshSettings();
        }

        mSettingsRepository.getSettings(new SettingsDataSource.LoadSettingsCallback() {
            @Override
            public void onSettingsLoaded(List<Setting> settings) {
                getUseCaseCallback().onSuccess(new ResponseValue(settings));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate){
            mForceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue {
        private final List<Setting> mSettings;

        public ResponseValue(@NonNull List<Setting>settings){
            mSettings = checkNotNull(settings,"settings cannot be null!");
        }

        public List<Setting> getSettings() {
            return mSettings;
        }
    }
}
