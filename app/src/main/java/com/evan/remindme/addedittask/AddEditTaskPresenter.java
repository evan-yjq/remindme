package com.evan.remindme.addedittask;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.addedittask.domain.usecase.GetTask;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.sorts.domain.usecase.GetSorts;
import com.evan.remindme.sorts.domain.usecase.SaveSort;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.tasks.domain.usecase.GetSort;

import java.util.Date;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午5:10
 */
public class AddEditTaskPresenter implements AddEditTasksContract.Presenter{

    private final AddEditTasksContract.View mView;
    private final GetTask mGetTask;
    private final GetSorts mGetSorts;
    private final GetSort mGetSort;
    private final SaveTask mSaveTask;
    private final SaveSort mSaveSort;

    private boolean mIsDataMissing;
    @Nullable
    private String mTaskId;

    private final UseCaseHandler mUseCaseHandler;

    private int mCircleType = TasksCircleType.CIRCLE_;

    private int mRepeatType = TasksRepeatType.REPEAT_;

    private Long mSortId = 1L;

    public AddEditTaskPresenter(@NonNull AddEditTasksContract.View mView, @NonNull GetTask mGetTask,
                                @NonNull GetSorts mGetSorts, @NonNull UseCaseHandler mUseCaseHandler,
                                @NonNull GetSort mGetSort,@NonNull SaveTask mSaveTask,
                                @NonNull SaveSort mSaveSort,
                                @Nullable String mTaskId,boolean shouldLoadDataFromRepo) {
        this.mView = checkNotNull(mView,"mView cannot be null!");
        this.mGetTask = checkNotNull(mGetTask,"mGetTask cannot be null!");
        this.mGetSorts = checkNotNull(mGetSorts,"mGetSorts cannot be null!");
        this.mUseCaseHandler = checkNotNull(mUseCaseHandler,"mUseCaseHandler cannot be null!");
        this.mGetSort = checkNotNull(mGetSort,"mGetSort cannot be null!");
        this.mSaveTask = checkNotNull(mSaveTask,"mSaveTask cannot be null!");
        this.mSaveSort = checkNotNull(mSaveSort,"mSaveSort cannot be null!");
        this.mTaskId = mTaskId;
        this.mIsDataMissing = shouldLoadDataFromRepo;

        mView.setPresenter(this);
    }


    @Override
    public void start() {
        setSorts(new BaseCallback() {
            @Override
            public void callback() {
                if (!isNewTask() && mIsDataMissing) {
                    populateTask();
                }
            }
        });
    }

    private void setSorts(final BaseCallback callback){
        mUseCaseHandler.execute(mGetSorts, new GetSorts.RequestValues(false),
                new UseCase.UseCaseCallback<GetSorts.ResponseValue>() {
                    @Override
                    public void onSuccess(GetSorts.ResponseValue response) {
                        if (mView.isActive()){
                            mView.setSortSpinner(response.getSorts());
                            mView.setTitle("提醒名");
                        }
                        callback.callback();
                    }

                    @Override
                    public void onError() {
                        if (mView.isActive()) {
                            mView.showMessage("加载分类组错误");
                        }
                    }
                });
    }

    interface BaseCallback{
        void callback();
    }

    @Override
    public void saveSort(@NonNull String name) {
        checkNotNull(name,"name cannot be null!");
        Sort sort = new Sort(name);
        mUseCaseHandler.execute(mSaveSort, new SaveSort.RequestValues(sort,true),
                new UseCase.UseCaseCallback<SaveSort.ResponseValue>() {
                    @Override
                    public void onSuccess(final SaveSort.ResponseValue response) {
                        mView.showMessage("新建分类成功");
                        setSorts(new BaseCallback() {
                            @Override
                            public void callback() {
                                mView.setSelectSort(response.getSort());
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        mView.showMessage("创建分类失败");
                        mView.setSelectSort(0);
                    }
                });
    }

    private void save(@NonNull Task task) {
        checkNotNull(task,"saveTask cannot be null!");
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(task),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        mView.showTasksList();
                    }

                    @Override
                    public void onError() {
                        mView.showMessage("保存失败");
                    }
                });
    }

    @Override
    public void save(String title, Date time) {
        if (title.trim().isEmpty()){
            mView.showMessage("标题不能为空");
            return;
        }
        if (time == null){
            mView.showMessage("未选择时间");
            return;
        }
        Task task = new Task(title,mCircleType,mRepeatType,time,time,mSortId,"",true);
        save(task);
    }

    @Override
    public void getDateDialog() {
        if (mView.isActive()) {
            if (isNewTask()&&date==null) {
                mView.showTimePickerDialog(new Date());
            }else{
                mView.showTimePickerDialog(date);
            }
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mUseCaseHandler.execute(mGetTask, new GetTask.RequestValues(mTaskId),
                new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTask.ResponseValue response) {
                        showTask(response.getTask());
                    }

                    @Override
                    public void onError() {
                        showEmptyTaskError();
                    }
                });
    }

    private Date date = null;
    private void showTask(Task task) {
        date = task.getTime();
        mSortId = task.getSortId();
        mCircleType = task.getCircle();
        mRepeatType = task.getRepeat();
        // The view may not be able to handle UI updates anymore
        if (mView.isActive()) {
            mView.setTitle(task.getTitle());
            mView.setDate(task.getTime());
            mView.setSelectCircle(task.getCircle());
            mView.setSelectRepeat(task.getRepeat());
        }
        mUseCaseHandler.execute(mGetSort, new GetSort.RequestValues(task.getSortId()),
                new UseCase.UseCaseCallback<GetSort.ResponseValue>() {
                    @Override
                    public void onSuccess(GetSort.ResponseValue response) {
                        if (mView.isActive()){
                            mView.setSelectSort(response.getSort());
                        }
                    }

                    @Override
                    public void onError() {
                        if (mView.isActive()){
                            mView.showMessage("加载分类出错");
                        }
                    }
                });
        mIsDataMissing = false;
    }



    private void showEmptyTaskError() {
        // The view may not be able to handle UI updates anymore
        if (mView.isActive()) {
            mView.showMessage("加载提醒时出错");
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }


    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public int getCircleType() {
        return mCircleType;
    }

    @Override
    public int getRepeatType() {
        return mRepeatType;
    }

    @Override
    public void setCircleType(int circle) {
        mCircleType = circle;
    }

    @Override
    public void setRepeatType(int repeat) {
        mRepeatType = repeat;
    }

    @Override
    public Long getSortId(){
        return mSortId;
    }

    @Override
    public void setSortId(Long id){
        mSortId = id;
    }

//    @Override
//    public String getTitle(String taskId) {
//        return null;
//    }
}
