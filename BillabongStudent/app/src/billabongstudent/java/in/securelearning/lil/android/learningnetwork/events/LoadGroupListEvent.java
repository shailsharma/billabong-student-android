package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;
import in.securelearning.lil.android.base.dataobjects.Group;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadGroupListEvent {
    private final ArrayList<Group> mGroups;

    public LoadGroupListEvent(ArrayList<Group> groups) {
        this.mGroups = groups;
    }

    public ArrayList<Group> getGropList() {
        return mGroups;
    }
}
