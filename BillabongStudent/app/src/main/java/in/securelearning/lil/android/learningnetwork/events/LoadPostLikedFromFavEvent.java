package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.PostDataDetail;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadPostLikedFromFavEvent {
    private final PostDataDetail mPostDataDetail;

    public LoadPostLikedFromFavEvent(PostDataDetail mPostDataDetail) {
        this.mPostDataDetail = mPostDataDetail;
    }

    public PostDataDetail getPostDataDetail() {
        return mPostDataDetail;
    }

}
