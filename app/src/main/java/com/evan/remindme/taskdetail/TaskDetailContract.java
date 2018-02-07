package com.evan.remindme.taskdetail;

import android.support.annotation.NonNull;
import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;
import com.evan.remindme.allclassify.domain.model.Classify;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/4
 * Time: 下午4:27
 */
public interface TaskDetailContract {

    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        void showDetails(List<Map<String,String>>details);

        void showTaskDeleted();

        void showEditTask(@NonNull String taskId);

        void showMissingTask(List<Map<String,String>>errors);

        void showSelectClassifyDialog(List<Classify>classifies,boolean isCopy);

        void showLoadAllClassifyError();

        void showDeleteError();
        void showCopyOk();
        void showCopyError();
        void showMoveOk();
        void showMoveError();
    }

    interface Presenter extends BasePresenter{
        void deleteTask();
        void editTask();
        void copyTask(@NonNull Classify classify);
        void moveTask(@NonNull Classify classify);
        void getClassify(boolean isCopy);
        void loadTask();
    }

}
