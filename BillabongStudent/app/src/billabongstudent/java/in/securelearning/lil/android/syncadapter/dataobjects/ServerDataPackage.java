package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;


/**
 * Data Package representing the json containing
 * the list of assignments , assignment responses and quizzes.
 */
public class ServerDataPackage extends BaseDataObject {
    @SerializedName("id")
    @Expose(deserialize = true, serialize = false)
    private String mObjectId = "";
    @SerializedName("AssignmentResponses")
    @Expose
    private ArrayList<AssignmentResponse> mAssignmentResponseList = new ArrayList<>();
    @SerializedName("PostDatas")
    @Expose
    private ArrayList<PostData> mLearningNetworkPostDataList = new ArrayList<>();
    @SerializedName("PostResponses")
    @Expose
    private ArrayList<PostResponse> mPostResponsesList = new ArrayList<>();
    @SerializedName("Assignments")
    @Expose
    private ArrayList<Assignment> mAssignmentList = new ArrayList<>();
    @SerializedName("Quizzes")
    @Expose
    private ArrayList<Quiz> mQuizList = new ArrayList<>();


    public ArrayList<Assignment> getAssignmentList() {
        return mAssignmentList;
    }

    /*getters and setters*/
    public void setAssignmentList(ArrayList<Assignment> assignmentList) {
        mAssignmentList = assignmentList;
    }

    public ArrayList<AssignmentResponse> getAssignmentResponseList() {
        return mAssignmentResponseList;
    }

    public ArrayList<PostData> getLearningNetworkPostDataList() {
        return mLearningNetworkPostDataList;
    }

    public void setLearningPostDataList(ArrayList<PostData> learningPostDataList) {
        mLearningNetworkPostDataList = learningPostDataList;
    }
    public void setAssignmentResponseList(ArrayList<AssignmentResponse> assignmentResponseList) {
        mAssignmentResponseList = assignmentResponseList;
    }

    public ArrayList<Quiz> getQuizList() {
        return mQuizList;
    }

    public void setQuizList(ArrayList<Quiz> quizList) {
        mQuizList = quizList;
    }

    /**
     * override the base method to set sync status of all objects within this class
     *
     * @param syncStatus sync status to set
     */
    @Override
    public void setSyncStatus(String syncStatus) {
        super.setSyncStatus(syncStatus);
        ArrayList<Quiz> list1 = this.getQuizList();
        for (int i = 0; i < list1.size(); i++) {
            list1.get(i).setSyncStatus(syncStatus);
        }

        ArrayList<Assignment> list2 = this.getAssignmentList();
        for (int i = 0; i < list2.size(); i++) {
            list2.get(i).setSyncStatus(syncStatus);
        }

        ArrayList<AssignmentResponse> list3 = this.getAssignmentResponseList();
        for (int i = 0; i < list3.size(); i++) {
            list3.get(i).setSyncStatus(syncStatus);
        }

        ArrayList<PostData> list4 = this.getLearningNetworkPostDataList();
        for (int i = 0; i < list4.size(); i++) {
            list4.get(i).setSyncStatus(syncStatus);
        }

        ArrayList<PostResponse> list5 = this.getPostResponsesList();
        for (int i = 0; i < list5.size(); i++) {
            list5.get(i).setSyncStatus(syncStatus);
        }
    }

    public ArrayList<PostResponse> getPostResponsesList() {
        return mPostResponsesList;
    }

    public void setPostResponsesList(ArrayList<PostResponse> mPostResponsesList) {
        this.mPostResponsesList = mPostResponsesList;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(String objectId) {
        this.mObjectId = objectId;
    }

    @Override
    public String thumbnailFileUrl() {
        return null;
    }
}
