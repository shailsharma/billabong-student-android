package in.securelearning.lil.android.player.events;

public class SpeakCompletedEvent {
    private final boolean mIsSpeakCompleted;

    public SpeakCompletedEvent(boolean isSpeakCompleted) {
        this.mIsSpeakCompleted = isSpeakCompleted;
    }

    public boolean isSpeakCompleted() {
        return mIsSpeakCompleted;
    }
}
