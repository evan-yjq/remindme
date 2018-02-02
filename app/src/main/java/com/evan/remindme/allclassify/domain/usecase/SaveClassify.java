package com.evan.remindme.allclassify.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.ClassifyRepository;
import com.evan.remindme.allclassify.domain.model.Classify;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:20
 */
public class SaveClassify extends UseCase<SaveClassify.RequestValues,SaveClassify.ResponseValue>{

    private final ClassifyRepository classifyRepository;

    public SaveClassify(@NonNull ClassifyRepository classifyRepository) {
        this.classifyRepository = checkNotNull(classifyRepository,"classifyRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final Classify classify = requestValues.getClassify();
        final boolean isNew = requestValues.isNew();
        classifyRepository.check(classify, new ClassifyDataSource.CheckCallback() {
            @Override
            public void onCheck(boolean b) {
                if (b){
                    getUseCaseCallback().onError();
                }else{
                    if(isNew) {
                        classifyRepository.saveClassify(classify, new ClassifyDataSource.SaveCallback() {
                            @Override
                            public void onSave(Long id) {
                                getUseCaseCallback().onSuccess(new ResponseValue(classify));
                            }
                        });
                    }else{
                        classifyRepository.updateClassify(classify);
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

        public Classify getClassify() {
            return mClassify;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue{
        private final Classify mClassify;

        public ResponseValue(@NonNull Classify classify) {
            this.mClassify = checkNotNull(classify,"classify cannot be null!");
        }

        public Classify getClassify() {
            return mClassify;
        }
    }
}
