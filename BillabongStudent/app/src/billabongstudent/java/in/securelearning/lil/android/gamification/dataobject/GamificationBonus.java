package in.securelearning.lil.android.gamification.dataobject;

import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.dataobjects.TopicSuper;
import in.securelearning.lil.android.syncadapter.dataobject.UserMinimal;

public class GamificationBonus {

    private int mBonusId;
    private UserMinimal userObject;
    private SubjectSuper mSubjectObject;
    private TopicSuper mTopicSuper;
    private Object mHomeWorkObject;
    // where we have to update the bonus login , subject
    private String  activityToUpdate ;
    private double  multiplier ;

    // need to confirm in sec or min
    private long expireIn;
    private String expireInDate;
    private long mTimerStartValue;

    public int getBonusId() {
        return mBonusId;
    }

    public void setBonusId(int bonusId) {
        mBonusId = bonusId;
    }

    public UserMinimal getUserObject() {
        return userObject;
    }

    public void setUserObject(UserMinimal userObject) {
        this.userObject = userObject;
    }

    public SubjectSuper getSubjectObject() {
        return mSubjectObject;
    }

    public void setSubjectObject(SubjectSuper subjectObject) {
        mSubjectObject = subjectObject;
    }

    public Object getHomeWorkObject() {
        return mHomeWorkObject;
    }

    public void setHomeWorkObject(Object homeWorkObject) {
        mHomeWorkObject = homeWorkObject;
    }

    public String getActivityToUpdate() {
        return activityToUpdate;
    }

    public void setActivityToUpdate(String activityToUpdate) {
        this.activityToUpdate = activityToUpdate;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public TopicSuper getTopicSuper() {
        return mTopicSuper;
    }

    public void setTopicSuper(TopicSuper topicSuper) {
        mTopicSuper = topicSuper;
    }

    public long getTimerStartValue() {
        return mTimerStartValue;
    }

    public void setTimerStartValue(long timerStartValue) {
        this.mTimerStartValue = timerStartValue;
    }

    public String getExpireInDate() {
        return expireInDate;
    }

    public void setExpireInDate(String expireInDate) {
        this.expireInDate = expireInDate;
    }
}