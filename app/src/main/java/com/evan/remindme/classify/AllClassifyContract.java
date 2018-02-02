package com.evan.remindme.classify;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.classify.domain.model.Classify;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:22
 */
public interface SortsContract {
    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        void showAddSort();

        boolean isActive();

        void showSorts(List<Classify> classifies);

        void showMessage(String message);

        void showNoSort();

        void showSuccessfullySavedMessage();

        void showSortDeleteError();

        void showSortSaveError();

        void showSortRename(Classify classify);

        void showSortRenameError();

        void showLoadingSortsError();

    }
    interface Presenter extends BasePresenter{

        void rename(@NonNull Classify classify, @NonNull String name);

        void save(@NonNull String name);

        void delete(@NonNull Classify classify);

        void loadSorts(boolean forceUpdate);

        void addNewSort();
    }
}
