package com.evan.remindme.sorts.domain.usecase;

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
public class GetSortByName extends UseCase<GetSortByName.RequestValues,GetSortByName.ResponseValue>{

    private final SortsRepository sortsRepository;

    public GetSortByName(@NonNull SortsRepository sortsRepository) {
        this.sortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        String name = requestValues.getName();
        sortsRepository.getSortWithName(name, new SortsDataSource.GetSortCallback() {
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
        private final String name;

        public RequestValues(@NonNull String name) {
            this.name = checkNotNull(name,"name cannot be null!");
        }

        public String getName() {
            return name;
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
