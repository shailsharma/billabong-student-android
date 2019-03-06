package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Topic;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadTopicListEvent {
    private final ArrayList<Topic> mTopics;

    public LoadTopicListEvent(ArrayList<Topic> topics) {
        this.mTopics = topics;
    }

    public ArrayList<Topic> getTopicList() {
        return mTopics;
    }
}
