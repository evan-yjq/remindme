package com.evan.remindme.classify.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.ClassifyRepository;
import com.evan.remindme.classify.domain.model.Classify;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:50
 */
public class GetSorts extends UseCase<GetSorts.RequestValues,GetSorts.ResponseValue>{

    private final ClassifyRepository mSortsRepository;

    public GetSorts(@NonNull ClassifyRepository sortsRepository){
        mSortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (requestValues.isForceUpdate()){
            mSortsRepository.refreshSorts();
        }

        mSortsRepository.getSorts(new ClassifyDataSource.LoadSortsCallback() {
            @Override
            public void onSortsLoaded(List<Classify> classifies) {
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

        public List<Classify> getSorts() {
            return mClassifies;
        }
    }
}
