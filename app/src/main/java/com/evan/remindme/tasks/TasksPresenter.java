package com.evan.remindme.tasks;

import android.graphics.Path;
import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.sorts.domain.usecase.CloseSort;
import com.evan.remindme.sorts.domain.usecase.GetSortByName;
import com.evan.remindme.sorts.domain.usecase.OpenSort;
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
    private final OpenSort mOpenSort;
    private final CloseSort mCloseSort;
    private final GetSortByName mGetSortByName;

    //默认显示方式
    private TasksDisplayType mCurrentDisplaying = TasksDisplayType.TASKS_BY_SORT;

    // 设定第一次是否从网络数据中启动
    private boolean mFirstLoad = false;

    private final UseCaseHandler mUseCaseHandler;


    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull TasksContract.View tasksView,
                          @NonNull GetTasks getTasks, @NonNull TurnOnTask turnOnTask,
                          @NonNull TurnOffTask turnOffTask, @NonNull SaveTask saveTask,
                          @NonNull OpenSort openSort, @NonNull CloseSort closeSort,
                          @NonNull GetSortByName getSortByName) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTasks = checkNotNull(getTasks, "getTask cannot be null!");
        mTurnOnTask = checkNotNull(turnOnTask, "turnOnTask cannot be null!");
        mTurnOffTask = checkNotNull(turnOffTask, "turnOffTask cannot be null!");
        mSaveTask = checkNotNull(saveTask,"saveTask cannot be null!");
        mOpenSort = checkNotNull(openSort,"openSort cannot be null!");
        mCloseSort = checkNotNull(closeSort,"closeSort cannot be null!");
        mGetSortByName = checkNotNull(getSortByName,"getSortByName cannot be null!");

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
                        mTasksView.showSuccessfullySavedMessage();
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

        GetTasks.RequestValues requestValues = new GetTasks.RequestValues(forceUpdate, mCurrentDisplaying);

        mUseCaseHandler.execute(mGetTasks, requestValues,
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
            @Override
            public void onSuccess(GetTasks.ResponseValue response) {
                Map<Sort,List<Task>> tasks = response.getTasks();
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

    private void processTasks(Map<Sort,List<Task>> tasks) {
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
                    mTasksView.showTimeTasks(tasks.get(new Sort("")));
                    break;
            }
            //设置显示label标题
//            showDisplayLabel();
        }
    }

//    private void showDisplayLabel() {
//        switch (mCurrentDisplaying) {
//            case TASKS_BY_SORT:
//                mTasksView.showDisplayBySortLabel();
//                break;
//            case TASKS_BY_TIME:
//                mTasksView.showDisplayByTimeLabel();
//                break;
//            default:
//                mTasksView.showDisplayBySortLabel();
//                break;
//        }
//    }

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
    public void addNewTask(final String name) {
        checkNotNull(name,"name cannot be null!");
        mUseCaseHandler.execute(mGetSortByName, new GetSortByName.RequestValues(name),
                new UseCase.UseCaseCallback<GetSortByName.ResponseValue>() {
                    @Override
                    public void onSuccess(GetSortByName.ResponseValue response) {
                        mTasksView.showAddTask(name,response.getSort().getId());
                    }

                    @Override
                    public void onError() {
                        mTasksView.showAddTask(name,(long)1);
                    }
                });
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
    public void openSort(@NonNull Sort openSort) {
        checkNotNull(openSort,"openSort cannot be null!");
        mUseCaseHandler.execute(mOpenSort, new OpenSort.RequestValues(openSort.getId()),
                new UseCase.UseCaseCallback<OpenSort.ResponseValue>() {
                    @Override
                    public void onSuccess(OpenSort.ResponseValue response) {
//                        mTasksView.showMessage("打开Sort写入");
//                        loadTasks(false,false);
                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });
    }

    @Override
    public void closeSort(@NonNull Sort closeSort) {
        checkNotNull(closeSort,"closeSort cannot be null!");
        mUseCaseHandler.execute(mCloseSort, new CloseSort.RequestValues(closeSort.getId()),
                new UseCase.UseCaseCallback<CloseSort.ResponseValue>() {
                    @Override
                    public void onSuccess(CloseSort.ResponseValue response) {
//                        mTasksView.showMessage("关闭Sort写入");
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
