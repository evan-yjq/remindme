package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午6:33
 */
public class OpenSort extends UseCase<OpenSort.RequestValues,OpenSort.ResponseValue>{

    private final SortsRepository mSortsRepository;

    public OpenSort(@NonNull SortsRepository mSortsRepository) {
        this.mSortsRepository = checkNotNull(mSortsRepository,"mSortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long openSort = requestValues.getmOpenSort();
        mSortsRepository.openSort(openSort);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Long mOpenSort;

        public RequestValues(@NonNull Long openSort){
            mOpenSort = checkNotNull(openSort,"openSort cannot be null!");
        }

        public Long getmOpenSort() {
            return mOpenSort;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
