package com.evan.remindme.data.source.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.evan.remindme.settings.domain.model.Setting;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.domain.model.Task;

import com.evan.remindme.data.source.dao.SettingDao;
import com.evan.remindme.data.source.dao.ClassifyDao;
import com.evan.remindme.data.source.dao.TaskDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig settingDaoConfig;
    private final DaoConfig classifyDaoConfig;
    private final DaoConfig taskDaoConfig;

    private final SettingDao settingDao;
    private final ClassifyDao classifyDao;
    private final TaskDao taskDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        settingDaoConfig = daoConfigMap.get(SettingDao.class).clone();
        settingDaoConfig.initIdentityScope(type);

        classifyDaoConfig = daoConfigMap.get(ClassifyDao.class).clone();
        classifyDaoConfig.initIdentityScope(type);

        taskDaoConfig = daoConfigMap.get(TaskDao.class).clone();
        taskDaoConfig.initIdentityScope(type);

        settingDao = new SettingDao(settingDaoConfig, this);
        classifyDao = new ClassifyDao(classifyDaoConfig, this);
        taskDao = new TaskDao(taskDaoConfig, this);

        registerDao(Setting.class, settingDao);
        registerDao(Classify.class, classifyDao);
        registerDao(Task.class, taskDao);
    }
    
    public void clear() {
        settingDaoConfig.clearIdentityScope();
        classifyDaoConfig.clearIdentityScope();
        taskDaoConfig.clearIdentityScope();
    }

    public SettingDao getSettingDao() {
        return settingDao;
    }

    public ClassifyDao getClassifyDao() {
        return classifyDao;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

}
