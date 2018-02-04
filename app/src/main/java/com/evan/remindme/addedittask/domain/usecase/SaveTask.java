package com.evan.remindme.addedittask.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.TasksRepository;
import com.evan.remindme.tasks.domain.model.Task;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/27
 * Time: 下午8:26
 */
public class SaveTask extends UseCase<SaveTask.RequestValues,SaveTask.ResponseValue>{

    private final TasksRepository mTasksRepository;

    public SaveTask(@NonNull TasksRepository tasksRepository){
        mTasksRepository = checkNotNull(tasksRepository,"tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(SaveTask.RequestValues values) {
        Task task = values.getSaveTask();
        if (values.isNew()) {
            mTasksRepository.saveTask(task);
        }else {
            mTasksRepository.updateTask(task);
        }
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Task mSaveTask;
        private final boolean isNew;

        public RequestValues(@NonNull Task saveTask,@NonNull boolean isNew){
            mSaveTask = checkNotNull(saveTask,"saveTask cannot be null!");
            this.isNew = checkNotNull(isNew,"isNew cannot be null!");
        }

        public Task getSaveTask() {
            return mSaveTask;
        }

        public boolean isNew() {
            return isNew;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
