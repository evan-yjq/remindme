package com.evan.remindme.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.util.Objects;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evan.remindme.util.Objects.checkNotNull;

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
        addSort((long)1,"默认");
        addSort((long)2,"新建");
    }

    private static void addSort(Long id,String title) {
        Sort newSort = new Sort(id,title);
        SORTS_SERVICE_DATA.put(id, newSort);
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
    public void check(@NonNull Sort sort, @NonNull CheckCallback callback) {
        for (Sort s : SORTS_SERVICE_DATA.values()) {
            if (Objects.equal(sort.getName(),s.getName())) {
                callback.onCheck(true);
                return;
            }
        }
        callback.onCheck(false);
    }

    @Override
    public void openSort(@NonNull Sort sort) {
        updateSort(sort);
    }

    @Override
    public void openSort(@NonNull Long sortId) {

    }

    @Override
    public void closeSort(@NonNull Sort sort) {
        updateSort(sort);
    }

    @Override
    public void closeSort(@NonNull Long sortId) {

    }

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
    public void saveSort(@NonNull Sort sort,@NonNull SaveCallback callback) {
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

    @Override
    public void getSortWithName(@NonNull String name, @NonNull GetSortCallback callback) {
        for (Sort sort : SORTS_SERVICE_DATA.values()) {
            if (Objects.equal(sort.getName(),name)) {
                callback.onSortLoaded(sort);
                break;
            }
        }
        callback.onDataNotAvailable();
    }
}
