package com.evan.remindme.addedittask.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.TasksDataSource;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.tasks.domain.model.Task;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午5:08
 */
public class GetTask extends UseCase<GetTask.RequestValues,GetTask.ResponseValue>{

    private final TasksRepository tasksRepository;

    public GetTask(@NonNull TasksRepository tasksRepository) {
        this.tasksRepository = checkNotNull(tasksRepository,"tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final String id = requestValues.getId();
        tasksRepository.getTask(id, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                getUseCaseCallback().onSuccess(new ResponseValue(task));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String id;

        public RequestValues(@NonNull String id) {
            this.id = checkNotNull(id,"id cannot be null!");
        }

        public String getId() {
            return id;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue{
        private final Task task;

        public ResponseValue(@NonNull Task task) {
            this.task = checkNotNull(task,"task cannot be null!");
        }

        public Task getTask() {
            return task;
        }
    }
}