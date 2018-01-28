package com.evan.remindme.data.source.local;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.sorts.domain.model.Sort;

import com.evan.remindme.data.source.local.TaskDao;
import com.evan.remindme.data.source.local.SortDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig taskDaoConfig;
    private final DaoConfig sortDaoConfig;

    private final TaskDao taskDao;
    private final SortDao sortDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        taskDaoConfig = daoConfigMap.get(TaskDao.class).clone();
        taskDaoConfig.initIdentityScope(type);

        sortDaoConfig = daoConfigMap.get(SortDao.class).clone();
        sortDaoConfig.initIdentityScope(type);

        taskDao = new TaskDao(taskDaoConfig, this);
        sortDao = new SortDao(sortDaoConfig, this);

        registerDao(Task.class, taskDao);
        registerDao(Sort.class, sortDao);
    }
    
    public void clear() {
        taskDaoConfig.clearIdentityScope();
        sortDaoConfig.clearIdentityScope();
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public SortDao getSortDao() {
        return sortDao;
    }

}
