package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.DatabaseHelper;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.DatabaseManager;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsRole;

/**
 * Created by ASUS on 11/10/2018.
 */

public class clsRoleRepo implements crud {
    private DatabaseHelper helper;

    public clsRoleRepo(Context context){
        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }
    @Override
    public int create(Object item) throws SQLException {
        int index = -1;
        clsRole object = (clsRole) item;
        try {
            index = helper.getclsRoleDao().create(object);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int createOrUpdate(Object item) throws SQLException {
        int index = -1;
        clsRole object = (clsRole) item;
        try {
            Dao.CreateOrUpdateStatus status = helper.getclsRoleDao().createOrUpdate(object);
            index = status.getNumLinesChanged();
//            index = 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int update(Object item) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Object item) throws SQLException {
        return 0;
    }

    @Override
    public Object findById(int id) throws SQLException {
        clsRole item = null;
        try{
            item = helper.getclsRoleDao().queryForId(id);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<clsRole> findAll() throws SQLException {
        List<clsRole> items = null;
        try{
            items = helper.getclsRoleDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return items;
    }
}
