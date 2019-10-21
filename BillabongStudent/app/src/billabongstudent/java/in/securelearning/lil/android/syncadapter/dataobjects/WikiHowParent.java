package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WikiHowParent implements Serializable {

    @SerializedName("app")
    @Expose
    private WikiHow mWikiHow;

    public WikiHow getWikiHow() {
        return mWikiHow;
    }
}
