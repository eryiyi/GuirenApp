package com.Lbins.GuirenApp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.Lbins.GuirenApp.module.Emp;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table EMP.
*/
public class EmpDao extends AbstractDao<Emp, String> {

    public static final String TABLENAME = "EMP";

    /**
     * Properties of entity Emp.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Hxusername = new Property(0, String.class, "hxusername", true, "HXUSERNAME");
        public final static Property Mm_emp_mobile = new Property(1, String.class, "mm_emp_mobile", false, "MM_EMP_MOBILE");
        public final static Property Mm_emp_nickname = new Property(2, String.class, "mm_emp_nickname", false, "MM_EMP_NICKNAME");
        public final static Property Mm_emp_card = new Property(3, String.class, "mm_emp_card", false, "MM_EMP_CARD");
        public final static Property Mm_emp_password = new Property(4, String.class, "mm_emp_password", false, "MM_EMP_PASSWORD");
        public final static Property Mm_emp_cover = new Property(5, String.class, "mm_emp_cover", false, "MM_EMP_COVER");
        public final static Property Mm_emp_company = new Property(6, String.class, "mm_emp_company", false, "MM_EMP_COMPANY");
        public final static Property Mm_emp_provinceId = new Property(7, String.class, "mm_emp_provinceId", false, "MM_EMP_PROVINCE_ID");
        public final static Property Mm_emp_cityId = new Property(8, String.class, "mm_emp_cityId", false, "MM_EMP_CITY_ID");
        public final static Property Mm_emp_countryId = new Property(9, String.class, "mm_emp_countryId", false, "MM_EMP_COUNTRY_ID");
        public final static Property Mm_emp_regtime = new Property(10, String.class, "mm_emp_regtime", false, "MM_EMP_REGTIME");
        public final static Property Is_login = new Property(11, String.class, "is_login", false, "IS_LOGIN");
        public final static Property Is_use = new Property(12, String.class, "is_use", false, "IS_USE");
        public final static Property Lat = new Property(13, String.class, "lat", false, "LAT");
        public final static Property Lng = new Property(14, String.class, "lng", false, "LNG");
        public final static Property Ischeck = new Property(15, String.class, "ischeck", false, "ISCHECK");
        public final static Property Is_upate_profile = new Property(16, String.class, "is_upate_profile", false, "IS_UPATE_PROFILE");
        public final static Property UserId = new Property(17, String.class, "userId", false, "USER_ID");
        public final static Property ChannelId = new Property(18, String.class, "channelId", false, "CHANNEL_ID");
        public final static Property DeviceType = new Property(19, String.class, "deviceType", false, "DEVICE_TYPE");
        public final static Property Mm_emp_email = new Property(20, String.class, "mm_emp_email", false, "MM_EMP_EMAIL");
        public final static Property Mm_emp_sex = new Property(21, String.class, "mm_emp_sex", false, "MM_EMP_SEX");
        public final static Property Mm_emp_birthday = new Property(22, String.class, "mm_emp_birthday", false, "MM_EMP_BIRTHDAY");
        public final static Property Mm_emp_techang = new Property(23, String.class, "mm_emp_techang", false, "MM_EMP_TECHANG");
        public final static Property Mm_emp_xingqu = new Property(24, String.class, "mm_emp_xingqu", false, "MM_EMP_XINGQU");
        public final static Property Mm_emp_detail = new Property(25, String.class, "mm_emp_detail", false, "MM_EMP_DETAIL");
        public final static Property Guiren_card_num = new Property(26, String.class, "guiren_card_num", false, "GUIREN_CARD_NUM");
        public final static Property Mm_hangye_id = new Property(27, String.class, "mm_hangye_id", false, "MM_HANGYE_ID");
        public final static Property Mm_emp_up_emp = new Property(28, String.class, "mm_emp_up_emp", false, "MM_EMP_UP_EMP");
        public final static Property ProvinceName = new Property(29, String.class, "provinceName", false, "PROVINCE_NAME");
        public final static Property CityName = new Property(30, String.class, "cityName", false, "CITY_NAME");
        public final static Property Mm_hangye_name = new Property(31, String.class, "mm_hangye_name", false, "MM_HANGYE_NAME");
        public final static Property AreaName = new Property(32, String.class, "areaName", false, "AREA_NAME");
        public final static Property Top_number = new Property(33, String.class, "top_number", false, "TOP_NUMBER");
        public final static Property Mm_emp_weixin = new Property(34, String.class, "mm_emp_weixin", false, "MM_EMP_WEIXIN");
        public final static Property Mm_emp_qq = new Property(35, String.class, "mm_emp_qq", false, "MM_EMP_QQ");
        public final static Property Mm_emp_age = new Property(36, String.class, "mm_emp_age", false, "MM_EMP_AGE");
        public final static Property Mm_emp_native = new Property(37, String.class, "mm_emp_native", false, "MM_EMP_NATIVE");
        public final static Property Mm_emp_motto = new Property(38, String.class, "mm_emp_motto", false, "MM_EMP_MOTTO");
        public final static Property Mm_emp_id = new Property(39, String.class, "mm_emp_id", false, "MM_EMP_ID");
        public final static Property Mm_emp_type = new Property(40, String.class, "mm_emp_type", false, "MM_EMP_TYPE");
        public final static Property Mm_emp_bg = new Property(41, String.class, "mm_emp_bg", false, "MM_EMP_BG");
    };

    private DaoSession daoSession;


    public EmpDao(DaoConfig config) {
        super(config);
    }
    
    public EmpDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'EMP' (" + //
                "'HXUSERNAME' TEXT PRIMARY KEY NOT NULL ," + // 0: hxusername
                "'MM_EMP_MOBILE' TEXT NOT NULL ," + // 1: mm_emp_mobile
                "'MM_EMP_NICKNAME' TEXT NOT NULL ," + // 2: mm_emp_nickname
                "'MM_EMP_CARD' TEXT," + // 3: mm_emp_card
                "'MM_EMP_PASSWORD' TEXT NOT NULL ," + // 4: mm_emp_password
                "'MM_EMP_COVER' TEXT NOT NULL ," + // 5: mm_emp_cover
                "'MM_EMP_COMPANY' TEXT," + // 6: mm_emp_company
                "'MM_EMP_PROVINCE_ID' TEXT," + // 7: mm_emp_provinceId
                "'MM_EMP_CITY_ID' TEXT," + // 8: mm_emp_cityId
                "'MM_EMP_COUNTRY_ID' TEXT," + // 9: mm_emp_countryId
                "'MM_EMP_REGTIME' TEXT," + // 10: mm_emp_regtime
                "'IS_LOGIN' TEXT," + // 11: is_login
                "'IS_USE' TEXT," + // 12: is_use
                "'LAT' TEXT," + // 13: lat
                "'LNG' TEXT," + // 14: lng
                "'ISCHECK' TEXT," + // 15: ischeck
                "'IS_UPATE_PROFILE' TEXT," + // 16: is_upate_profile
                "'USER_ID' TEXT," + // 17: userId
                "'CHANNEL_ID' TEXT," + // 18: channelId
                "'DEVICE_TYPE' TEXT," + // 19: deviceType
                "'MM_EMP_EMAIL' TEXT," + // 20: mm_emp_email
                "'MM_EMP_SEX' TEXT," + // 21: mm_emp_sex
                "'MM_EMP_BIRTHDAY' TEXT," + // 22: mm_emp_birthday
                "'MM_EMP_TECHANG' TEXT," + // 23: mm_emp_techang
                "'MM_EMP_XINGQU' TEXT," + // 24: mm_emp_xingqu
                "'MM_EMP_DETAIL' TEXT," + // 25: mm_emp_detail
                "'GUIREN_CARD_NUM' TEXT," + // 26: guiren_card_num
                "'MM_HANGYE_ID' TEXT," + // 27: mm_hangye_id
                "'MM_EMP_UP_EMP' TEXT," + // 28: mm_emp_up_emp
                "'PROVINCE_NAME' TEXT," + // 29: provinceName
                "'CITY_NAME' TEXT," + // 30: cityName
                "'MM_HANGYE_NAME' TEXT," + // 31: mm_hangye_name
                "'AREA_NAME' TEXT," + // 32: areaName
                "'TOP_NUMBER' TEXT," + // 33: top_number
                "'MM_EMP_WEIXIN' TEXT," + // 34: mm_emp_weixin
                "'MM_EMP_QQ' TEXT," + // 35: mm_emp_qq
                "'MM_EMP_AGE' TEXT," + // 36: mm_emp_age
                "'MM_EMP_NATIVE' TEXT," + // 37: mm_emp_native
                "'MM_EMP_MOTTO' TEXT," + // 38: mm_emp_motto
                "'MM_EMP_ID' TEXT NOT NULL ," + // 39: mm_emp_id
                "'MM_EMP_TYPE' TEXT," + // 40: mm_emp_type
                "'MM_EMP_BG' TEXT);"); // 41: mm_emp_bg
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'EMP'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Emp entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getHxusername());
        stmt.bindString(2, entity.getMm_emp_mobile());
        stmt.bindString(3, entity.getMm_emp_nickname());
 
        String mm_emp_card = entity.getMm_emp_card();
        if (mm_emp_card != null) {
            stmt.bindString(4, mm_emp_card);
        }
        stmt.bindString(5, entity.getMm_emp_password());
        stmt.bindString(6, entity.getMm_emp_cover());
 
        String mm_emp_company = entity.getMm_emp_company();
        if (mm_emp_company != null) {
            stmt.bindString(7, mm_emp_company);
        }
 
        String mm_emp_provinceId = entity.getMm_emp_provinceId();
        if (mm_emp_provinceId != null) {
            stmt.bindString(8, mm_emp_provinceId);
        }
 
        String mm_emp_cityId = entity.getMm_emp_cityId();
        if (mm_emp_cityId != null) {
            stmt.bindString(9, mm_emp_cityId);
        }
 
        String mm_emp_countryId = entity.getMm_emp_countryId();
        if (mm_emp_countryId != null) {
            stmt.bindString(10, mm_emp_countryId);
        }
 
        String mm_emp_regtime = entity.getMm_emp_regtime();
        if (mm_emp_regtime != null) {
            stmt.bindString(11, mm_emp_regtime);
        }
 
        String is_login = entity.getIs_login();
        if (is_login != null) {
            stmt.bindString(12, is_login);
        }
 
        String is_use = entity.getIs_use();
        if (is_use != null) {
            stmt.bindString(13, is_use);
        }
 
        String lat = entity.getLat();
        if (lat != null) {
            stmt.bindString(14, lat);
        }
 
        String lng = entity.getLng();
        if (lng != null) {
            stmt.bindString(15, lng);
        }
 
        String ischeck = entity.getIscheck();
        if (ischeck != null) {
            stmt.bindString(16, ischeck);
        }
 
        String is_upate_profile = entity.getIs_upate_profile();
        if (is_upate_profile != null) {
            stmt.bindString(17, is_upate_profile);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(18, userId);
        }
 
        String channelId = entity.getChannelId();
        if (channelId != null) {
            stmt.bindString(19, channelId);
        }
 
        String deviceType = entity.getDeviceType();
        if (deviceType != null) {
            stmt.bindString(20, deviceType);
        }
 
        String mm_emp_email = entity.getMm_emp_email();
        if (mm_emp_email != null) {
            stmt.bindString(21, mm_emp_email);
        }
 
        String mm_emp_sex = entity.getMm_emp_sex();
        if (mm_emp_sex != null) {
            stmt.bindString(22, mm_emp_sex);
        }
 
        String mm_emp_birthday = entity.getMm_emp_birthday();
        if (mm_emp_birthday != null) {
            stmt.bindString(23, mm_emp_birthday);
        }
 
        String mm_emp_techang = entity.getMm_emp_techang();
        if (mm_emp_techang != null) {
            stmt.bindString(24, mm_emp_techang);
        }
 
        String mm_emp_xingqu = entity.getMm_emp_xingqu();
        if (mm_emp_xingqu != null) {
            stmt.bindString(25, mm_emp_xingqu);
        }
 
        String mm_emp_detail = entity.getMm_emp_detail();
        if (mm_emp_detail != null) {
            stmt.bindString(26, mm_emp_detail);
        }
 
        String guiren_card_num = entity.getGuiren_card_num();
        if (guiren_card_num != null) {
            stmt.bindString(27, guiren_card_num);
        }
 
        String mm_hangye_id = entity.getMm_hangye_id();
        if (mm_hangye_id != null) {
            stmt.bindString(28, mm_hangye_id);
        }
 
        String mm_emp_up_emp = entity.getMm_emp_up_emp();
        if (mm_emp_up_emp != null) {
            stmt.bindString(29, mm_emp_up_emp);
        }
 
        String provinceName = entity.getProvinceName();
        if (provinceName != null) {
            stmt.bindString(30, provinceName);
        }
 
        String cityName = entity.getCityName();
        if (cityName != null) {
            stmt.bindString(31, cityName);
        }
 
        String mm_hangye_name = entity.getMm_hangye_name();
        if (mm_hangye_name != null) {
            stmt.bindString(32, mm_hangye_name);
        }
 
        String areaName = entity.getAreaName();
        if (areaName != null) {
            stmt.bindString(33, areaName);
        }
 
        String top_number = entity.getTop_number();
        if (top_number != null) {
            stmt.bindString(34, top_number);
        }
 
        String mm_emp_weixin = entity.getMm_emp_weixin();
        if (mm_emp_weixin != null) {
            stmt.bindString(35, mm_emp_weixin);
        }
 
        String mm_emp_qq = entity.getMm_emp_qq();
        if (mm_emp_qq != null) {
            stmt.bindString(36, mm_emp_qq);
        }
 
        String mm_emp_age = entity.getMm_emp_age();
        if (mm_emp_age != null) {
            stmt.bindString(37, mm_emp_age);
        }
 
        String mm_emp_native = entity.getMm_emp_native();
        if (mm_emp_native != null) {
            stmt.bindString(38, mm_emp_native);
        }
 
        String mm_emp_motto = entity.getMm_emp_motto();
        if (mm_emp_motto != null) {
            stmt.bindString(39, mm_emp_motto);
        }
        stmt.bindString(40, entity.getMm_emp_id());
 
        String mm_emp_type = entity.getMm_emp_type();
        if (mm_emp_type != null) {
            stmt.bindString(41, mm_emp_type);
        }
 
        String mm_emp_bg = entity.getMm_emp_bg();
        if (mm_emp_bg != null) {
            stmt.bindString(42, mm_emp_bg);
        }
    }

    @Override
    protected void attachEntity(Emp entity) {
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
    public Emp readEntity(Cursor cursor, int offset) {
        Emp entity = new Emp( //
            cursor.getString(offset + 0), // hxusername
            cursor.getString(offset + 1), // mm_emp_mobile
            cursor.getString(offset + 2), // mm_emp_nickname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // mm_emp_card
            cursor.getString(offset + 4), // mm_emp_password
            cursor.getString(offset + 5), // mm_emp_cover
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // mm_emp_company
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // mm_emp_provinceId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // mm_emp_cityId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // mm_emp_countryId
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // mm_emp_regtime
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // is_login
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // is_use
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // lat
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // lng
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // ischeck
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // is_upate_profile
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // userId
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // channelId
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // deviceType
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // mm_emp_email
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // mm_emp_sex
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // mm_emp_birthday
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // mm_emp_techang
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // mm_emp_xingqu
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // mm_emp_detail
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // guiren_card_num
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // mm_hangye_id
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // mm_emp_up_emp
            cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29), // provinceName
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // cityName
            cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31), // mm_hangye_name
            cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32), // areaName
            cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33), // top_number
            cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34), // mm_emp_weixin
            cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35), // mm_emp_qq
            cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36), // mm_emp_age
            cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37), // mm_emp_native
            cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38), // mm_emp_motto
            cursor.getString(offset + 39), // mm_emp_id
            cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40), // mm_emp_type
            cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41) // mm_emp_bg
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Emp entity, int offset) {
        entity.setHxusername(cursor.getString(offset + 0));
        entity.setMm_emp_mobile(cursor.getString(offset + 1));
        entity.setMm_emp_nickname(cursor.getString(offset + 2));
        entity.setMm_emp_card(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMm_emp_password(cursor.getString(offset + 4));
        entity.setMm_emp_cover(cursor.getString(offset + 5));
        entity.setMm_emp_company(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setMm_emp_provinceId(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setMm_emp_cityId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setMm_emp_countryId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setMm_emp_regtime(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIs_login(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIs_use(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setLat(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setLng(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setIscheck(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setIs_upate_profile(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setUserId(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setChannelId(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setDeviceType(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setMm_emp_email(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setMm_emp_sex(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setMm_emp_birthday(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setMm_emp_techang(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setMm_emp_xingqu(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setMm_emp_detail(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setGuiren_card_num(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setMm_hangye_id(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setMm_emp_up_emp(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setProvinceName(cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29));
        entity.setCityName(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setMm_hangye_name(cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31));
        entity.setAreaName(cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32));
        entity.setTop_number(cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33));
        entity.setMm_emp_weixin(cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34));
        entity.setMm_emp_qq(cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35));
        entity.setMm_emp_age(cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36));
        entity.setMm_emp_native(cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37));
        entity.setMm_emp_motto(cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38));
        entity.setMm_emp_id(cursor.getString(offset + 39));
        entity.setMm_emp_type(cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40));
        entity.setMm_emp_bg(cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Emp entity, long rowId) {
        return entity.getHxusername();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Emp entity) {
        if(entity != null) {
            return entity.getHxusername();
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
