package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajat Jain on 14/10/19.
 */
public class HelpAndFaqCategory implements Serializable {

    @SerializedName("category")
    @Expose
    private String mCategory;

    @SerializedName("data")
    @Expose
    private ArrayList<HelpAndFaqModule> mHelpAndFaqModuleList;

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public ArrayList<HelpAndFaqModule> getHelpAndFaqModuleList() {
        return mHelpAndFaqModuleList;
    }

    public void setHelpAndFaqModuleList(ArrayList<HelpAndFaqModule> helpAndFaqModuleList) {
        mHelpAndFaqModuleList = helpAndFaqModuleList;
    }
}
