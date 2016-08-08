package com.Lbins.GuirenApp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.Lbins.GuirenApp.module.Emp;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by liuzwei on 2015/3/13.
 */
public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;
    private static DaoMaster.DevOpenHelper helper;
    private EmpDao testDao;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;

    private DBHelper() {
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }
            helper = new DaoMaster.DevOpenHelper(context, "guiren_hm_db_t", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            instance.testDao = daoMaster.newSession().getEmpDao();
        }
        return instance;
    }

    /**
     * 插入数据
     *
     * @param test
     */
    public void addShoppingToTable(Emp test) {
        testDao.insert(test);
    }

    //查询是否存在该商品
    public boolean isSaved(String ID) {
        QueryBuilder<Emp> qb = testDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(ID));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    //批量插入数据
    public void saveTestList(List<Emp> tests) {
        testDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询所有的购物车
     *
     * @return
     */
    public List<Emp> getShoppingList() {
        return testDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @param test
     * @return
     */
    public long saveShopping(Emp test) {
        return testDao.insertOrReplace(test);
    }

    /**
     * 更新数据
     *
     * @param test
     */
    public void updateTest(Emp test) {
        testDao.update(test);
    }

//    /**
//     * 获得所有收藏的题
//     * @return
//     */
//    public List<ShoppingCart> getFavourTest(){
//        QueryBuilder qb = testDao.queryBuilder();
//        qb.where(ShoppingCartDao.Properties.IsFavor.eq(true));
//        return qb.list();
//    }

    /**
     * 删除所有数据--购物车
     */

    public void deleteShopping() {
        testDao.deleteAll();
    }

    /**
     * 删除数据根据goods_id
     */

    public void deleteShoppingByGoodsId(String cartid) {
        QueryBuilder qb = testDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(cartid));
        testDao.deleteByKey(cartid);//删除
    }


    //动态
    //批量插入数据
    public void saveRecordList(List<Emp> tests) {
        testDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询动态列表
     *
     * @return
     */
    public List<Emp> getRecordList() {
        return testDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @param test
     * @return
     */
    public long saveRecord(Emp test) {
        return testDao.insertOrReplace(test);
    }

    //查询是否存在该动态
    public boolean isRecord(String id) {
        QueryBuilder<Emp> qb = testDao.queryBuilder();
        qb.where(EmpDao.Properties.Hxusername.eq(id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    //查询动态
    public Emp getRecord(String id) {
        Emp recordMsg = testDao.load(id);
        return recordMsg;
    }

    /**
     * 更新数据
     *
     * @param test
     */
    public void updateRecord(Emp test) {
        testDao.update(test);
    }
}
