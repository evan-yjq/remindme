package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午6:33
 */
public class OpenClassify extends UseCase<OpenClassify.RequestValues,OpenClassify.ResponseValue>{

    private final ClassifyRepository classifyRepository;

    public OpenClassify(@NonNull ClassifyRepository classifyRepository) {
        this.classifyRepository = checkNotNull(classifyRepository,"classifyRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long id = requestValues.getId();
        classifyRepository.openClassify(id);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Long id;

        public RequestValues(@NonNull Long id){
            this.id = checkNotNull(id,"id cannot be null!");
        }

        public Long getId() {
            return id;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
