package in.securelearning.lil.android.player.events;

public class TTSInitialiseDoneEvent {
    private final boolean mIsTTSInitCompleted;

    public TTSInitialiseDoneEvent(boolean isTTsInitCompleted) {
        this.mIsTTSInitCompleted = isTTsInitCompleted;
    }

    public boolean isTTsInitCompleted() {
        return mIsTTSInitCompleted;
    }
}
