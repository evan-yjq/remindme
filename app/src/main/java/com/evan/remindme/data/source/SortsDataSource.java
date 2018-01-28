package com.evan.remindme.data.source;


import android.support.annotation.NonNull;
import com.evan.remindme.sorts.domain.model.Sort;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:23
 */
public interface SortsDataSource {
    interface LoadSortsCallback{

        void onSortsLoaded(List<Sort> sorts);

        void onDataNotAvailable();
    }

    interface GetSortCallback{

        void onSortLoaded(Sort sort);

        void onDataNotAvailable();
    }

    interface SaveCallback{
        void onSave(Long id);
    }

    interface CheckCallback{
        void onCheck(boolean b);
    }

    void check(@NonNull Sort sort,@NonNull CheckCallback callback);

    void openSort(@NonNull Sort sort);

    void openSort(@NonNull Long sortId);

    void closeSort(@NonNull Sort sort);

    void closeSort(@NonNull Long sortId);

    void getSorts(@NonNull LoadSortsCallback callback);

    void getSort(@NonNull Long sortId, @NonNull GetSortCallback callback);

    void saveSort(@NonNull Sort sort,@NonNull SaveCallback callback);

    void deleteSort(@NonNull Long sortId);

    void deleteAllSorts();

    void refreshSorts();

    void updateSort(@NonNull Sort sort);

    void getSortWithName(@NonNull String name,@NonNull GetSortCallback callback);
}
