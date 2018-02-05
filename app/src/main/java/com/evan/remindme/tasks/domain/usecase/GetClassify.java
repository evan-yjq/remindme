package com.evan.remindme.tasks.domain.usecase;

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
 * Time: 下午11:18
 */
public class GetClassify extends UseCase<GetClassify.RequestValues,GetClassify.ResponseValue>{

    private final ClassifyRepository classifyRepository;

    public GetClassify(@NonNull ClassifyRepository classifyRepository) {
        this.classifyRepository = checkNotNull(classifyRepository,"classifyRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Long id = requestValues.getId();
        classifyRepository.getClassify(id, new ClassifyDataSource.GetClassifyCallback() {
            @Override
            public void onClassifyLoaded(Classify classify) {
                getUseCaseCallback().onSuccess(new ResponseValue(classify));
            }

            @Override
            public void onDataNotAvailable() {
                Classify classify = new Classify((long)1,"默认",1);
                getUseCaseCallback().onSuccess(new ResponseValue(classify));
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
        private final Classify classify;

        public ResponseValue(@NonNull Classify classify) {
            this.classify = checkNotNull(classify,"classify cannot be null!");
        }

        public Classify getClassify() {
            return classify;
        }
    }
}
