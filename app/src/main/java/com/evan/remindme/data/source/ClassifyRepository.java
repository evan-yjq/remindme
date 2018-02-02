package com.evan.remindme.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evan.remindme.allclassify.domain.model.Classify;

import java.util.*;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午6:51
 */
public class ClassifyRepository implements ClassifyDataSource {

    private static ClassifyRepository INSTANCE = null;

    private final ClassifyDataSource mClassifyRemoteDataSource;

    private final ClassifyDataSource mClassifyLocalDataSource;

    private Map<Long, Classify> mCachedClassify;

    private boolean mCacheIsDirty = false;

    //防止直接实例化。
    private ClassifyRepository(@NonNull ClassifyDataSource classifyRemoteDataSource,
                               @NonNull ClassifyDataSource classifyLocalDataSource) {
        mClassifyRemoteDataSource = checkNotNull(classifyRemoteDataSource);
        mClassifyLocalDataSource = checkNotNull(classifyLocalDataSource);
    }

    /**
     * 返回这个类的单个实例，如果需要的话创建它。
     *
     * @param classifyRemoteDataSource 网络数据源
     * @param classifyLocalDataSource  设备存储数据源
     * @return the {@link ClassifyRepository} instance
     */
    public static ClassifyRepository getInstance(ClassifyDataSource classifyRemoteDataSource,
                                                 ClassifyDataSource classifyLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ClassifyRepository(classifyRemoteDataSource, classifyLocalDataSource);
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
    public void openClassify(@NonNull Classify classify) {
        checkNotNull(classify);
        classify.setIsOpen(true);
        mClassifyRemoteDataSource.openClassify(classify);
        mClassifyLocalDataSource.openClassify(classify);

        Classify openTask = new Classify(classify);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedClassify== null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.put(classify.getId(), openTask);
    }

    @Override
    public void openClassify(@NonNull Long id) {
        checkNotNull(id);
        openClassify(Objects.requireNonNull(getClassifyWithId(id)));
    }

    @Override
    public void closeClassify(@NonNull Classify classify) {
        checkNotNull(classify);
        classify.setIsOpen(false);
        mClassifyRemoteDataSource.closeClassify(classify);
        mClassifyLocalDataSource.closeClassify(classify);

        Classify closeTask = new Classify(classify);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedClassify== null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.put(classify.getId(), closeTask);
    }

    @Override
    public void closeClassify(@NonNull Long id) {
        checkNotNull(id);
        closeClassify(Objects.requireNonNull(getClassifyWithId(id)));
    }

    @Override
    public void getAllClassify(@NonNull final LoadAllClassifyCallback callback) {
        checkNotNull(callback);

        //如果可用且不肮脏，则立即响应缓存
        if (mCachedClassify != null && !mCacheIsDirty) {
            callback.onAllClassifyLoaded(new ArrayList<>(mCachedClassify.values()));
            return;
        }

        if (mCacheIsDirty) {
            // 如果缓存脏了，我们需要从网络中获取新的数据。
            getAllClassifyFromRemoteDataSource(callback);
        }else {
            // 查询本地存储（如果可用）。如果不行，则查询网络。
            mClassifyLocalDataSource.getAllClassify(new LoadAllClassifyCallback() {
                @Override
                public void onAllClassifyLoaded(List<Classify> classifies) {
                    refreshCache(classifies);
                    callback.onAllClassifyLoaded(new ArrayList<>(mCachedClassify.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getAllClassifyFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getClassify(@NonNull final Long id, @NonNull final GetClassifyCallback callback) {
        checkNotNull(id);
        checkNotNull(callback);

        Classify cachedClassify = getClassifyWithId(id);

        //如果可用，立即响应缓存
        if (cachedClassify != null) {
            callback.onClassifyLoaded(cachedClassify);
            return;
        }

        //从服务器加载/保持如果需要。

        //本地存储其中是否有Classify，如果没有，从网上加载
        mClassifyLocalDataSource.getClassify(id, new GetClassifyCallback() {
            @Override
            public void onClassifyLoaded(Classify classify) {
                // 在内存缓存更新，以保持应用程序界面最新
                if (mCachedClassify == null) {
                    mCachedClassify = new LinkedHashMap<>();
                }
                mCachedClassify.put(id, classify);
                callback.onClassifyLoaded(classify);
            }

            @Override
            public void onDataNotAvailable() {
                mClassifyRemoteDataSource.getClassify(id, new GetClassifyCallback() {
                    @Override
                    public void onClassifyLoaded(Classify classify) {
                        // 在内存缓存更新，以保持应用程序界面最新
                        if (mCachedClassify == null) {
                            mCachedClassify = new LinkedHashMap<>();
                        }
                        mCachedClassify.put(id, classify);
                        callback.onClassifyLoaded(classify);
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
    public void saveClassify(@NonNull final Classify classify, @NonNull final SaveCallback callback){
        checkNotNull(classify);
        checkNotNull(callback);
        mClassifyLocalDataSource.saveClassify(classify, new SaveCallback() {
            @Override
            public void onSave(Long id) {
                classify.setId(id);
                mClassifyRemoteDataSource.saveClassify(classify,callback);
                //在内存缓存更新，以保持应用程序界面最新
                if (mCachedClassify == null) {
                    mCachedClassify = new LinkedHashMap<>();
                }

                mCachedClassify.put(id, classify);
                callback.onSave(id);
            }
        });
    }

    @Override
    public void check(@NonNull final Classify classify, @NonNull final CheckCallback callback){
        checkNotNull(classify);
        checkNotNull(callback);
        mClassifyLocalDataSource.check(classify, new CheckCallback() {
            @Override
            public void onCheck(boolean b) {
                if (b){
                    callback.onCheck(true);
                }else{
                    mClassifyRemoteDataSource.check(classify, new CheckCallback() {
                        @Override
                        public void onCheck(boolean b) {
                            callback.onCheck(b);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void deleteClassify(@NonNull Long id) {
        checkNotNull(id);
        mClassifyRemoteDataSource.deleteClassify(id);
        mClassifyLocalDataSource.deleteClassify(id);
        if (mCachedClassify == null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.remove(id);
    }

    @Override
    public void deleteAllClassify() {
        mClassifyRemoteDataSource.deleteAllClassify();
        mClassifyLocalDataSource.deleteAllClassify();

        if (mCachedClassify == null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.clear();
    }

    @Override
    public void refreshClassify() {
        mCacheIsDirty = true;
    }

    @Override
    public void updateClassify(@NonNull Classify classify) {
        checkNotNull(classify);
        mClassifyRemoteDataSource.updateClassify(classify);
        mClassifyLocalDataSource.updateClassify(classify);

        Classify updateClassify = new Classify(classify);
        //在内存缓存更新，以保持应用程序界面最新
        if (mCachedClassify == null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.put(classify.getId(), updateClassify);
    }

    @Override
    public void getClassifyWithName(@NonNull final String name, @NonNull final GetClassifyCallback callback) {
        checkNotNull(name);
        checkNotNull(callback);

        final Classify classify = getClassifyWithName(name);

        //如果可用，立即响应缓存
        if (classify != null) {
            callback.onClassifyLoaded(classify);
            return;
        }
        //从服务器加载/保持如果需要。
        //本地存储其中是否有ID，如果没有，从网上加载
        mClassifyLocalDataSource.getClassifyWithName(name, new GetClassifyCallback() {
            @Override
            public void onClassifyLoaded(Classify classify) {
                // 在内存缓存更新，以保持应用程序界面最新
                if (mCachedClassify == null) {
                    mCachedClassify = new LinkedHashMap<>();
                }
                mCachedClassify.put(classify.getId(), classify);
                callback.onClassifyLoaded(classify);
            }

            @Override
            public void onDataNotAvailable() {
                mClassifyRemoteDataSource.getClassifyWithName(name, new GetClassifyCallback() {
                    @Override
                    public void onClassifyLoaded(Classify classify) {
                        // 在内存缓存更新，以保持应用程序界面最新
                        if (mCachedClassify == null) {
                            mCachedClassify = new LinkedHashMap<>();
                        }
                        mCachedClassify.put(classify.getId(), classify);
                        callback.onClassifyLoaded(classify);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    private void getAllClassifyFromRemoteDataSource(@NonNull final LoadAllClassifyCallback callback) {
        mClassifyRemoteDataSource.getAllClassify(new LoadAllClassifyCallback() {
            @Override
            public void onAllClassifyLoaded(List<Classify> classifies) {
                refreshCache(classifies);
                refreshLocalDataSource(classifies);
                callback.onAllClassifyLoaded(new ArrayList<>(mCachedClassify.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Classify> classifies) {
        if (mCachedClassify == null) {
            mCachedClassify = new LinkedHashMap<>();
        }
        mCachedClassify.clear();
        for (Classify classify : classifies) {
            mCachedClassify.put(classify.getId(), classify);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Classify> classifies) {
        mClassifyLocalDataSource.deleteAllClassify();
        for (final Classify classify : classifies) {
            mClassifyLocalDataSource.saveClassify(classify, new SaveCallback() {
                @Override
                public void onSave(Long id) {
                    classify.setId(id);
                }
            });
        }
    }

    @Nullable
    private Classify getClassifyWithName(@NonNull String name){
        checkNotNull(name);
        if (mCachedClassify != null && !mCachedClassify.isEmpty()) {
            for (Classify classify : mCachedClassify.values()) {
                if (com.evan.remindme.util.Objects.equal(classify.getName(),name)) {
                    return classify;
                }
            }
        }
        return null;
    }

    @Nullable
    private Classify getClassifyWithId(@NonNull Long id) {
        checkNotNull(id);
        if (mCachedClassify == null || mCachedClassify.isEmpty()) {
            return null;
        } else {
            return mCachedClassify.get(id);
        }
    }
}
