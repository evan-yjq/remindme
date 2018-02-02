package com.evan.remindme.data.source;


import android.support.annotation.NonNull;
import com.evan.remindme.allclassify.domain.model.Classify;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:23
 */
public interface ClassifyDataSource {
    interface LoadAllClassifyCallback{

        void onAllClassifyLoaded(List<Classify> classifies);

        void onDataNotAvailable();
    }

    interface GetClassifyCallback{

        void onClassifyLoaded(Classify classify);

        void onDataNotAvailable();
    }

    interface SaveCallback{
        void onSave(Long id);
    }

    interface CheckCallback{
        void onCheck(boolean b);
    }

    void check(@NonNull Classify classify, @NonNull CheckCallback callback);

    void openClassify(@NonNull Classify classify);

    void openClassify(@NonNull Long id);

    void closeClassify(@NonNull Classify classify);

    void closeClassify(@NonNull Long id);

    void getAllClassify(@NonNull LoadAllClassifyCallback callback);

    void getClassify(@NonNull Long id, @NonNull GetClassifyCallback callback);

    void saveClassify(@NonNull Classify classify, @NonNull SaveCallback callback);

    void deleteClassify(@NonNull Long id);

    void deleteAllClassify();

    void refreshClassify();

    void updateClassify(@NonNull Classify classify);

    void getClassifyWithName(@NonNull String name,@NonNull GetClassifyCallback callback);
}
