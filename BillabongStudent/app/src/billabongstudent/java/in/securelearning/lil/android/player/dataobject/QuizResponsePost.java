package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.QuestionResponse;

public class QuizResponsePost implements Serializable {

    @SerializedName("cardId")
    @Expose
    private String mCardId;

    @SerializedName("courseId")
    @Expose
    private String mCourseId;

    @SerializedName("quizId")
    @Expose
    private String mQuizId;

    @SerializedName("sectionId")
    @Expose
    private String mSectionId;

    @SerializedName("type")
    @Expose
    private String mType;

    @SerializedName("quizResponses")
    @Expose
    private ArrayList<QuestionResponse> mQuestionResponseList;

    @SerializedName("courseDetail")
    @Expose
    private ModuleDetail mModuleDetail;

    public String getCardId() {
        return mCardId;
    }

    public void setCardId(String cardId) {
        mCardId = cardId;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getQuizId() {
        return mQuizId;
    }

    public void setQuizId(String quizId) {
        mQuizId = quizId;
    }

    public String getSectionId() {
        return mSectionId;
    }

    public void setSectionId(String sectionId) {
        mSectionId = sectionId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public ArrayList<QuestionResponse> getQuestionResponseList() {
        return mQuestionResponseList;
    }

    public void setQuestionResponseList(ArrayList<QuestionResponse> questionResponseList) {
        mQuestionResponseList = questionResponseList;
    }

    public ModuleDetail getModuleDetail() {
        return mModuleDetail;
    }

    public void setModuleDetail(ModuleDetail moduleDetail) {
        mModuleDetail = moduleDetail;
    }
}
