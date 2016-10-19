package com.Lbins.GuirenApp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.Lbins.GuirenApp.module.*;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by liuzwei on 2015/3/13.
 */
public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;
    private static DaoMaster.DevOpenHelper helper;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;

    private EmpDao empDao;
    private EmpDianpuDao empDianpuDao;
    private RecordDao recordDao;
    private VideosDao videosDao;
    private XixunObjDao xixunObjDao;
    private ManagerInfoDao managerInfoDao;
    private AdObjDao adObjDao;
    private MinePicObjDao minePicObjDao;

    private DBHelper() {
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }
            helper = new DaoMaster.DevOpenHelper(context, "guiren_hm_db_t_002", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            instance.empDao = daoMaster.newSession().getEmpDao();
            instance.empDianpuDao = daoMaster.newSession().getEmpDianpuDao();
            instance.recordDao = daoMaster.newSession().getRecordDao();
            instance.videosDao = daoMaster.newSession().getVideosDao();
            instance.xixunObjDao = daoMaster.newSession().getXixunObjDao();
            instance.managerInfoDao = daoMaster.newSession().getManagerInfoDao();
            instance.adObjDao = daoMaster.newSession().getAdObjDao();
            instance.minePicObjDao = daoMaster.newSession().getMinePicObjDao();
        }
        return instance;
    }

    /**
     * 插入会员信息
     *
     * @param test
     */
    public void addEmp(Emp test) {
        empDao.insert(test);
    }

    //查询是否存在该会员信息
    public boolean isSaved(String ID) {
        QueryBuilder<Emp> qb = empDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(ID));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }


    /**
     * 更新数据
     *
     * @param emp
     */
    public void updateEmp(Emp emp) {
        empDao.update(emp);
    }

    /**
     * 删除所有数据--会员信息
     */

    public void deleteAllEmp() {
        empDao.deleteAll();
    }

    /**
     * 删除数据根据
     */

    public void deleteEmpByHxusername(String hxusername) {
        QueryBuilder qb = empDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(hxusername));
        empDao.deleteByKey(hxusername);//删除
    }


    //动态
    //批量插入数据
    public void saveEmpList(List<Emp> tests) {
        empDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询动态列表
     *
     * @return
     */
    public List<Emp> getEmpList() {
        return empDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveEmp(Emp emp) {
        return empDao.insertOrReplace(emp);
    }

    //查询是否存在该动态
    public boolean isEmpSave(String id) {
        QueryBuilder<Emp> qb = empDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    //查询会员信息
    public Emp getEmpById(String id) {
        Emp emp = empDao.load(id);
        return emp;
    }

    public Emp getEmpByEmpId(String id) {
        QueryBuilder qb = empDao.queryBuilder();
        qb.where(EmpDao.Properties.Mm_emp_id.eq(id));
        List<Emp> emps = qb.list();
        if(emps != null && emps.size()>0){
            return emps.get(0);
        }else {
            return null;
        }
    }

    //查询喜讯是否存在
    public XixunObj getXixunObjById(String id) {
        XixunObj xixunObj = xixunObjDao.load(id);
        return xixunObj;
    }


    //查询图片是否存在
    public MinePicObj getMinePicObjById(String id) {
        MinePicObj minePicObj = minePicObjDao.load(id);
        return minePicObj;
    }


    /**
     * 查询图片列表
     *
     * @return
     */
    public List<MinePicObj> getPicsListByEmpId(String id) {
        QueryBuilder qb = minePicObjDao.queryBuilder();
        qb.where(MinePicObjDao.Properties.Mm_emp_id.eq(id));
        List<MinePicObj> emps = qb.list();
        return emps;
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveXixunObj(XixunObj xixunObj) {
        return xixunObjDao.insertOrReplace(xixunObj);
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveMinePicObj(MinePicObj minePicObj) {
        return minePicObjDao.insertOrReplace(minePicObj);
    }


    /**
     * 查询喜讯列表
     *
     * @return
     */
    public List<XixunObj> getXixunList() {
        return xixunObjDao.loadAll();
    }

    /**
     * 查询动态
     *
     * @return
     */
    public List<Record> getRecordList() {
        return recordDao.loadAll();
    }

    //查询动态是否存在
    public Record getRecordById(String id) {
        Record record = recordDao.load(id);
        return record;
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveRecord(Record record) {
        return recordDao.insertOrReplace(record);
    }

    /**
     * 查询电影
     *
     * @return
     */
    public List<Videos> getVideos() {
        return videosDao.loadAll();
    }

    //查询videos是否存在
    public Videos getVideosById(String id) {
        Videos videos = videosDao.load(id);
        return videos;
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveVideos(Videos videos) {
        return videosDao.insertOrReplace(videos);
    }


    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveDianpu(EmpDianpu empDianpu) {
        return empDianpuDao.insertOrReplace(empDianpu);
    }

    //查询店铺是否存在
    public EmpDianpu getEmpDianpuById(String id) {
        EmpDianpu empDianpu = empDianpuDao.load(id);
        return empDianpu;
    }

    /**
     * 查询店铺
     *
     * @return
     */
    public List<EmpDianpu> getDianpus() {
        return empDianpuDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveManagerInfo(ManagerInfo managerInfo) {
        return managerInfoDao.insertOrReplace(managerInfo);
    }

    //查询店铺是否存在
    public ManagerInfo getManagerInfoById(String id) {
        ManagerInfo managerInfo = managerInfoDao.load(id);
        return managerInfo;
    }

    public ManagerInfo getManagerInfoByEmpId(String id) {
        QueryBuilder qb = managerInfoDao.queryBuilder();
        qb.where(ManagerInfoDao.Properties.Emp_id.eq(id));
        List<ManagerInfo> emps = qb.list();
        if(emps != null && emps.size()>0){
            return emps.get(0);
        }else {
            return null;
        }
    }

    /**
     * 查询动态
     *
     * @return
     */
    public List<Record> getRecordListByEmpId(String id) {
        QueryBuilder qb = recordDao.queryBuilder();
        qb.where(RecordDao.Properties.Mm_emp_id.eq(id));
        List<Record> records = qb.list();
        return records;
    }


    /**
     * 查询广告
     *
     * @return
     */
    public List<AdObj> getAdObjs() {
        return adObjDao.loadAll();
    }

    //查询广告是否存在
    public AdObj getAdObjById(String id) {
        AdObj videos = adObjDao.load(id);
        return videos;
    }

    /**
     * 插入或是更新数据
     *
     * @return
     */
    public long saveAdObj(AdObj adObj) {
        return adObjDao.insertOrReplace(adObj);
    }
}
