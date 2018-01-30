package com.evan.remindme.sorts;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.sorts.domain.usecase.SaveSort;
import com.evan.remindme.sorts.domain.usecase.DeleteSort;
import com.evan.remindme.sorts.domain.usecase.GetSorts;
import com.evan.remindme.tasks.TasksContract;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午10:21
 */
public class SortsPresenter implements SortsContract.Presenter {

    private final SortsContract.View mSortsView;
    private final GetSorts mGetSorts;
    private final SaveSort mSaveSort;
    private final DeleteSort mDeleteSort;

    // 设定第一次是否从网络数据中启动
    private boolean mFirstLoad = false;

    private final UseCaseHandler mUseCaseHandler;

    public SortsPresenter(@NonNull UseCaseHandler useCaseHandler,@NonNull SortsContract.View sortView,
                          @NonNull GetSorts getSorts,@NonNull SaveSort saveSort,
                          @NonNull DeleteSort deleteSort){
        mSaveSort = saveSort;
        mDeleteSort = deleteSort;
        mGetSorts = getSorts;
        mUseCaseHandler = useCaseHandler;
        mSortsView = sortView;

        mSortsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadSorts(false);
    }

    @Override
    public void rename(@NonNull Sort sort) {
        checkNotNull(sort,"sort cannot be null!");
        mUseCaseHandler.execute(mSaveSort, new SaveSort.RequestValues(sort,false),
                new UseCase.UseCaseCallback<SaveSort.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveSort.ResponseValue response) {
                        loadSorts(false,false);
                    }

                    @Override
                    public void onError() {
                        mSortsView.showSortRenameError();
                    }
                });
    }

    @Override
    public void save(@NonNull Sort sort) {
        checkNotNull(sort,"sort cannot be null!");
        mUseCaseHandler.execute(mSaveSort, new SaveSort.RequestValues(sort,true),
                new UseCase.UseCaseCallback<SaveSort.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveSort.ResponseValue response) {
                        mSortsView.showSuccessfullySavedMessage();
                        loadSorts(false,false);
                    }

                    @Override
                    public void onError() {
                        mSortsView.showSortSaveError();
                    }
                });
    }

    @Override
    public void delete(@NonNull Sort sort) {
        checkNotNull(sort,"sort cannot be null!");
        mUseCaseHandler.execute(mDeleteSort, new DeleteSort.RequestValues(sort.getId()),
                new UseCase.UseCaseCallback<DeleteSort.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteSort.ResponseValue response) {
                        loadSorts(false,false);
                    }

                    @Override
                    public void onError() {
                        mSortsView.showSortDeleteError();
                    }
                });
    }

    @Override
    public void loadSorts(boolean forceUpdate) {
        loadSorts(forceUpdate || mFirstLoad,true);
        mFirstLoad = false;
    }

    @Override
    public void addNewSort() {
        mSortsView.showAddSort();
    }

    private void loadSorts(boolean forceUpdate, final boolean showLoadingUI){
        if (showLoadingUI){
            mSortsView.setLoadingIndicator(true);
        }

        GetSorts.RequestValues requestValues = new GetSorts.RequestValues(forceUpdate);

        mUseCaseHandler.execute(mGetSorts, requestValues,
                new UseCase.UseCaseCallback<GetSorts.ResponseValue>() {
                    @Override
                    public void onSuccess(GetSorts.ResponseValue response) {
                        List<Sort> sorts = response.getSorts();
                        if (!mSortsView.isActive()){
                            return;
                        }
                        if (showLoadingUI){
                            mSortsView.setLoadingIndicator(false);
                        }
                        processSorts(sorts);
                    }

                    @Override
                    public void onError() {
                        if (!mSortsView.isActive()){
                            return;
                        }
                        if (showLoadingUI){
                            mSortsView.setLoadingIndicator(false);
                        }
                        mSortsView.showLoadingSortsError();
                    }
                });

    }

    private void processSorts(List<Sort>sorts){
        if (sorts.isEmpty()){
            mSortsView.showNoSort();
        }else{
            mSortsView.showSorts(sorts);
        }
    }
}
