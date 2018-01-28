package com.evan.remindme.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.sorts.domain.model.Sort;

import java.util.*;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午6:51
 */
public class SortsRepository implements SortsDataSource{

    private static SortsRepository INSTANCE = null;

    private final SortsDataSource mSortsRemoteDataSource;

    private final SortsDataSource mSortsLocalDataSource;

    private Map<Long, Sort> mCachedSorts;

    private boolean mCacheIsDirty = false;

    //防止直接实例化。
    private SortsRepository(@NonNull SortsDataSource sortsRemoteDataSource,
                            @NonNull SortsDataSource sortsLocalDataSource) {
        mSortsRemoteDataSource = checkNotNull(sortsRemoteDataSource);
        mSortsLocalDataSource = checkNotNull(sortsLocalDataSource);
    }

    /**
     * 返回这个类的单个实例，如果需要的话创建它。
     *
     * @param sortsRemoteDataSource 网络数据源
     * @param sortsLocalDataSource  设备存储数据源
     * @return the {@link SortsRepository} instance
     */
    public static SortsRepository getInstance(SortsDataSource sortsRemoteDataSource,
                                              SortsDataSource sortsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SortsRepository(sortsRemoteDataSource, sortsLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * 用于在下次调用时强制创建一个新实例。
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void openSort(@NonNull Sort sort) {
        checkNotNull(sort);
        mSortsRemoteDataSource.openSort(sort);
        mSortsLocalDataSource.openSort(sort);

        sort.setIsOpen(true);
        Sort openTask = new Sort(sort);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedSorts== null) {
            mCachedSorts = new LinkedHashMap<>();
        }
        mCachedSorts.put(sort.getId(), openTask);
    }

    @Override
    public void openSort(@NonNull Long sortId) {
        checkNotNull(sortId);
        openSort(Objects.requireNonNull(getSortWithId(sortId)));
    }

    @Override
    public void closeSort(@NonNull Sort sort) {
        checkNotNull(sort);
        mSortsRemoteDataSource.closeSort(sort);
        mSortsLocalDataSource.closeSort(sort);

        sort.setIsOpen(false);
        Sort closeTask = new Sort(sort);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedSorts== null) {
            mCachedSorts = new LinkedHashMap<>();
        }
        mCachedSorts.put(sort.getId(), closeTask);
    }

    @Override
    public void closeSort(@NonNull Long sortId) {
        checkNotNull(sortId);
        closeSort(Objects.requireNonNull(getSortWithId(sortId)));
    }

