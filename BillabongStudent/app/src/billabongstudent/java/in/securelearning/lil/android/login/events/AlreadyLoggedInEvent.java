package in.securelearning.lil.android.login.events;

public class AlreadyLoggedInEvent {
    private boolean mIsAlreadyLoggedIn;

    public AlreadyLoggedInEvent(boolean isAlreadyLoggedIn) {
        this.mIsAlreadyLoggedIn = isAlreadyLoggedIn;
    }

    public boolean isAlreadyLoggedIn() {
        return mIsAlreadyLoggedIn;
    }
}
