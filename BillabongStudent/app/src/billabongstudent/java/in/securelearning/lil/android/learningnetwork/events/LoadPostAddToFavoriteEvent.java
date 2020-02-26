package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadPostAddToFavoriteEvent {
    private final PostDataDetail mPostDataDetail;
    private final boolean isAddedToFav;

    public LoadPostAddToFavoriteEvent(PostDataDetail mPostDataDetail, boolean isAddedToFav) {
        this.mPostDataDetail = mPostDataDetail;
        this.isAddedToFav = isAddedToFav;
    }

    public PostDataDetail getPostDataDetail() {
        return mPostDataDetail;
    }

    public boolean isAddedToFav() {
        return isAddedToFav;
    }
}
