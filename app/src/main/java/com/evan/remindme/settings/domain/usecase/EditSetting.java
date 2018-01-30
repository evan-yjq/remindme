package com.evan.remindme.settings.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SettingsRepository;
import com.evan.remindme.settings.domain.model.Setting;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/30
 * Time: 下午6:16
 */
public class EditSetting extends UseCase<EditSetting.RequestValues,EditSetting.ResponseValue>{

    private final SettingsRepository settingsRepository;

    public EditSetting(@NonNull SettingsRepository settingsRepository) {
        this.settingsRepository = checkNotNull(settingsRepository,"settingsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final Setting setting = requestValues.getSetting();
        settingsRepository.editSetting(setting);
        getUseCaseCallback().onSuccess(new ResponseValue(setting));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Setting setting;


        public RequestValues(@NonNull Setting setting) {
            this.setting = checkNotNull(setting,"setting cannot be null!");
        }

        public Setting getSetting() {
            return setting;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{
        private final Setting setting;


        public ResponseValue(@NonNull Setting setting) {
            this.setting = checkNotNull(setting,"setting cannot be null!");
        }

        public Setting getSetting() {
            return setting;
        }
    }

}
