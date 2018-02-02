package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.allclassify.domain.usecase.GetAllClassify;
import com.evan.remindme.allclassify.domain.usecase.SaveClassify;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.TasksDisplayType;
import com.evan.remindme.tasks.domain.display.DisplayFactory;
import com.evan.remindme.tasks.domain.display.TaskDisplay;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.List;
import java.util.Map;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午11:48
 */
public class GetTasks extends UseCase<GetTasks.RequestValues,GetTasks.ResponseValue>{

    private final TasksRepository mTasksRepository;

    private final DisplayFactory mDisplayFactory;

    private final UseCaseHandler mUseCaseHandler;

    private final GetAllClassify mGetAllClassify;
    private final SaveClassify mSaveClassify;

    public GetTasks(@NonNull TasksRepository tasksRepository, @NonNull DisplayFactory displayFactory,
                    @NonNull UseCaseHandler useCaseHandler, @NonNull GetAllClassify getAllClassify,
                    @NonNull SaveClassify saveClassify) {
        mGetAllClassify = checkNotNull(getAllClassify,"getAllClassify cannot be null!");
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mDisplayFactory = checkNotNull(displayFactory, "filterFactory cannot be null!");
        mSaveClassify = checkNotNull(saveClassify, "saveClassify cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()) {
            mTasksRepository.refreshTasks();
        }
        TasksDisplayType currentDisplaying = values.getCurrentDisplaying();
        final TaskDisplay taskDisplay = mDisplayFactory.create(currentDisplaying);

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(final List<Task> tasks) {
                mUseCaseHandler.execute(mGetAllClassify, new GetAllClassify.RequestValues(values.isForceUpdate()),
                        new UseCaseCallback<GetAllClassify.ResponseValue>() {
                            @Override
                            public void onSuccess(GetAllClassify.ResponseValue response) {
                                Map<Classify,List<Task>> tasksDisplay = taskDisplay.display(tasks,response.getAllClassify(),
                                        new ClassifyDataSource.GetClassifyCallback(){
                                            @Override
                                            public void onClassifyLoaded(Classify classify) {
                                                mUseCaseHandler.execute(mSaveClassify, new SaveClassify.RequestValues(classify, true),
                                                        new UseCaseCallback<SaveClassify.ResponseValue>() {
                                                            @Override
                                                            public void onSuccess(SaveClassify.ResponseValue response) {}

                                                            @Override
                                                            public void onError() {}
                                                        });
                                            }
                                            @Override
                                            public void onDataNotAvailable() {}
                                        });
                                ResponseValue responseValue = new ResponseValue(tasksDisplay);
                                getUseCaseCallback().onSuccess(responseValue);
                            }

                            @Override
                            public void onError() {
                                Map<Classify,List<Task>> tasksDisplay = taskDisplay.display(tasks,null,null);
                                ResponseValue responseValue = new ResponseValue(tasksDisplay);
                                getUseCaseCallback().onSuccess(responseValue);
                            }
                        });
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final TasksDisplayType mCurrentDisplaying;

        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate, @NonNull TasksDisplayType currentDisplaying) {
            mForceUpdate = forceUpdate;
            mCurrentDisplaying = checkNotNull(currentDisplaying, "currentDisplaying cannot be null!");
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }

        public TasksDisplayType getCurrentDisplaying() {
            return mCurrentDisplaying;
        }

    }
    public static final class ResponseValue implements UseCase.ResponseValue {
        private final Map<Classify,List<Task>> mTasks;

        public ResponseValue(@NonNull Map<Classify,List<Task>> tasks) {
            mTasks = checkNotNull(tasks, "tasks cannot be null!");
        }

        public Map<Classify,List<Task>> getTasks() {
            return mTasks;
        }
    }
}
