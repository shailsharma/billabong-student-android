package in.securelearning.lil.android.gamification.event;

public class GamificationEventDone {
    private boolean isDone;
    private String eventActivity;
    private String subActivity;
    private String id;

    public GamificationEventDone(String subActivity, String eventActivity, Boolean isDone) {
        this.eventActivity = eventActivity;
        this.subActivity = subActivity;
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getEventActivity() {
        return eventActivity;
    }

    public String getSubActivity() {
        return subActivity;
    }

    public String getId() {
        return id;
    }
}
