package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.TasksRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午11:48
 */
public class TurnOnTask extends UseCase<TurnOnTask.RequestValues,TurnOnTask.ResponseValue>{

    private final TasksRepository mTasksRepository;

    public TurnOnTask(@NonNull TasksRepository tasksRepository){
        mTasksRepository = checkNotNull(tasksRepository,"tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        Long turnOnTask = values.getmTurnOnTask();
        mTasksRepository.turnOnTask(turnOnTask);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Long mTurnOnTask;

        public RequestValues(@NonNull Long turnOnTask){
            mTurnOnTask = checkNotNull(turnOnTask,"turnOnTask cannot be null!");
        }

        public Long getmTurnOnTask() {
            return mTurnOnTask;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
