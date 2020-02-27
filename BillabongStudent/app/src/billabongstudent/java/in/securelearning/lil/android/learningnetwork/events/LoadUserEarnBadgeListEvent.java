package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.LILBadges;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadUserEarnBadgeListEvent {
    private List<LILBadges> lilBadges = new ArrayList<>();

    public LoadUserEarnBadgeListEvent(ArrayList<LILBadges> lilBadges) {
        this.lilBadges = lilBadges;
    }

    public List<LILBadges> getLilBadges() {
        return lilBadges;
    }
}
