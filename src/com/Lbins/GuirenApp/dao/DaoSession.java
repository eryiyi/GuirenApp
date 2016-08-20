package com.Lbins.GuirenApp.dao;

import android.database.sqlite.SQLiteDatabase;
import com.Lbins.GuirenApp.module.*;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import java.util.Map;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig empDaoConfig;

    private final EmpDao empDao;


    private final DaoConfig xixunObjDaoConfig;
    private final DaoConfig recordDaoConfig;
    private final DaoConfig videosDaoConfig;
    private final DaoConfig empDianpuDaoConfig;

    private final XixunObjDao xixunObjDao;
    private final RecordDao recordDao;
    private final VideosDao videosDao;
    private final EmpDianpuDao empDianpuDao;

    private final DaoConfig managerInfoDaoConfig;

    private final ManagerInfoDao managerInfoDao;

    private final DaoConfig adObjDaoConfig;

    private final AdObjDao adObjDao;

    private final DaoConfig minePicObjDaoConfig;

    private final MinePicObjDao minePicObjDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        empDaoConfig = daoConfigMap.get(EmpDao.class).clone();
        empDaoConfig.initIdentityScope(type);

        empDao = new EmpDao(empDaoConfig, this);

        registerDao(Emp.class, empDao);

        xixunObjDaoConfig = daoConfigMap.get(XixunObjDao.class).clone();
        xixunObjDaoConfig.initIdentityScope(type);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        videosDaoConfig = daoConfigMap.get(VideosDao.class).clone();
        videosDaoConfig.initIdentityScope(type);

        empDianpuDaoConfig = daoConfigMap.get(EmpDianpuDao.class).clone();
        empDianpuDaoConfig.initIdentityScope(type);

        xixunObjDao = new XixunObjDao(xixunObjDaoConfig, this);
        recordDao = new RecordDao(recordDaoConfig, this);
        videosDao = new VideosDao(videosDaoConfig, this);
        empDianpuDao = new EmpDianpuDao(empDianpuDaoConfig, this);

        registerDao(XixunObj.class, xixunObjDao);
        registerDao(Record.class, recordDao);
        registerDao(Videos.class, videosDao);
        registerDao(EmpDianpu.class, empDianpuDao);

        managerInfoDaoConfig = daoConfigMap.get(ManagerInfoDao.class).clone();
        managerInfoDaoConfig.initIdentityScope(type);

        managerInfoDao = new ManagerInfoDao(managerInfoDaoConfig, this);

        registerDao(ManagerInfo.class, managerInfoDao);

        adObjDaoConfig = daoConfigMap.get(AdObjDao.class).clone();
        adObjDaoConfig.initIdentityScope(type);

        adObjDao = new AdObjDao(adObjDaoConfig, this);

        registerDao(AdObj.class, adObjDao);

        minePicObjDaoConfig = daoConfigMap.get(MinePicObjDao.class).clone();
        minePicObjDaoConfig.initIdentityScope(type);

        minePicObjDao = new MinePicObjDao(minePicObjDaoConfig, this);

        registerDao(MinePicObj.class, minePicObjDao);
    }
    
    public void clear() {
        empDaoConfig.getIdentityScope().clear();

        xixunObjDaoConfig.getIdentityScope().clear();
        recordDaoConfig.getIdentityScope().clear();
        videosDaoConfig.getIdentityScope().clear();
        empDianpuDaoConfig.getIdentityScope().clear();
        managerInfoDaoConfig.getIdentityScope().clear();
        adObjDaoConfig.getIdentityScope().clear();
        minePicObjDaoConfig.getIdentityScope().clear();
    }

    public EmpDao getEmpDao() {
        return empDao;
    }

    public XixunObjDao getXixunObjDao() {
        return xixunObjDao;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public VideosDao getVideosDao() {
        return videosDao;
    }

    public EmpDianpuDao getEmpDianpuDao() {
        return empDianpuDao;
    }

    public ManagerInfoDao getManagerInfoDao() {
        return managerInfoDao;
    }

    public AdObjDao getAdObjDao() {
        return adObjDao;
    }

    public MinePicObjDao getMinePicObjDao() {
        return minePicObjDao;
    }

}
