package com.evan.remindme.sorts;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.sorts.domain.model.Sort;

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

        void showSorts(List<Sort>sorts);

        void showMessage(String message);

        void showNoSort();

        void showSuccessfullySavedMessage();

        void showSortDeleteError();

        void showSortSaveError();

        void showSortRename(Sort sort);

        void showSortRenameError();

        void showLoadingSortsError();


    }
    interface Presenter extends BasePresenter{

        void rename(@NonNull Sort sort);

        void save(@NonNull Sort sort);

        void delete(@NonNull Sort sort);

        void loadSorts(boolean forceUpdate);

        void addNewSort();
    }
}
