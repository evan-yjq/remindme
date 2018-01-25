package com.evan.remindme.data.source.remote;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.tasks.domain.model.Sort;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.Objects;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午10:08
 */
public class SortsRemoteDataSource implements SortsDataSource {

    private static SortsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<Long, Sort> SORTS_SERVICE_DATA;

    static {
        SORTS_SERVICE_DATA = new LinkedHashMap<>(1);
        addSort("默认",1);
        addSort("新建",2);
    }

    private static void addSort(String title,long id) {
        Sort newSort = new Sort();
        newSort.setName(title);
        newSort.setId(id);
        SORTS_SERVICE_DATA.put(newSort.getId(), newSort);
    }

    public static SortsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SortsRemoteDataSource();
        }
        return INSTANCE;
    }

    //防止直接实例化。
    private SortsRemoteDataSource(){}

    @Override
    public void getSorts(@NonNull LoadSortsCallback callback) {
        callback.onSortsLoaded(Objects.Lists.newArrayList(SORTS_SERVICE_DATA.values()));
    }

    @Override
    public void getSort(@NonNull Long sortId, @NonNull GetSortCallback callback) {
        Sort sort = SORTS_SERVICE_DATA.get(sortId);
        callback.onSortLoaded(sort);
    }

    @Override
    public void saveSort(@NonNull Sort sort) {
        SORTS_SERVICE_DATA.put(sort.getId(),sort);
    }

    @Override
    public void deleteSort(@NonNull Long sortId) {
        SORTS_SERVICE_DATA.remove(sortId);
    }

    @Override
    public void deleteAllSorts() {
        SORTS_SERVICE_DATA.clear();
    }

    @Override
    public void refreshSorts() {

    }

    @Override
    public void updateSort(@NonNull Sort sort) {
        Sort updateSort = new Sort(sort);
        SORTS_SERVICE_DATA.put(sort.getId(), updateSort);
    }
}
