package in.securelearning.lil.android.home.events;

public class AnimateFragmentEvent {

    private int mId;

    public AnimateFragmentEvent(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }
}
