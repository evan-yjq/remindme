package com.evan.remindme.addedittask;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.addedittask.domain.usecase.GetTask;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.allclassify.domain.usecase.GetAllClassify;
import com.evan.remindme.allclassify.domain.usecase.SaveClassify;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.tasks.domain.usecase.GetClassify;
import com.evan.remindme.util.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
    private final GetAllClassify mGetAllClassify;
    private final GetClassify mGetClassify;
    private final SaveTask mSaveTask;
    private final SaveClassify mSaveClassify;

    private boolean mIsDataMissing;

    @Nullable
    private String mTaskId;

    private final UseCaseHandler mUseCaseHandler;

    //一些默认值
    private int mCircleType = TasksCircleType.CIRCLE_;
    private int mRepeatType = TasksRepeatType.REPEAT_;
    private Classify mClassify;
    private Date mDate = new Date();
    private Date mNext = null;
    private String mBell = "震动";

    public AddEditTaskPresenter(@NonNull AddEditTasksContract.View mView, @NonNull GetTask mGetTask,
                                @NonNull GetAllClassify mGetAllClassify, @NonNull UseCaseHandler mUseCaseHandler,
                                @NonNull GetClassify mGetClassify, @NonNull SaveTask mSaveTask,
                                @NonNull SaveClassify mSaveClassify,
                                @Nullable String mTaskId, boolean shouldLoadDataFromRepo) {
        this.mView = checkNotNull(mView,"mView cannot be null!");
        this.mGetTask = checkNotNull(mGetTask,"mGetTask cannot be null!");
        this.mGetAllClassify = checkNotNull(mGetAllClassify,"mGetAllClassify cannot be null!");
        this.mUseCaseHandler = checkNotNull(mUseCaseHandler,"mUseCaseHandler cannot be null!");
        this.mGetClassify = checkNotNull(mGetClassify,"mGetClassify cannot be null!");
        this.mSaveTask = checkNotNull(mSaveTask,"mSaveTask cannot be null!");
        this.mSaveClassify = checkNotNull(mSaveClassify,"mSaveClassify cannot be null!");
        this.mTaskId = mTaskId;
        this.mIsDataMissing = shouldLoadDataFromRepo;

        mView.setPresenter(this);
    }


    @Override
    public void start() {
        if (classifies==null) {
            setAllClassify(new BaseCallback() {
                @Override
                public void callback() {
                    if (!isNewTask() && mIsDataMissing) {
                        populateTask();
                    }else if (mView.isActive()){
                        mView.setSelectClassify(mClassify);
                        mView.setDate(mDate);
                    }
                }
            });
        }
    }

    private List<Classify>classifies;

    private int mClassifySize = 0;
    private void setAllClassify(final BaseCallback callback){
        mUseCaseHandler.execute(mGetAllClassify, new GetAllClassify.RequestValues(false),
                new UseCase.UseCaseCallback<GetAllClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(GetAllClassify.ResponseValue response) {
                        classifies = response.getAllClassify();
                        mClassifySize = classifies.size();
                        if (mView.isActive()){
                            mView.setClassifySpinner(classifies);
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
    public void saveClassify(@NonNull String name) {
        checkNotNull(name,"name cannot be null!");
        Classify classify = new Classify(name,mClassifySize);
        mUseCaseHandler.execute(mSaveClassify, new SaveClassify.RequestValues(classify,true),
                new UseCase.UseCaseCallback<SaveClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(final SaveClassify.ResponseValue response) {
                        mView.showMessage("新建分类成功");
                        setAllClassify(new BaseCallback() {
                            @Override
                            public void callback() {
                                mClassify = response.getClassify();
                                mView.setSelectClassify(mClassify);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        mView.showMessage("创建分类失败");
                        mView.setSelectClassify(mClassify);
                    }
                });
    }

    private void save(@NonNull Task task) {
        checkNotNull(task,"task cannot be null!");
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(task,isNewTask()),
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
    public void save(String title) {
        if (title.trim().isEmpty()){
            mView.showMessage("标题不能为空");
            return;
        }
        if (mNext==null)mNext = mDate;
        Task task = new Task(title, mCircleType, mRepeatType, mDate, mNext, mClassify.getId(), mBell);
        if (!isNewTask()) {
            task.setId(mTaskId);
        }
        save(task);
    }

    @Override
    public void getDateDialog() {
        mView.showTimePickerDialog(mDate);
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


    private void showTask(Task task) {
        mDate = task.getTime();
        mNext = task.getNextTime();
        mCircleType = task.getCircle();
        mRepeatType = task.getRepeat();
        mBell = task.getBell();
        // The view may not be able to handle UI updates anymore
        if (mView.isActive()) {
            mView.setTitle(task.getTitle());
            mView.setDate(mDate);
            mView.setSelectCircle(mCircleType);
            mView.setSelectRepeat(mRepeatType);
            mView.setSelectBell(mBell);
        }
        mUseCaseHandler.execute(mGetClassify, new GetClassify.RequestValues(task.getClassifyId()),
                new UseCase.UseCaseCallback<GetClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(GetClassify.ResponseValue response) {
                        if (mView.isActive()){
                            mClassify = response.getClassify();
                            mView.setSelectClassify(mClassify);
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
    public Classify getClassify(){
        return mClassify;
    }

    @Override
    public Date getDate(){
        return mDate;
    }

    @Override
    public void setDate(Date date){
        mDate = date;
    }

    @Override
    public void setClassify(Classify classify){
        mClassify = classify;
    }

    @Override
    public void setBell(String bell){
        mBell = bell;
    }

//    @Override
//    public String getTitle(String taskId) {
//        return null;
//    }
}
