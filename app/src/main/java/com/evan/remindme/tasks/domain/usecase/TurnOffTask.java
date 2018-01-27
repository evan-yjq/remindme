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
public class TurnOffTask extends UseCase<TurnOffTask.RequestValues,TurnOffTask.ResponseValue> {

    private final TasksRepository mTasksRepository;

    public TurnOffTask(@NonNull TasksRepository tasksRepository){
        mTasksRepository = checkNotNull(tasksRepository,"tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(TurnOffTask.RequestValues values) {
        String turnOffTask = values.getmTurnOffTask();
        mTasksRepository.turnOffTask(turnOffTask);
        getUseCaseCallback().onSuccess(new TurnOffTask.ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTurnOffTask;

        public RequestValues(@NonNull String turnOffTask){
            mTurnOffTask = checkNotNull(turnOffTask,"turnOffTask cannot be null!");
        }

        public String getmTurnOffTask() {
            return mTurnOffTask;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
