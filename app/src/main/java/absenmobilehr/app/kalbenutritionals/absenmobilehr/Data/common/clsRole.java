package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ASUS on 11/10/2018.
 */
@DatabaseTable
public class clsRole {
    @DatabaseField(id = true)
    int intRoleID;
    @DatabaseField
    String txtRoleName;

    public int getIntRoleID() {
        return intRoleID;
    }

    public void setIntRoleID(int intRoleID) {
        this.intRoleID = intRoleID;
    }

    public String getTxtRoleName() {
        return txtRoleName;
    }

    public void setTxtRoleName(String txtRoleName) {
        this.txtRoleName = txtRoleName;
    }
}
