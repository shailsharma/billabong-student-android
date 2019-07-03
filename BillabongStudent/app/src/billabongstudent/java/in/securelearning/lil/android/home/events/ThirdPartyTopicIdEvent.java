package in.securelearning.lil.android.home.events;

public class ThirdPartyTopicIdEvent {
    private String mThirdPartyTopicId;

    public ThirdPartyTopicIdEvent(String thirdPartyTopicId) {
        this.mThirdPartyTopicId = thirdPartyTopicId;
    }

    public String getThirdPartyTopicId() {
        return mThirdPartyTopicId;
    }
}
