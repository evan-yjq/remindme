package com.evan.remindme.data.source.local;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.data.source.dao.ClassifyDao;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.util.AppExecutors;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:27
 */
public class ClassifyLocalDataSource implements ClassifyDataSource {

    private static volatile ClassifyLocalDataSource INSTANCE;

    private ClassifyDao classifyDao;

    private AppExecutors mAppExecutors;

    private ClassifyLocalDataSource(@NonNull AppExecutors appExecutors,
                                    @NonNull ClassifyDao classifyDao){
        mAppExecutors = appExecutors;
        this.classifyDao = classifyDao;
    }

    public static ClassifyLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                      @NonNull ClassifyDao classifyDao){
        if (INSTANCE == null){
            synchronized (ClassifyLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ClassifyLocalDataSource(appExecutors, classifyDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void check(@NonNull final Classify classify, @NonNull final CheckCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Classify s = classifyDao.queryBuilder().where(ClassifyDao.Properties.Name.eq(classify.getName())).unique();
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
    public void openClassify(@NonNull Classify classify) {
        updateClassify(classify);
    }

    @Override
    public void openClassify(@NonNull Long id) {

    }

    @Override
    public void closeClassify(@NonNull Classify classify) {
        updateClassify(classify);
    }

    @Override
    public void closeClassify(@NonNull Long id) {

    }

    @Override
    public void getAllClassify(@NonNull final LoadAllClassifyCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Classify> classifies = classifyDao.loadAll();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (classifies.isEmpty()){
                            callback.onDataNotAvailable();
                        }else{
                            callback.onAllClassifyLoaded(classifies);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getClassify(@NonNull final Long id, @NonNull final GetClassifyCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Classify classify = classifyDao.load(id);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (classify != null){
                            callback.onClassifyLoaded(classify);
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
    public void saveClassify(@NonNull final Classify classify, @NonNull final SaveCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Long id = classifyDao.insert(classify);
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
    public void deleteClassify(@NonNull final Long id) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                classifyDao.deleteByKey(id);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllClassify() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                classifyDao.deleteAll();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void refreshClassify() {

    }

    @Override
    public void updateClassify(@NonNull final Classify classify) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                classifyDao.update(classify);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getClassifyWithName(@NonNull final String name, @NonNull final GetClassifyCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Classify classify = classifyDao.queryBuilder().where(ClassifyDao.Properties.Name.eq(name)).unique();
                if (classify != null){
                    callback.onClassifyLoaded(classify);
                }else{
                    callback.onDataNotAvailable();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
