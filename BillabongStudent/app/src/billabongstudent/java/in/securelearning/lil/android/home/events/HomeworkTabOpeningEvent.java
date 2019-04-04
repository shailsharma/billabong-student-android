package in.securelearning.lil.android.home.events;

public class HomeworkTabOpeningEvent {
    private int mIndex;
    public static final int OVERDUE = 0;
    public static final int DUE = 1;

    public HomeworkTabOpeningEvent(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }
}
