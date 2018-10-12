package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Robert on 9/4/2018.
 */
@DatabaseTable
public class clsActivityUser implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String intProgramAuditId;
    @DatabaseField
    private String intProgramAuditDetailId;
    @DatabaseField
    private String txtCategory;
    @DatabaseField
    private String Activity;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dtStart;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dtEnd;
    @DatabaseField
    private int intFlag;
    @DatabaseField
    private String txtFlag;
    @DatabaseField
    private String txtNotes;
    @DatabaseField
    private int intStatus;
    @DatabaseField
    private String txtStatus;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dtVerified;

    public int getId() {
        return id;
    }

    public String getIntProgramAuditId() {
        return intProgramAuditId;
    }

    public void setIntProgramAuditId(String intProgramAuditId) {
        this.intProgramAuditId = intProgramAuditId;
    }

    public String getIntProgramAuditDetailId() {
        return intProgramAuditDetailId;
    }

    public void setIntProgramAuditDetailId(String intProgramAuditDetailId) {
        this.intProgramAuditDetailId = intProgramAuditDetailId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxtCategory() {
        return txtCategory;
    }

    public void setTxtCategory(String txtCategory) {
        this.txtCategory = txtCategory;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtEnd() {
        return dtEnd;
    }

    public int getIntFlag() {
        return intFlag;
    }

    public void setIntFlag(int intFlag) {
        this.intFlag = intFlag;
    }

    public String getTxtFlag() {
        return txtFlag;
    }

    public void setTxtFlag(String txtFlag) {
        this.txtFlag = txtFlag;
    }

    public int getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public String getTxtStatus() {
        return txtStatus;
    }

    public String getTxtNotes() {
        return txtNotes;
    }

    public void setTxtNotes(String txtNotes) {
        this.txtNotes = txtNotes;
    }

    public void setTxtStatus(String txtStatus) {
        this.txtStatus = txtStatus;
    }

    public void setDtEnd(Date dtEnd) {
        this.dtEnd = dtEnd;
    }

    public Date getDtVerified() {
        return dtVerified;
    }

    public void setDtVerified(Date dtVerified) {
        this.dtVerified = dtVerified;
    }
}
