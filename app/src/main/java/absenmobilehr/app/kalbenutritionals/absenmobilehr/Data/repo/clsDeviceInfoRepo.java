package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.DatabaseHelper;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.DatabaseManager;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsDeviceInfo;

/**
 * Created by Robert on 19/06/2017.
 */

public class clsDeviceInfoRepo implements crud {
    DatabaseHelper helper;
    public clsDeviceInfoRepo(Context context){
        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) throws SQLException{
        int index = -1;
        clsDeviceInfo object = (clsDeviceInfo) item;
        try {
            index = helper.getDeviceInfoDao().create(object);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int createOrUpdate(Object item) throws SQLException {
        int index = -1;
        clsDeviceInfo object = (clsDeviceInfo) item;
        try {
            Dao.CreateOrUpdateStatus status = helper.getDeviceInfoDao().createOrUpdate(object);
            index = status.getNumLinesChanged();
//            index = 1;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int update(Object item) throws SQLException {
        int index = -1;
        clsDeviceInfo object = (clsDeviceInfo) item;
        try {
            index = helper.getDeviceInfoDao().update(object);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int delete(Object item) throws SQLException{
        int index = -1;
        clsDeviceInfo object = (clsDeviceInfo) item;
        try {
            index = helper.getDeviceInfoDao().delete(object);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public Object findById(int id) throws SQLException{
        clsDeviceInfo item = null;
        try{
            item = helper.getDeviceInfoDao().queryForId(id);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<?> findAll() throws SQLException{
        List<clsDeviceInfo> items = null;
        try{
            items = helper.getDeviceInfoDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return items;
    }
    public clsDeviceInfo getDataDevice(Context context){
        clsDeviceInfoRepo repo = new clsDeviceInfoRepo(context);
        clsDeviceInfo dataLogin =new clsDeviceInfo();
//        if(repo.CheckLoginNow()){
        List<clsDeviceInfo> listData= null;
        try {
            listData = (List<clsDeviceInfo>) repo.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (clsDeviceInfo data : listData){
                dataLogin = data;
        }

//        }
        return dataLogin;
    }
    public void clearTable() throws SQLException{
        TableUtils.clearTable(helper.getConnectionSource(), clsDeviceInfo.class);
    }
}