    @Override
    public void getSorts(@NonNull final LoadSortsCallback callback) {
        checkNotNull(callback);

        //如果可用且不肮脏，则立即响应缓存
        if (mCachedSorts != null && !mCacheIsDirty) {
            callback.onSortsLoaded(new ArrayList<>(mCachedSorts.values()));
            return;
        }

        if (mCacheIsDirty) {
            // 如果缓存脏了，我们需要从网络中获取新的数据。
            getSortsFromRemoteDataSource(callback);
        }else {
            // 查询本地存储（如果可用）。如果不行，则查询网络。
            mSortsLocalDataSource.getSorts(new LoadSortsCallback() {
                @Override
                public void onSortsLoaded(List<Sort> sorts) {
                    refreshCache(sorts);
                    callback.onSortsLoaded(new ArrayList<>(mCachedSorts.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getSortsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getSort(@NonNull final Long sortId, @NonNull final GetSortCallback callback) {
        checkNotNull(sortId);
        checkNotNull(callback);

        Sort cachedSort = getSortWithId(sortId);

        //如果可用，立即响应缓存
        if (cachedSort != null) {
            callback.onSortLoaded(cachedSort);
            return;
        }

        //从服务器加载/保持如果需要。

        //本地存储其中是否有Sort，如果没有，从网上加载
        mSortsLocalDataSource.getSort(sortId, new GetSortCallback() {
            @Override
            public void onSortLoaded(Sort sort) {
                // 在内存缓存更新，以保持应用程序界面最新
                if (mCachedSorts == null) {
                    mCachedSorts = new LinkedHashMap<>();
                }
                mCachedSorts.put(sort.getId(), sort);
                callback.onSortLoaded(sort);
            }

            @Override
            public void onDataNotAvailable() {
                mSortsRemoteDataSource.getSort(sortId, new GetSortCallback() {
                    @Override
                    public void onSortLoaded(Sort sort) {
                        // 在内存缓存更新，以保持应用程序界面最新
                        if (mCachedSorts == null) {
                            mCachedSorts = new LinkedHashMap<>();
                        }
                        mCachedSorts.put(sort.getId(), sort);
                        callback.onSortLoaded(sort);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveSort(@NonNull final Sort sort,@NonNull final SaveCallback callback){
        checkNotNull(sort);

        mSortsLocalDataSource.saveSort(sort, new SaveCallback() {
            @Override
            public void onSave(Long id) {
                sort.setId(id);
                mSortsRemoteDataSource.saveSort(sort,callback);
                //在内存缓存更新，以保持应用程序界面最新
                if (mCachedSorts == null) {
                    mCachedSorts = new LinkedHashMap<>();
                }

                mCachedSorts.put(sort.getId(), sort);
                callback.onSave(id);
            }
        });
    }

    @Override
    public void check(@NonNull final Sort sort, @NonNull final CheckCallback callback){
        checkNotNull(sort);
        mSortsLocalDataSource.check(sort, new CheckCallback() {
            @Override
            public void onCheck(boolean b) {
                if (b){
                    callback.onCheck(true);
                }else{
                    mSortsRemoteDataSource.check(sort, new CheckCallback() {
                        @Override
                        public void onCheck(boolean b) {
                            if (b){
                                callback.onCheck(true);
                            }else{
                                callback.onCheck(false);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void deleteSort(@NonNull Long sortId) {
        mSortsRemoteDataSource.deleteSort(checkNotNull(sortId));
        mSortsLocalDataSource.deleteSort(checkNotNull(sortId));

        mCachedSorts.remove(sortId);
    }

    @Override
    public void deleteAllSorts() {
        mSortsRemoteDataSource.deleteAllSorts();
        mSortsLocalDataSource.deleteAllSorts();

        if (mCachedSorts == null) {
            mCachedSorts = new LinkedHashMap<>();
        }
        mCachedSorts.clear();
    }

    @Override
    public void refreshSorts() {
        mCacheIsDirty = true;
    }

    @Override
    public void updateSort(@NonNull Sort sort) {
        checkNotNull(sort);
        mSortsRemoteDataSource.updateSort(sort);
        mSortsLocalDataSource.updateSort(sort);

        Sort updateSort = new Sort(sort);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedSorts == null) {
            mCachedSorts = new LinkedHashMap<>();
        }
        mCachedSorts.put(sort.getId(), updateSort);
    }

    @Override
    public void getSortWithName(@NonNull final String name, @NonNull final GetSortCallback callback) {
        checkNotNull(callback);

        final Sort sort = getSortWithName(name);

        //如果可用，立即响应缓存
        if (sort != null) {
            callback.onSortLoaded(sort);
            return;
        }
        //从服务器加载/保持如果需要。
        //本地存储其中是否有ID，如果没有，从网上加载
        mSortsLocalDataSource.getSortWithName(name, new GetSortCallback() {
            @Override
            public void onSortLoaded(Sort sort) {
                // 在内存缓存更新，以保持应用程序界面最新
                if (mCachedSorts == null) {
                    mCachedSorts = new LinkedHashMap<>();
                }
                mCachedSorts.put(sort.getId(), sort);
                callback.onSortLoaded(sort);
            }

            @Override
            public void onDataNotAvailable() {
                mSortsRemoteDataSource.getSortWithName(name, new GetSortCallback() {
                    @Override
                    public void onSortLoaded(Sort sort) {
                        // 在内存缓存更新，以保持应用程序界面最新
                        if (mCachedSorts == null) {
                            mCachedSorts = new LinkedHashMap<>();
                        }
                        mCachedSorts.put(sort.getId(), sort);
                        callback.onSortLoaded(sort);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    private void getSortsFromRemoteDataSource(@NonNull final LoadSortsCallback callback) {
        mSortsRemoteDataSource.getSorts(new LoadSortsCallback() {
            @Override
            public void onSortsLoaded(List<Sort> sorts) {
                refreshCache(sorts);
                refreshLocalDataSource(sorts);
                callback.onSortsLoaded(new ArrayList<>(mCachedSorts.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Sort> sorts) {
        if (mCachedSorts == null) {
            mCachedSorts = new LinkedHashMap<>();
        }
        mCachedSorts.clear();
        for (Sort sort : sorts) {
            mCachedSorts.put(sort.getId(), sort);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Sort> sorts) {
        mSortsLocalDataSource.deleteAllSorts();
        for (final Sort sort : sorts) {
            mSortsLocalDataSource.saveSort(sort, new SaveCallback() {
                @Override
                public void onSave(Long id) {
                    sort.setId(id);
                }
            });
        }
    }

    @Nullable
    private Sort getSortWithName(@NonNull String name){
        checkNotNull(name);
        if (mCachedSorts != null && !mCachedSorts.isEmpty()) {
            for (Sort sort : mCachedSorts.values()) {
                if (com.evan.remindme.util.Objects.equal(sort.getName(),name)) {
                    return sort;
                }
            }
        }
        return null;
    }

    @Nullable
    private Sort getSortWithId(@NonNull Long id) {
        checkNotNull(id);
        if (mCachedSorts == null || mCachedSorts.isEmpty()) {
            return null;
        } else {
            return mCachedSorts.get(id);
        }
    }
}
