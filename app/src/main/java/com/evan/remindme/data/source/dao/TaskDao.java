package com.evan.remindme.data.source.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.evan.remindme.tasks.domain.model.Task;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TASK".
*/
public class TaskDao extends AbstractDao<Task, String> {

    public static final String TABLENAME = "TASK";

    /**
     * Properties of entity Task.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Circle = new Property(2, int.class, "circle", false, "CIRCLE");
        public final static Property Repeat = new Property(3, int.class, "repeat", false, "REPEAT");
        public final static Property Time = new Property(4, java.util.Date.class, "time", false, "TIME");
        public final static Property NextTime = new Property(5, java.util.Date.class, "nextTime", false, "NEXT_TIME");
        public final static Property ClassifyId = new Property(6, Long.class, "classifyId", false, "CLASSIFY_ID");
        public final static Property Bell = new Property(7, String.class, "bell", false, "BELL");
        public final static Property TurnOn = new Property(8, boolean.class, "turnOn", false, "TURN_ON");
    }


    public TaskDao(DaoConfig config) {
        super(config);
    }
    
    public TaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TASK\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TITLE\" TEXT," + // 1: title
                "\"CIRCLE\" INTEGER NOT NULL ," + // 2: circle
                "\"REPEAT\" INTEGER NOT NULL ," + // 3: repeat
                "\"TIME\" INTEGER," + // 4: time
                "\"NEXT_TIME\" INTEGER," + // 5: nextTime
                "\"CLASSIFY_ID\" INTEGER," + // 6: classifyId
                "\"BELL\" TEXT," + // 7: bell
                "\"TURN_ON\" INTEGER NOT NULL );"); // 8: turnOn
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TASK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Task entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getCircle());
        stmt.bindLong(4, entity.getRepeat());
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(5, time.getTime());
        }
 
        java.util.Date nextTime = entity.getNextTime();
        if (nextTime != null) {
            stmt.bindLong(6, nextTime.getTime());
        }
 
        Long classifyId = entity.getClassifyId();
        if (classifyId != null) {
            stmt.bindLong(7, classifyId);
        }
 
        String bell = entity.getBell();
        if (bell != null) {
            stmt.bindString(8, bell);
        }
        stmt.bindLong(9, entity.getTurnOn() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Task entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getCircle());
        stmt.bindLong(4, entity.getRepeat());
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(5, time.getTime());
        }
 
        java.util.Date nextTime = entity.getNextTime();
        if (nextTime != null) {
            stmt.bindLong(6, nextTime.getTime());
        }
 
        Long classifyId = entity.getClassifyId();
        if (classifyId != null) {
            stmt.bindLong(7, classifyId);
        }
 
        String bell = entity.getBell();
        if (bell != null) {
            stmt.bindString(8, bell);
        }
        stmt.bindLong(9, entity.getTurnOn() ? 1L: 0L);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public Task readEntity(Cursor cursor, int offset) {
        Task entity = new Task( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.getInt(offset + 2), // circle
            cursor.getInt(offset + 3), // repeat
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // time
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // nextTime
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // classifyId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // bell
            cursor.getShort(offset + 8) != 0 // turnOn
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Task entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCircle(cursor.getInt(offset + 2));
        entity.setRepeat(cursor.getInt(offset + 3));
        entity.setTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setNextTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setClassifyId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setBell(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTurnOn(cursor.getShort(offset + 8) != 0);
     }
    
    @Override
    protected final String updateKeyAfterInsert(Task entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(Task entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Task entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
