package in.securelearning.lil.android.syncadapter.dataobjects;

public class ChapterHeaderData {

    public static final String HEADER_IN_PROGRESS = "IN PROGRESS";
    public static final String HEADER_YET_TO_START = "YET TO START";
    public static final String HEADER_COMPLETED = "COMPLETED";

    private int mPosition;

    private String mHeaderTitle;

    public ChapterHeaderData(int position, String headerTitle) {
        this.mPosition = position;
        this.mHeaderTitle = headerTitle;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int index) {
        this.mPosition = index;
    }

    public String getHeaderTitle() {
        return mHeaderTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        mHeaderTitle = headerTitle;
    }
}
