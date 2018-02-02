package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午7:05
 */
public class CloseClassify extends UseCase<CloseClassify.RequestValues,CloseClassify.ResponseValue> {

    private final ClassifyRepository classifyRepository;

    public CloseClassify(@NonNull ClassifyRepository classifyRepository) {
        this.classifyRepository = checkNotNull(classifyRepository,"classifyRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long id = requestValues.getId();
        classifyRepository.closeClassify(id);
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
