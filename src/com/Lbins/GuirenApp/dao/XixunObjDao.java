package com.Lbins.GuirenApp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.Lbins.GuirenApp.module.XixunObj;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table XIXUN_OBJ.
*/
public class XixunObjDao extends AbstractDao<XixunObj, String> {

    public static final String TABLENAME = "XIXUN_OBJ";

    /**
     * Properties of entity XixunObj.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Guiren_xixun_id = new Property(0, String.class, "guiren_xixun_id", true, "GUIREN_XIXUN_ID");
        public final static Property Guiren_xixun_title = new Property(1, String.class, "guiren_xixun_title", false, "GUIREN_XIXUN_TITLE");
        public final static Property Mm_emp_id = new Property(2, String.class, "mm_emp_id", false, "MM_EMP_ID");
        public final static Property Dateline = new Property(3, String.class, "dateline", false, "DATELINE");
        public final static Property Mm_emp_nickname = new Property(4, String.class, "mm_emp_nickname", false, "MM_EMP_NICKNAME");
        public final static Property Mm_emp_cover = new Property(5, String.class, "mm_emp_cover", false, "MM_EMP_COVER");
        public final static Property Mm_hangye_name = new Property(6, String.class, "mm_hangye_name", false, "MM_HANGYE_NAME");
        public final static Property Mm_emp_sex = new Property(7, String.class, "mm_emp_sex", false, "MM_EMP_SEX");
        public final static Property Mm_emp_mobile = new Property(8, String.class, "mm_emp_mobile", false, "MM_EMP_MOBILE");
        public final static Property Lat = new Property(9, String.class, "lat", false, "LAT");
        public final static Property Lng = new Property(10, String.class, "lng", false, "LNG");
    };

    private DaoSession daoSession;


    public XixunObjDao(DaoConfig config) {
        super(config);
    }
    
    public XixunObjDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'XIXUN_OBJ' (" + //
                "'GUIREN_XIXUN_ID' TEXT PRIMARY KEY NOT NULL ," + // 0: guiren_xixun_id
                "'GUIREN_XIXUN_TITLE' TEXT NOT NULL ," + // 1: guiren_xixun_title
                "'MM_EMP_ID' TEXT NOT NULL ," + // 2: mm_emp_id
                "'DATELINE' TEXT," + // 3: dateline
                "'MM_EMP_NICKNAME' TEXT," + // 4: mm_emp_nickname
                "'MM_EMP_COVER' TEXT," + // 5: mm_emp_cover
                "'MM_HANGYE_NAME' TEXT," + // 6: mm_hangye_name
                "'MM_EMP_SEX' TEXT," + // 7: mm_emp_sex
                "'MM_EMP_MOBILE' TEXT," + // 8: mm_emp_mobile
                "'LAT' TEXT," + // 9: lat
                "'LNG' TEXT);"); // 10: lng
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'XIXUN_OBJ'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, XixunObj entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getGuiren_xixun_id());
        stmt.bindString(2, entity.getGuiren_xixun_title());
        stmt.bindString(3, entity.getMm_emp_id());
 
        String dateline = entity.getDateline();
        if (dateline != null) {
            stmt.bindString(4, dateline);
        }
 
        String mm_emp_nickname = entity.getMm_emp_nickname();
        if (mm_emp_nickname != null) {
            stmt.bindString(5, mm_emp_nickname);
        }
 
        String mm_emp_cover = entity.getMm_emp_cover();
        if (mm_emp_cover != null) {
            stmt.bindString(6, mm_emp_cover);
        }
 
        String mm_hangye_name = entity.getMm_hangye_name();
        if (mm_hangye_name != null) {
            stmt.bindString(7, mm_hangye_name);
        }
 
        String mm_emp_sex = entity.getMm_emp_sex();
        if (mm_emp_sex != null) {
            stmt.bindString(8, mm_emp_sex);
        }
 
        String mm_emp_mobile = entity.getMm_emp_mobile();
        if (mm_emp_mobile != null) {
            stmt.bindString(9, mm_emp_mobile);
        }
 
        String lat = entity.getLat();
        if (lat != null) {
            stmt.bindString(10, lat);
        }
 
        String lng = entity.getLng();
        if (lng != null) {
            stmt.bindString(11, lng);
        }
    }

    @Override
    protected void attachEntity(XixunObj entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public XixunObj readEntity(Cursor cursor, int offset) {
        XixunObj entity = new XixunObj( //
            cursor.getString(offset + 0), // guiren_xixun_id
            cursor.getString(offset + 1), // guiren_xixun_title
            cursor.getString(offset + 2), // mm_emp_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // dateline
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // mm_emp_nickname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // mm_emp_cover
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // mm_hangye_name
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // mm_emp_sex
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // mm_emp_mobile
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // lat
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // lng
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, XixunObj entity, int offset) {
        entity.setGuiren_xixun_id(cursor.getString(offset + 0));
        entity.setGuiren_xixun_title(cursor.getString(offset + 1));
        entity.setMm_emp_id(cursor.getString(offset + 2));
        entity.setDateline(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMm_emp_nickname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMm_emp_cover(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMm_hangye_name(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setMm_emp_sex(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setMm_emp_mobile(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLat(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLng(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(XixunObj entity, long rowId) {
        return entity.getGuiren_xixun_id();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(XixunObj entity) {
        if(entity != null) {
            return entity.getGuiren_xixun_id();
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
