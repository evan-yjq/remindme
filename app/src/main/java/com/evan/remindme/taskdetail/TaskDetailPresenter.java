package com.evan.remindme.taskdetail;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.addedittask.domain.usecase.DeleteTask;
import com.evan.remindme.addedittask.domain.usecase.GetTask;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.allclassify.domain.usecase.GetAllClassify;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.tasks.domain.usecase.GetClassify;
import com.evan.remindme.util.DateUtils;
import com.evan.remindme.util.Objects;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.evan.remindme.addedittask.TasksCircleType.TASKS_CIRCLE_TYPE_LIST;
import static com.evan.remindme.addedittask.TasksRepeatType.TASKS_REPEAT_TYPE_LIST;
import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/4
 * Time: 下午4:26
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {
    private final TaskDetailContract.View mTaskDetailView;
    private final UseCaseHandler mUseCaseHandler;
    private final GetTask mGetTask;
    private final DeleteTask mDeleteTask;
    private final GetAllClassify mGetAllClassify;
    private final SaveTask mSaveTask;
    private final GetClassify mGetClassify;
    private final ActionBar mActionBar;

    @Nullable
    private String mTaskId;

    public TaskDetailPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String taskId,
                               @NonNull ActionBar ab, @NonNull TaskDetailContract.View taskDetailView,
                               @NonNull GetTask getTask, @NonNull GetAllClassify getAllClassify,
                               @NonNull SaveTask saveTask, @NonNull DeleteTask deleteTask,
                               @NonNull GetClassify getClassify) {
        mTaskId = taskId;
        mActionBar = checkNotNull(ab,"ab cannot be null!");
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mGetTask = checkNotNull(getTask, "getTask cannot be null!");
        mGetAllClassify = checkNotNull(getAllClassify, "getAllClassify cannot be null!");
        mSaveTask = checkNotNull(saveTask, "saveTask cannot be null!");
        mDeleteTask = checkNotNull(deleteTask, "deleteTask cannot be null!");
        mGetClassify = checkNotNull(getClassify,"getClassify cannot be null!");
        mTaskDetailView.setPresenter(this);
    }
    @Override
    public void start() {
        loadTask();
    }

    private List<Map<String,String>> details = new ArrayList<>();
    public void loadTask() {
        mTaskDetailView.setLoadingIndicator(true);
        getTask(new UseCase.UseCaseCallback<GetTask.ResponseValue>(){
            @Override
            public void onSuccess(GetTask.ResponseValue value) {
                final Task task = value.getTask();
                //The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()) {
                    return;
                }
                mActionBar.setTitle(task.getTitle());
                mUseCaseHandler.execute(mGetClassify, new GetClassify.RequestValues(task.getClassifyId()),
                        new UseCase.UseCaseCallback<GetClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(GetClassify.ResponseValue response) {
                        details.clear();
                        Classify classify = response.getClassify();
                        String[]titles = new String[]{"下次:","时间:","分类:","循环:","重复:","铃声:"};
                        String[]values = new String[]{new DateUtils().Date2String(task.getNextTime()),
                                new DateUtils().Date2String(task.getTime()),classify.getName(),
                                TASKS_CIRCLE_TYPE_LIST[task.getCircle()+1],TASKS_REPEAT_TYPE_LIST[task.getRepeat()+1],
                                task.getBell()};
                        for (int i = 0; i < titles.length; i++) {
                            Map<String,String>detail = new HashMap<>();
                            detail.put("title",titles[i]);
                            detail.put("value",values[i]);
                            details.add(detail);
                        }
                        mTaskDetailView.showDetails(details);
                        mTaskDetailView.setLoadingIndicator(false);
                    }
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        if (!mTaskDetailView.isActive()) {
                            return;
                        }
                        Map<String,String>detail = new HashMap<>();
                        detail.put("title","错误-102:");
                        detail.put("value","加载分类出错");
                        details.add(detail);
                        mTaskDetailView.showMissingTask(details);
                        mTaskDetailView.setLoadingIndicator(false);
                    }
                });
            }

            @Override
            public void onError() {
//                The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()) {
                    return;
                }
                Map<String,String>detail = new HashMap<>();
                detail.put("title","错误-101:");
                detail.put("value","加载提醒出错");
                details.add(detail);
                mTaskDetailView.showMissingTask(details);
                mTaskDetailView.setLoadingIndicator(false);
            }
        });
    }

    private void getTask(UseCase.UseCaseCallback<GetTask.ResponseValue> callback){
        if (checkTaskId()) {
            callback.onError();
            return;
        }
        assert mTaskId != null;
        mUseCaseHandler.execute(mGetTask, new GetTask.RequestValues(mTaskId), callback);
    }

    @Override
    public void deleteTask() {
        if (checkTaskId())return;
        mUseCaseHandler.execute(mDeleteTask, new DeleteTask.RequestValues(mTaskId),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {
                        mTaskDetailView.showTaskDeleted();
                    }

                    @Override
                    public void onError() {
                        mTaskDetailView.showDeleteError();
                    }
                });
    }

    @Override
    public void editTask() {
        if (checkTaskId()) return;
        assert mTaskId != null;
        mTaskDetailView.showEditTask(mTaskId);
    }

    private boolean checkTaskId() {
        return Objects.Strings.isNullOrEmpty(mTaskId);
    }

    @Override
    public void copyTask(@NonNull final Classify classify) {
        getTask(new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
            @Override
            public void onSuccess(GetTask.ResponseValue response) {
                final Task task = response.getTask();
                Task copyTask = new Task(task.getTitle(),task.getCircle(),task.getRepeat(),task.getTime(),
                                            task.getNextTime(), classify.getId(),task.getBell());
                mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(copyTask, true),
                        new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                            @Override
                            public void onSuccess(SaveTask.ResponseValue response) {
                                mTaskDetailView.showCopyOk();
                            }

                            @Override
                            public void onError() {
                                mTaskDetailView.showCopyError();
                            }
                        });
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void moveTask(@NonNull final Classify classify) {
        getTask(new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
            @Override
            public void onSuccess(GetTask.ResponseValue response) {
                final Task task = response.getTask();
                task.setClassifyId(classify.getId());
                mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(task, false),
                        new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                            @Override
                            public void onSuccess(SaveTask.ResponseValue response) {
                                loadTask();
                                mTaskDetailView.showMoveOk();
                            }

                            @Override
                            public void onError() {
                                mTaskDetailView.showMoveError();
                            }
                        });
            }

            @Override
            public void onError() {}
        });

    }

    @Override
    public void getClassify(final boolean isCopy){
        mUseCaseHandler.execute(mGetAllClassify, new GetAllClassify.RequestValues(false),
                new UseCase.UseCaseCallback<GetAllClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(GetAllClassify.ResponseValue response) {
                        mTaskDetailView.showSelectClassifyDialog(response.getAllClassify(),isCopy);
                    }

                    @Override
                    public void onError() {
                        mTaskDetailView.showLoadAllClassifyError();
                    }
                });
    }
}
