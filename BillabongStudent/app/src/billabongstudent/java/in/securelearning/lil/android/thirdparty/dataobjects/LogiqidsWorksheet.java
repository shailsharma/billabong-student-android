package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsWorksheet implements Serializable {

    @SerializedName("id")
    @Expose
    private int mWorksheetId;

    @SerializedName("status_id")
    @Expose
    private int mStatusId;

    @SerializedName("title")
    @Expose
    private String mWorksheetTitle;

    public int getWorksheetId() {
        return mWorksheetId;
    }

    public void setWorksheetId(int worksheetId) {
        mWorksheetId = worksheetId;
    }

    public int getStatusId() {
        return mStatusId;
    }

    public void setStatusId(int statusId) {
        mStatusId = statusId;
    }

    public String getWorksheetTitle() {
        return mWorksheetTitle;
    }

    public void setWorksheetTitle(String worksheetTitle) {
        mWorksheetTitle = worksheetTitle;
    }
}
