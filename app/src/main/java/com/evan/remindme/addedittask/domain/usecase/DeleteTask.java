package com.evan.remindme.addedittask.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.TasksRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午5:09
 */
public class DeleteTask extends UseCase<DeleteTask.RequestValues,DeleteTask.ResponseValue>{

    private final TasksRepository tasksRepository;

    public DeleteTask(@NonNull TasksRepository tasksRepository) {
        this.tasksRepository = checkNotNull(tasksRepository,"tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final String id = requestValues.getId();
        tasksRepository.deleteTask(id);
        getUseCaseCallback().onSuccess(new ResponseValue());
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

    public static final class ResponseValue implements UseCase.ResponseValue{}

}
