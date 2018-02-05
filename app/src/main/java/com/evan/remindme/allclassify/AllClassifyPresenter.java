package com.evan.remindme.allclassify;

import android.support.annotation.NonNull;
import com.evan.remindme.UseCase;
import com.evan.remindme.UseCaseHandler;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.allclassify.domain.usecase.GetAllClassify;
import com.evan.remindme.allclassify.domain.usecase.SaveClassify;
import com.evan.remindme.allclassify.domain.usecase.DeleteClassify;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午10:21
 */
public class AllClassifyPresenter implements AllClassifyContract.Presenter {

    private final AllClassifyContract.View mAllClassifyView;
    private final GetAllClassify mGetAllClassify;
    private final SaveClassify mSaveClassify;
    private final DeleteClassify mDeleteClassify;

    // 设定第一次是否从网络数据中启动
    private boolean mFirstLoad = false;

    private final UseCaseHandler mUseCaseHandler;

    public AllClassifyPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull AllClassifyContract.View classifyView,
                                @NonNull GetAllClassify getAllClassify, @NonNull SaveClassify saveClassify,
                                @NonNull DeleteClassify deleteClassify){
        mSaveClassify = saveClassify;
        mDeleteClassify = deleteClassify;
        mGetAllClassify = getAllClassify;
        mUseCaseHandler = useCaseHandler;
        mAllClassifyView = classifyView;

        mAllClassifyView.setPresenter(this);
    }

    @Override
    public void start() {
        loadAllClassify(false);
    }

    @Override
    public void rename(@NonNull final Classify classify, @NonNull String name) {
        checkNotNull(classify,"classify cannot be null!");
        checkNotNull(name,"name cannot be null!");
        final String nameBack = classify.getName();
        classify.setName(name);
        mUseCaseHandler.execute(mSaveClassify, new SaveClassify.RequestValues(classify,false),
                new UseCase.UseCaseCallback<SaveClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveClassify.ResponseValue response) {
                        loadAllClassify(false,false);
                    }

                    @Override
                    public void onError() {
                        classify.setName(nameBack);
                        mAllClassifyView.showClassifyRenameError();
                    }
                });
    }

    private int mClassifySize = 0;
    @Override
    public void save(@NonNull String name) {
        checkNotNull(name,"name cannot be null!");
        Classify classify = new Classify(name,mClassifySize);
        mUseCaseHandler.execute(mSaveClassify, new SaveClassify.RequestValues(classify,true),
                new UseCase.UseCaseCallback<SaveClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveClassify.ResponseValue response) {
                        mAllClassifyView.showSuccessfullySavedMessage();
                        loadAllClassify(false,false);
                    }

                    @Override
                    public void onError() {
                        mAllClassifyView.showClassifySaveError();
                    }
                });
    }

    @Override
    public void delete(@NonNull Classify classify) {
        checkNotNull(classify,"classify cannot be null!");
        mUseCaseHandler.execute(mDeleteClassify, new DeleteClassify.RequestValues(classify.getId()),
                new UseCase.UseCaseCallback<DeleteClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteClassify.ResponseValue response) {
                        loadAllClassify(false,false);
                    }

                    @Override
                    public void onError() {
                        mAllClassifyView.showClassifyDeleteError();
                    }
                });
    }

    @Override
    public void loadAllClassify(boolean forceUpdate) {
        loadAllClassify(forceUpdate || mFirstLoad,true);
        mFirstLoad = false;
    }

    @Override
    public void addNewClassify() {
        mAllClassifyView.showAddClassify();
    }

    private void loadAllClassify(boolean forceUpdate, final boolean showLoadingUI){
        if (showLoadingUI){
            mAllClassifyView.setLoadingIndicator(true);
        }

        GetAllClassify.RequestValues requestValues = new GetAllClassify.RequestValues(forceUpdate);

        mUseCaseHandler.execute(mGetAllClassify, requestValues,
                new UseCase.UseCaseCallback<GetAllClassify.ResponseValue>() {
                    @Override
                    public void onSuccess(GetAllClassify.ResponseValue response) {
                        List<Classify> classifies = response.getAllClassify();
                        mClassifySize = classifies.size();
                        if (!mAllClassifyView.isActive()){
                            return;
                        }
                        if (showLoadingUI){
                            mAllClassifyView.setLoadingIndicator(false);
                        }
                        processAllClassify(classifies);
                    }

                    @Override
                    public void onError() {
                        if (!mAllClassifyView.isActive()){
                            return;
                        }
                        if (showLoadingUI){
                            mAllClassifyView.setLoadingIndicator(false);
                        }
                        mAllClassifyView.showLoadingAllClassifyError();
                    }
                });

    }

    private void processAllClassify(List<Classify> classifies){
        if (classifies.isEmpty()){
            mAllClassifyView.showNoClassify();
        }else{
            mAllClassifyView.showAllClassify(classifies);
        }
    }

    public int getClassifySize() {
        return mClassifySize;
    }

    public void setClassifySize(int classifySize) {
        this.mClassifySize = classifySize;
    }
}
