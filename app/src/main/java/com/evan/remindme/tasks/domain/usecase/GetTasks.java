package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.data.source.SortsRepository;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.tasks.TasksDisplayType;
import com.evan.remindme.tasks.domain.display.DisplayFactory;
import com.evan.remindme.tasks.domain.display.TaskDisplay;
import com.evan.remindme.tasks.domain.model.Sort;
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

    private final SortsRepository mSortsRepository;

    private final DisplayFactory mDisplayFactory;

    public GetTasks(@NonNull TasksRepository tasksRepository, @NonNull SortsRepository sortsRepository,
                    @NonNull DisplayFactory displayFactory) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mSortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
        mDisplayFactory = checkNotNull(displayFactory, "filterFactory cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()) {
            mTasksRepository.refreshTasks();
            mSortsRepository.refreshSorts();
        }
        TasksDisplayType currentDisplaying = values.getCurrentDisplaying();
        final TaskDisplay taskDisplay = mDisplayFactory.create(currentDisplaying);

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                taskDisplay.setList(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

        mSortsRepository.getSorts(new SortsDataSource.LoadSortsCallback() {
            @Override
            public void onSortsLoaded(final List<Sort> sorts) {
                Map<String,List<Task>> tasksDisplay = taskDisplay.display(sorts);
                ResponseValue responseValue = new ResponseValue(tasksDisplay);
                getUseCaseCallback().onSuccess(responseValue);
            }
            @Override
            public void onDataNotAvailable() {
                Map<String,List<Task>> tasksDisplay = taskDisplay.display(null);
                ResponseValue responseValue = new ResponseValue(tasksDisplay);
                getUseCaseCallback().onSuccess(responseValue);
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
        private final Map<String,List<Task>> mTasks;

        public ResponseValue(@NonNull Map<String,List<Task>> tasks) {
            mTasks = checkNotNull(tasks, "tasks cannot be null!");
        }

        public Map<String,List<Task>> getTasks() {
            return mTasks;
        }
    }
}
