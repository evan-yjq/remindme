package com.evan.remindme.sorts.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:21
 */
public class DeleteSort extends UseCase<DeleteSort.RequestValues,DeleteSort.ResponseValue>{

    private final SortsRepository sortsRepository;

    public DeleteSort(@NonNull SortsRepository sortsRepository) {
        this.sortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long id = requestValues.getId();
        sortsRepository.deleteSort(id);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Long id;

        public RequestValues(@NonNull Long id) {
            this.id = checkNotNull(id,"id cannot be null!");
        }

        public Long getId() {
            return id;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue{

    }
}
