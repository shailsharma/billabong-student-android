package in.securelearning.lil.android.homework.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/* pojo class map with which student submit the homework nad which not*/
public class HomeworkDetail implements Serializable {

    @SerializedName("assignmentDetail")
    @Expose
    private Homework mHomeworkDetail;


    public Homework getHomeworkDetail() {
        return mHomeworkDetail;
    }

}
