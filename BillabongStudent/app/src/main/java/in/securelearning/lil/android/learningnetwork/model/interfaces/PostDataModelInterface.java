package in.securelearning.lil.android.learningnetwork.model.interfaces;

import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;

/**
 * Created by Pushkar Raj on 9/6/2016.
 */
public interface PostDataModelInterface {


    int validatePostData(PostData postData);

    int savePostData(PostData postData);

    int savePostResponse(PostResponse postResponse);

    void getPostForCurrentGroupByUid(String uid);

    void getFilterPostByGroupIdNdAttribute(String uid, String filterBy);

    void getFavoritePostByGroupIdNdUserId(String uid, String userId);
}
