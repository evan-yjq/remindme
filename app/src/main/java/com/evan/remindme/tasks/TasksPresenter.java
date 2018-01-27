package com.evan.remindme.tasks;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.tasks.domain.usecase.GetTasks;
import com.evan.remindme.tasks.domain.usecase.SaveTask;
import com.evan.remindme.tasks.domain.usecase.TurnOffTask;
import com.evan.remindme.tasks.domain.usecase.TurnOnTask;
import com.evan.remindme.util.Objects;

import java.util.ArrayList;
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

    //默认显示方式
    private TasksDisplayType mCurrentDisplaying = TasksDisplayType.TASKS_BY_SORT;

    // 设定第一次是否从网络数据中启动
    private boolean mFirstLoad = false;

    private final UseCaseHandler mUseCaseHandler;


    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull TasksContract.View tasksView, @NonNull GetTasks getTasks,
                          @NonNull TurnOnTask turnOnTask, @NonNull TurnOffTask turnOffTask,
                          @NonNull SaveTask saveTask) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTasks = checkNotNull(getTasks, "getTask cannot be null!");
        mTurnOnTask = checkNotNull(turnOnTask, "turnOnTask cannot be null!");
        mTurnOffTask = checkNotNull(turnOffTask, "turnOffTask cannot be null!");
        mSaveTask = checkNotNull(saveTask,"saveTask cannot be null!");

        mTasksView.setPresenter(this);
    }


    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // TODO 等有了添加页面来解除注释
        //如果任务已成功添加，显示消息提示
//        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode
//                && Activity.RESULT_OK == resultCode) {
//            mTasksView.showSuccessfullySavedMessage();
//        }
    }

    @Override
    public void save(@NonNull Task task) {
        checkNotNull(task,"saveTask cannot be null!");
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(task),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        mTasksView.showLoadingTasksError();
                    }
                });
    }

    @Override
    public void setDisplay(TasksDisplayType requestType) {
        mCurrentDisplaying = requestType;
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        //简化示例：网络重新加载将强制在第一次加载。
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   传入true以刷新{@link TasksDataSource}里的数据
     * @param showLoadingUI 传入true以在UI中显示加载图标
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI){
            mTasksView.setLoadingIndicator(true);
        }

        GetTasks.RequestValues requestValues = new GetTasks.RequestValues(forceUpdate,
                mCurrentDisplaying);

        mUseCaseHandler.execute(mGetTasks, requestValues,
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
            @Override
            public void onSuccess(GetTasks.ResponseValue response) {
                Map<String,List<Task>> tasks = response.getTasks();
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

    private void processTasks(Map<String,List<Task>> tasks) {
        if (tasks.isEmpty()){
            //显示一条消息，指出该过滤器类型没有Task
            processEmptyTasks();
        }else{
            //显示Task列表
            switch (mCurrentDisplaying){
                case TASKS_BY_SORT:
                    mTasksView.showSortTasks(tasks);
                    break;
                case TASKS_BY_TIME:
                    mTasksView.showTimeTasks(tasks.get(""));
                    break;
            }
            //设置显示label标题
            showDisplayLabel();
        }
    }

    private void showDisplayLabel() {
        switch (mCurrentDisplaying) {
            case TASKS_BY_SORT:
                mTasksView.showDisplayBySortLabel();
                break;
            case TASKS_BY_TIME:
                mTasksView.showDisplayByTimeLabel();
                break;
            default:
                mTasksView.showDisplayBySortLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentDisplaying) {
            case TASKS_BY_SORT:
                mTasksView.showNoSortTasks();
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

    public List<Task> openCloseSort(List<Task>tasks,long sortId){
        for (int i = 0; i < tasks.size(); i++) {
            if (!Objects.equal(tasks.get(i).getId(),(long)-1)&&Objects.equal(tasks.get(i).getSortId(),sortId)){
                tasks.remove(i);
                i--;
            }
        }
        return tasks;
    }

    @Override
    public TasksDisplayType getDisplaying() {
        return mCurrentDisplaying;
    }
}
