package in.securelearning.lil.android.home.events;

public class RefreshEvent {
    String mType;

    public RefreshEvent(String type) {
        this.mType = type;
    }

    public String getType() {
        return mType;
    }
}
