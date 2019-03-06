package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Secure on 06-10-2017.
 */

public class LearningLevelResult {

    @SerializedName("id")
    @Expose
    private String mId = "";

    @SerializedName("name")
    @Expose
    private String mName = "";

    @SerializedName("count")
    @Expose
    private int mCount = 0;

    @SerializedName("grades")
    @Expose
    private ArrayList<Grades> mGrades;

    public ArrayList<Grades> getGrades() {
        return mGrades;
    }

    public void setGrades(ArrayList<Grades> grades) {
        mGrades = grades;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = mId;
    }

    public class Grades {
        @SerializedName("name")
        @Expose
        private String mName = "";

        @SerializedName("count")
        @Expose
        private int mCount = 0;

        @SerializedName("id")
        @Expose
        private String mId = "";

        public Grades() {
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public int getCount() {
            return mCount;
        }

        public void setCount(int count) {
            mCount = count;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }
    }
}
