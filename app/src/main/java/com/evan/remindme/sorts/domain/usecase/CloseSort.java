package com.evan.remindme.sorts.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsRepository;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午7:05
 */
public class CloseSort extends UseCase<CloseSort.RequestValues,CloseSort.ResponseValue> {

    private final SortsRepository mSortsRepository;

    public CloseSort(@NonNull SortsRepository mSortsRepository) {
        this.mSortsRepository = checkNotNull(mSortsRepository,"mSortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long closeSort = requestValues.getmCloseSort();
        mSortsRepository.closeSort(closeSort);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Long mCloseSort;

        public RequestValues(@NonNull Long closeSort){
            mCloseSort = checkNotNull(closeSort,"closeSort cannot be null!");
        }

        public Long getmCloseSort() {
            return mCloseSort;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
