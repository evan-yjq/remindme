package com.evan.remindme.data.source;


import android.support.annotation.NonNull;
import com.evan.remindme.tasks.domain.model.Sort;
import com.evan.remindme.tasks.domain.model.Task;

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

    void getSorts(@NonNull LoadSortsCallback callback);

    void getSort(@NonNull String sortId, @NonNull GetSortCallback callback);

    void saveSort(@NonNull Sort sort);

    void deleteSort(@NonNull String sortId);

    void deleteAllSorts();

    void refreshSorts();

    void updateSort(@NonNull Sort sort);
}
