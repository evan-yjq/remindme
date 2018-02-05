package com.evan.remindme.data.source.remote;

import android.support.annotation.NonNull;
import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.util.Objects;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 下午10:08
 */
public class ClassifyRemoteDataSource implements ClassifyDataSource {

    private static ClassifyRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<Long, Classify> CLASSIFY_SERVICE_DATA;

    static {
        CLASSIFY_SERVICE_DATA = new LinkedHashMap<>(1);
        addClassify((long)1,"默认",1);
    }

    private static void addClassify(Long id,String title,int sort) {
        Classify newClassify = new Classify(id,title,sort);
        CLASSIFY_SERVICE_DATA.put(id, newClassify);
    }

    public static ClassifyRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassifyRemoteDataSource();
        }
        return INSTANCE;
    }

    //防止直接实例化。
    private ClassifyRemoteDataSource(){}

    @Override
    public void check(@NonNull Classify classify, @NonNull CheckCallback callback) {
        for (Classify s : CLASSIFY_SERVICE_DATA.values()) {
            if (Objects.equal(classify.getName(),s.getName())) {
                callback.onCheck(true);
                return;
            }
        }
        callback.onCheck(false);
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
    public void getAllClassify(@NonNull LoadAllClassifyCallback callback) {
        callback.onAllClassifyLoaded(Objects.Lists.newArrayList(CLASSIFY_SERVICE_DATA.values()));
    }

    @Override
    public void getClassify(@NonNull Long id, @NonNull GetClassifyCallback callback) {
        Classify classify = CLASSIFY_SERVICE_DATA.get(id);
        if (classify!=null)callback.onClassifyLoaded(classify);
        else callback.onDataNotAvailable();
    }

    @Override
    public void saveClassify(@NonNull Classify classify, @NonNull SaveCallback callback) {
        CLASSIFY_SERVICE_DATA.put(classify.getId(), classify);
    }

    @Override
    public void deleteClassify(@NonNull Long id) {
        CLASSIFY_SERVICE_DATA.remove(id);
    }

    @Override
    public void deleteAllClassify() {
        CLASSIFY_SERVICE_DATA.clear();
    }

    @Override
    public void refreshClassify() {

    }

    @Override
    public void updateClassify(@NonNull Classify classify) {
        Classify updateClassify = new Classify(classify);
        CLASSIFY_SERVICE_DATA.put(classify.getId(), updateClassify);
    }

    @Override
    public void getClassifyWithName(@NonNull String name, @NonNull GetClassifyCallback callback) {
        for (Classify classify : CLASSIFY_SERVICE_DATA.values()) {
            if (Objects.equal(classify.getName(),name)) {
                callback.onClassifyLoaded(classify);
                break;
            }
        }
        callback.onDataNotAvailable();
    }
}
