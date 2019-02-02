package com.zcmedical.common.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BLOOD_SUGAR".
*/
public class BloodSugarDao extends AbstractDao<BloodSugar, Long> {

    public static final String TABLENAME = "BLOOD_SUGAR";

    /**
     * Properties of entity BloodSugar.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Meal_type = new Property(1, int.class, "meal_type", false, "MEAL_TYPE");
        public final static Property User_id = new Property(2, String.class, "user_id", false, "USER_ID");
        public final static Property Remarks = new Property(3, String.class, "remarks", false, "REMARKS");
        public final static Property Blood_sugar = new Property(4, int.class, "blood_sugar", false, "BLOOD_SUGAR");
        public final static Property Measure_time = new Property(5, long.class, "measure_time", false, "MEASURE_TIME");
        public final static Property Created_at = new Property(6, long.class, "created_at", false, "CREATED_AT");
    };


    public BloodSugarDao(DaoConfig config) {
        super(config);
    }
    
    public BloodSugarDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BLOOD_SUGAR\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"MEAL_TYPE\" INTEGER NOT NULL ," + // 1: meal_type
                "\"USER_ID\" TEXT NOT NULL ," + // 2: user_id
                "\"REMARKS\" TEXT," + // 3: remarks
                "\"BLOOD_SUGAR\" INTEGER NOT NULL ," + // 4: blood_sugar
                "\"MEASURE_TIME\" INTEGER NOT NULL ," + // 5: measure_time
                "\"CREATED_AT\" INTEGER NOT NULL );"); // 6: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BLOOD_SUGAR\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BloodSugar entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMeal_type());
        stmt.bindString(3, entity.getUser_id());
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(4, remarks);
        }
        stmt.bindLong(5, entity.getBlood_sugar());
        stmt.bindLong(6, entity.getMeasure_time());
        stmt.bindLong(7, entity.getCreated_at());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BloodSugar readEntity(Cursor cursor, int offset) {
        BloodSugar entity = new BloodSugar( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // meal_type
            cursor.getString(offset + 2), // user_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // remarks
            cursor.getInt(offset + 4), // blood_sugar
            cursor.getLong(offset + 5), // measure_time
            cursor.getLong(offset + 6) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BloodSugar entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMeal_type(cursor.getInt(offset + 1));
        entity.setUser_id(cursor.getString(offset + 2));
        entity.setRemarks(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBlood_sugar(cursor.getInt(offset + 4));
        entity.setMeasure_time(cursor.getLong(offset + 5));
        entity.setCreated_at(cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BloodSugar entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BloodSugar entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
