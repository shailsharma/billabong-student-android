package in.securelearning.lil.android.quizcreator.events;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Chaitendra 27/7/2017.
 */
public class QuizMinimalDeleteEvent {
    private final String mAlias;

    public QuizMinimalDeleteEvent(String s) {
        this.mAlias = s;
    }

    public String getAlias() {
        return mAlias;
    }
}
