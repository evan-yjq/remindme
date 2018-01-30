package com.evan.remindme.settings;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.settings.domain.model.Setting;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:14
 */
public interface SettingsContract {

    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        boolean isActive();

        void showSettings(List<Setting>settings);

        void showMessage(String message);

    }

    interface Presenter extends BasePresenter{

        void edit(@NonNull Setting setting);

        void loadSettings(boolean forceUpdate);

    }
}
