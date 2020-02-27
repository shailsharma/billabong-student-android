package in.securelearning.lil.android.lrpa.events;

/**
 * Created by Rajat Jain on 17/12/19.
 */
public class RefreshLRPAAccordingToType {

    private String mLRPAType;

    public RefreshLRPAAccordingToType(String LRPAType) {
        mLRPAType = LRPAType;
    }

    public String getLRPAType() {
        return mLRPAType;
    }
}
