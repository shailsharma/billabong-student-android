package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.PostDataDetail;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadPostListEvent {
    private final ArrayList<PostDataDetail> mPostDatas;

    public LoadPostListEvent(ArrayList<PostDataDetail> postDatas) {
        this.mPostDatas = postDatas;
    }

    public ArrayList<PostDataDetail> getPostDatas() {
        return mPostDatas;
    }
}
