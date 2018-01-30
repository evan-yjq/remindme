package com.evan.remindme.data.source.local;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.data.source.dao.SortDao;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.util.AppExecutors;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:27
 */
public class SortsLocalDataSource implements SortsDataSource{

    private static volatile SortsLocalDataSource INSTANCE;

    private SortDao mSortDao;

    private AppExecutors mAppExecutors;

    private SortsLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull SortDao sortDao){
        mAppExecutors = appExecutors;
        mSortDao = sortDao;
    }

    public static SortsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull SortDao sortDao){
        if (INSTANCE == null){
            synchronized (SortsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SortsLocalDataSource(appExecutors, sortDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void check(@NonNull final Sort sort, @NonNull final CheckCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Sort s = mSortDao.queryBuilder().where(SortDao.Properties.Name.eq(sort.getName())).unique();
                if (s!=null){
                    callback.onCheck(true);
                }else{
                    callback.onCheck(false);
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
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
    public void getSorts(@NonNull final LoadSortsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Sort> sorts = mSortDao.loadAll();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (sorts.isEmpty()){
                            callback.onDataNotAvailable();
                        }else{
                            callback.onSortsLoaded(sorts);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSort(@NonNull final Long SortId, @NonNull final GetSortCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Sort sort = mSortDao.load(SortId);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (sort != null){
                            callback.onSortLoaded(sort);
                        }else{
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveSort(@NonNull final Sort sort,@NonNull final SaveCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Long id = mSortDao.insert(sort);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSave(id);
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteSort(@NonNull final Long sortId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSortDao.deleteByKey(sortId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllSorts() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSortDao.deleteAll();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void refreshSorts() {

    }

    @Override
    public void updateSort(@NonNull final Sort sort) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mSortDao.update(sort);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSortWithName(@NonNull final String name, @NonNull final GetSortCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Sort sort = mSortDao.queryBuilder().where(SortDao.Properties.Name.eq(name)).unique();
                if (sort != null){
                    callback.onSortLoaded(sort);
                }else{
                    callback.onDataNotAvailable();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
