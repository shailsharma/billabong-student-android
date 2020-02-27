package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadUpdatePostCountEvent {
    private final int count;

    public LoadUpdatePostCountEvent(int count) {
        this.count=count;
    }

    public int getCount() {
        return count;
    }

}
