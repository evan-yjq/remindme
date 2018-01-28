package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.data.source.SortsRepository;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.sorts.domain.usecase.GetSorts;
import com.evan.remindme.tasks.TasksDisplayType;
import com.evan.remindme.tasks.domain.display.DisplayFactory;
import com.evan.remindme.tasks.domain.display.TaskDisplay;
import com.evan.remindme.sorts.domain.model.Sort;
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

    private final GetSorts mGetSorts;

    public GetTasks(@NonNull TasksRepository tasksRepository, @NonNull DisplayFactory displayFactory,
                    @NonNull UseCaseHandler useCaseHandler,@NonNull GetSorts getSorts) {
        mGetSorts = checkNotNull(getSorts,"getSorts cannot be null!");
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mDisplayFactory = checkNotNull(displayFactory, "filterFactory cannot be null!");
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
                mUseCaseHandler.execute(mGetSorts, new GetSorts.RequestValues(values.isForceUpdate()),
                        new UseCaseCallback<GetSorts.ResponseValue>() {
                            @Override
                            public void onSuccess(GetSorts.ResponseValue response) {
                                Map<Sort,List<Task>> tasksDisplay = taskDisplay.display(tasks,response.getSorts());
                                ResponseValue responseValue = new ResponseValue(tasksDisplay);
                                getUseCaseCallback().onSuccess(responseValue);
                            }

                            @Override
                            public void onError() {
                                Map<Sort,List<Task>> tasksDisplay = taskDisplay.display(tasks,null);
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
        private final Map<Sort,List<Task>> mTasks;

        public ResponseValue(@NonNull Map<Sort,List<Task>> tasks) {
            mTasks = checkNotNull(tasks, "tasks cannot be null!");
        }

        public Map<Sort,List<Task>> getTasks() {
            return mTasks;
        }
    }
}
