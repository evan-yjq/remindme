package com.evan.remindme.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.addedittask.AddEditTaskActivity;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.settings.SettingKey;
import com.evan.remindme.settings.domain.model.Setting;
import com.evan.remindme.settings.domain.usecase.GetSetting;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.domain.usecase.*;
import com.evan.remindme.tasks.domain.usecase.CloseClassify;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;

import java.util.List;
import java.util.Map;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午6:08
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final TasksContract.View mTasksView;
    private final GetTasks mGetTasks;
    private final TurnOnTask mTurnOnTask;
    private final TurnOffTask mTurnOffTask;
    private final SaveTask mSaveTask;
    private final OpenClassify mOpenClassify;
    private final CloseClassify mCloseClassify;
    private final GetSetting mGetSetting;

    //默认显示方式
    private TasksDisplayType mCurrentDisplaying = TasksDisplayType.TASKS_BY_CLASSIFY;

    // 是否是第一次启动
    private boolean mFirstLoad = true;

    private final UseCaseHandler mUseCaseHandler;


    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull TasksContract.View tasksView,
                          @NonNull GetTasks getTasks, @NonNull TurnOnTask turnOnTask,
                          @NonNull TurnOffTask turnOffTask, @NonNull SaveTask saveTask,
                          @NonNull OpenClassify openClassify, @NonNull CloseClassify closeClassify,
                          @NonNull GetSetting getSetting) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTasks = checkNotNull(getTasks, "getTask cannot be null!");
        mTurnOnTask = checkNotNull(turnOnTask, "turnOnTask cannot be null!");
        mTurnOffTask = checkNotNull(turnOffTask, "turnOffTask cannot be null!");
        mSaveTask = checkNotNull(saveTask,"saveTask cannot be null!");
        mOpenClassify = checkNotNull(openClassify,"openClassify cannot be null!");
        mCloseClassify = checkNotNull(closeClassify,"closeClassify cannot be null!");
        mGetSetting = checkNotNull(getSetting,"getSetting cannot be null!");

        mTasksView.setPresenter(this);
    }


    @Override
    public void start() {
        loadDefaultDisplaySetting();
    }

    @Override
    public void result(int requestCode, int resultCode) {
//        如果任务已成功添加，显示消息提示
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode
                && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void setDisplay(TasksDisplayType requestType) {
        mCurrentDisplaying = requestType;
    }

    private void loadDefaultDisplaySetting(){
        if (mFirstLoad){
            GetSetting.RequestValues requestValues = new GetSetting.RequestValues(SettingKey.DEFAULT_TASKS_DISPLAY_TYPE);
            mUseCaseHandler.execute(mGetSetting, requestValues,
                    new UseCase.UseCaseCallback<GetSetting.ResponseValue>() {
                        @Override
                        public void onSuccess(GetSetting.ResponseValue response) {
                            Setting setting = response.getSetting();
                            if (Boolean.parseBoolean(setting.getValue())) {
                                setDisplay(TasksDisplayType.TASKS_BY_CLASSIFY);
                            }else{
                                setDisplay(TasksDisplayType.TASKS_BY_TIME);
                            }
                            loadTasks(false);
                        }
                        @Override
                        public void onError() {
                            setDisplay(TasksDisplayType.TASKS_BY_CLASSIFY);
                            loadTasks(false);
                        }
                    });
        }else{
            loadTasks(false);
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate, true);
        mFirstLoad = false;
    }

    @Override
    public boolean isFirstLoad(){
        return mFirstLoad;
    }

    @Override
    public void setFirstLoad(boolean firstLoad){
        mFirstLoad = firstLoad;
    }

    /**
     * @param forceUpdate   传入true以刷新{@link TasksDataSource}里的数据
     * @param showLoadingUI 传入true以在UI中显示加载图标
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI){
            mTasksView.setLoadingIndicator(true);
        }

        GetTasks.RequestValues requestValues = new GetTasks.RequestValues(forceUpdate, mCurrentDisplaying);

        mUseCaseHandler.execute(mGetTasks, requestValues,
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
            @Override
            public void onSuccess(GetTasks.ResponseValue response) {
                Map<Classify,List<Task>> tasks = response.getTasks();
                //该视图可能无法处理UI更新
                if (!mTasksView.isActive()){
                    return;
                }
                if (showLoadingUI){
                    mTasksView.setLoadingIndicator(false);
                }

                processTasks(tasks);
            }

            @Override
            public void onError() {
                //该视图可能无法处理UI更新
                if (!mTasksView.isActive()){
                    return;
                }
                if (showLoadingUI){
                    mTasksView.setLoadingIndicator(false);
                }
                mTasksView.showLoadingTasksError();
            }
        });
    }

    private void processTasks(Map<Classify,List<Task>> tasks) {
        if (tasks.isEmpty()){
            //显示一条消息，指出该过滤器类型没有Task
            processEmptyTasks();
        }else{
            //显示Task列表
            switch (mCurrentDisplaying){
                case TASKS_BY_CLASSIFY:
                    mTasksView.showClassifyTasks(tasks);
                    break;
                case TASKS_BY_TIME:
                    mTasksView.showTimeTasks(tasks.get(null));
                    break;
            }
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentDisplaying) {
            case TASKS_BY_CLASSIFY:
                mTasksView.showNoClassifyTasks();
                break;
            case TASKS_BY_TIME:
                mTasksView.showNoTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void turnOnTask(@NonNull Task turnOnTask) {
        checkNotNull(turnOnTask,"turnOnTask cannot be null!");
        mUseCaseHandler.execute(mTurnOnTask, new TurnOnTask.RequestValues(turnOnTask.getId()),
                new UseCase.UseCaseCallback<TurnOnTask.ResponseValue>() {
                    @Override
                    public void onSuccess(TurnOnTask.ResponseValue response) {
                        mTasksView.showTaskMarkedTurnOn();
                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        mTasksView.showLoadingTasksError();
                    }
                });
    }

    @Override
    public void turnOffTask(@NonNull Task turnOffTask) {
        checkNotNull(turnOffTask,"turnOnTask cannot be null!");
        mUseCaseHandler.execute(mTurnOffTask, new TurnOffTask.RequestValues(turnOffTask.getId()),
                new UseCase.UseCaseCallback<TurnOffTask.ResponseValue>() {
                    @Override
                    public void onSuccess(TurnOffTask.ResponseValue response) {
                        mTasksView.showTaskMarkedTurnOff();
                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        mTasksView.showLoadingTasksError();
                    }
                });
    }

    @Override
    public void openClassify(@NonNull Classify openClassify) {
        checkNotNull(openClassify,"openClassify cannot be null!");
        mUseCaseHandler.execute(mOpenClassify, new OpenClassify.RequestValues(openClassify.getId()),
                new UseCase.UseCaseCallback<OpenClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(OpenClassify.ResponseValue response) {
//                        mTasksView.showMessage("打开Classify写入");
//                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });
    }

    @Override
    public void closeClassify(@NonNull Classify closeClassify) {
        checkNotNull(closeClassify,"closeClassify cannot be null!");
        mUseCaseHandler.execute(mCloseClassify, new CloseClassify.RequestValues(closeClassify.getId()),
                new UseCase.UseCaseCallback<CloseClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(CloseClassify.ResponseValue response) {
//                        mTasksView.showMessage("关闭Classify写入");
//                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });
    }

    @Override
    public TasksDisplayType getDisplaying() {
        return mCurrentDisplaying;
    }
}
