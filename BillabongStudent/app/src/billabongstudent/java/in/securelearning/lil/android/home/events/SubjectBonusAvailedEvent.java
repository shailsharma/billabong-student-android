package in.securelearning.lil.android.home.events;

/*When user click on subject and availed bonus then this event would refresh subject list on dashboard            */
public class SubjectBonusAvailedEvent {

    private String mSubjectId;

    public SubjectBonusAvailedEvent(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }
}
