package com.evan.remindme.sorts.domain.usecase;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.data.source.SortsRepository;
import com.evan.remindme.sorts.domain.model.Sort;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午9:50
 */
public class GetSorts extends UseCase<GetSorts.RequestValues,GetSorts.ResponseValue>{

    private final SortsRepository mSortsRepository;

    public GetSorts(@NonNull SortsRepository sortsRepository){
        mSortsRepository = checkNotNull(sortsRepository,"sortsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (requestValues.isForceUpdate()){
            mSortsRepository.refreshSorts();
        }

        mSortsRepository.getSorts(new SortsDataSource.LoadSortsCallback() {
            @Override
            public void onSortsLoaded(List<Sort> sorts) {
                getUseCaseCallback().onSuccess(new ResponseValue(sorts));
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
        private final List<Sort>mSorts;

        public ResponseValue(@NonNull List<Sort>sorts){
            mSorts = checkNotNull(sorts,"sorts cannot be null!");
        }

        public List<Sort> getSorts() {
            return mSorts;
        }
    }
}
