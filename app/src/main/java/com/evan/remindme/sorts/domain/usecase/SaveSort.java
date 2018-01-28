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
 * Time: 下午9:20
 */
public class SaveSort extends UseCase<SaveSort.RequestValues,SaveSort.ResponseValue>{

    private final SortsRepository sortsRepository;

    public SaveSort(@NonNull SortsRepository sortsRepository) {
        this.sortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final Sort sort = requestValues.getSort();
        final boolean isNew = requestValues.isNew();
        sortsRepository.check(sort, new SortsDataSource.CheckCallback() {
            @Override
            public void onCheck(boolean b) {
                if (b){
                    getUseCaseCallback().onError();
                }else{
                    if (isNew) {
                        sortsRepository.saveSort(sort, new SortsDataSource.SaveCallback() {
                            @Override
                            public void onSave(Long id) {
                                sort.setId(id);
                                getUseCaseCallback().onSuccess(new ResponseValue(sort));
                            }
                        });
                    }else{
                        sortsRepository.updateSort(sort);
                        getUseCaseCallback().onSuccess(new ResponseValue(sort));
                    }
                }
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Sort mSort;
        private final boolean isNew;

        public RequestValues(@NonNull Sort sort,@NonNull boolean isNew) {
            this.mSort = checkNotNull(sort,"sort cannot be null!");
            this.isNew = checkNotNull(isNew,"isNew cannot be null!");
        }

        public boolean isNew() {
            return isNew;
        }

        public Sort getSort() {
            return mSort;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue{
        private final Sort mSort;

        public ResponseValue(@NonNull Sort sort) {
            this.mSort = checkNotNull(sort,"sort cannot be null!");
        }

        public Sort getSort() {
            return mSort;
        }
    }
}
