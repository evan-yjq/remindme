package com.evan.remindme.tasks.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.data.source.SortsRepository;
import com.evan.remindme.sorts.domain.model.Sort;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午11:18
 */
public class GetSort extends UseCase<GetSort.RequestValues,GetSort.ResponseValue>{

    private final SortsRepository sortsRepository;

    public GetSort(@NonNull SortsRepository sortsRepository) {
        this.sortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long id = requestValues.getId();
        sortsRepository.getSort(id, new SortsDataSource.GetSortCallback() {
            @Override
            public void onSortLoaded(Sort sort) {
                getUseCaseCallback().onSuccess(new ResponseValue(sort));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
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
    public static final class ResponseValue implements UseCase.ResponseValue {
        private final Sort sort;

        public ResponseValue(@NonNull Sort sort) {
            this.sort = checkNotNull(sort,"sort cannot be null!");
        }

        public Sort getSort() {
            return sort;
        }
    }
}
