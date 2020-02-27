package in.securelearning.lil.android.lrpa.events;

public class FetchSubjectDetailEvent {

    private String mId;
    private String mChapterTitle;
    private String mChapterStatus;

    public FetchSubjectDetailEvent(String id, String chapterTitle, String chapterStatus) {
        this.mId = id;
        this.mChapterTitle = chapterTitle;
        this.mChapterStatus = chapterStatus;
    }

    public String getId() {
        return mId;
    }

    public String getChapterTitle() {
        return mChapterTitle;
    }

    public String getChapterStatus() {
        return mChapterStatus;
    }
}
