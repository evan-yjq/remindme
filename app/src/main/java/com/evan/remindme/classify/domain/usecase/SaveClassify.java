package com.evan.remindme.classify.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.ClassifyRepository;
import com.evan.remindme.classify.domain.model.Classify;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:20
 */
public class SaveSort extends UseCase<SaveSort.RequestValues,SaveSort.ResponseValue>{

    private final ClassifyRepository sortsRepository;

    public SaveSort(@NonNull ClassifyRepository sortsRepository) {
        this.sortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final Classify classify = requestValues.getSort();
        final boolean isNew = requestValues.isNew();
        sortsRepository.check(classify, new ClassifyDataSource.CheckCallback() {
            @Override
            public void onCheck(boolean b) {
                if (b){
                    getUseCaseCallback().onError();
                }else{
                    if(isNew) {
                        sortsRepository.saveSort(classify, new ClassifyDataSource.SaveCallback() {
                            @Override
                            public void onSave(Long id) {
                                getUseCaseCallback().onSuccess(new ResponseValue(classify));
                            }
                        });
                    }else{
                        sortsRepository.updateSort(classify);
                        getUseCaseCallback().onSuccess(new ResponseValue(classify));
                    }
                }
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Classify mClassify;
        private final boolean isNew;

        public RequestValues(@NonNull Classify classify, @NonNull boolean isNew) {
            this.mClassify = checkNotNull(classify,"classify cannot be null!");
            this.isNew = checkNotNull(isNew,"isNew cannot be null!");
        }

        public boolean isNew() {
            return isNew;
        }

        public Classify getSort() {
            return mClassify;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue{
        private final Classify mClassify;

        public ResponseValue(@NonNull Classify classify) {
            this.mClassify = checkNotNull(classify,"classify cannot be null!");
        }

        public Classify getSort() {
            return mClassify;
        }
    }
}
