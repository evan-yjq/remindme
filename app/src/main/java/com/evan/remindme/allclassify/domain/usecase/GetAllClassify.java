package com.evan.remindme.allclassify.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.ClassifyRepository;
import com.evan.remindme.allclassify.domain.model.Classify;

import java.util.Collections;
import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:50
 */
public class GetAllClassify extends UseCase<GetAllClassify.RequestValues,GetAllClassify.ResponseValue>{

    private final ClassifyRepository classifyRepository;

    public GetAllClassify(@NonNull ClassifyRepository classifyRepository){
        this.classifyRepository = checkNotNull(classifyRepository,"classifyRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (requestValues.isForceUpdate()){
            classifyRepository.refreshClassify();
        }

        classifyRepository.getAllClassify(new ClassifyDataSource.LoadAllClassifyCallback() {
            @Override
            public void onAllClassifyLoaded(List<Classify> classifies) {
                Collections.sort(classifies);
                getUseCaseCallback().onSuccess(new ResponseValue(classifies));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate){
            mForceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue {
        private final List<Classify> mClassifies;

        public ResponseValue(@NonNull List<Classify> classifies){
            mClassifies = checkNotNull(classifies,"classifies cannot be null!");
        }

        public List<Classify> getAllClassify() {
            return mClassifies;
        }
    }
}
