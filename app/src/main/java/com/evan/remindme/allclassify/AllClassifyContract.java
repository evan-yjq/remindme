package com.evan.remindme.allclassify;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.allclassify.domain.model.Classify;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:22
 */
public interface AllClassifyContract {
    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        void showAddClassify();

        boolean isActive();

        void showAllClassify(List<Classify> classifies);

        void showMessage(String message);

        void showNoClassify();

        void showSuccessfullySavedMessage();

        void showClassifyDeleteError();

        void showClassifySaveError();

        void showClassifyRename(Classify classify);

        void showClassifyRenameError();

        void showLoadingAllClassifyError();

    }
    interface Presenter extends BasePresenter{

        void rename(@NonNull Classify classify, @NonNull String name);

        void save(@NonNull String name);

        void delete(@NonNull Classify classify);

        void loadAllClassify(boolean forceUpdate);

        void addNewClassify();
    }
}
