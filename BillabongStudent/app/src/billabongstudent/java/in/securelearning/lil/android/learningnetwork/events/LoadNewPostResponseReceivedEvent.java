package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.PostResponse;

/**
 * Created by Chaitendra on 18-May-17.
 */

public class LoadNewPostResponseReceivedEvent {

    private final PostResponse mPostResponse;

    public PostResponse getPostResponse() {
        return mPostResponse;
    }

    public LoadNewPostResponseReceivedEvent(PostResponse postResponse) {
        this.mPostResponse = postResponse;

    }
}
