package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chaitendra on 13-Jan-18.
 */

public class PrerequisiteCoursesPostData implements Serializable {

    @SerializedName("courseIds")
    @Expose
    private List<String> mCourseIds;

    public List<String> getCourseIds() {
        return mCourseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        mCourseIds = courseIds;
    }
}
