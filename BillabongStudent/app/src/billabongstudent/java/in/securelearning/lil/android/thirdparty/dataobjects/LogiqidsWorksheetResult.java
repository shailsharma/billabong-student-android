package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LogiqidsWorksheetResult implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<LogiqidsWorksheet> mWorksheetList;

    public ArrayList<LogiqidsWorksheet> getWorksheetList() {
        return mWorksheetList;
    }

    public void setWorksheetList(ArrayList<LogiqidsWorksheet> worksheetList) {
        mWorksheetList = worksheetList;
    }
}
