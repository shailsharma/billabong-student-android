package in.securelearning.lil.android.learningnetwork.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostSharedIntentActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.GroupDetailActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostLikeActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostResponseListFragment;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public interface LearningNetworkComponent extends BaseComponent {

    PostDataModel postDataModel();

    void inject(PostDataLearningModel postDataLearningModel);

    void inject(PostListFragment postListFragment);

    void inject(PostListActivity groupPostListActivity);

    void inject(PostResponseListFragment postResponseListFragment);

    void inject(LearningNetworkGroupListFragment learningNetworkGroupListFragment);

    void inject(GroupDetailActivity groupDetailActivity);

    void inject(PostLikeActivity postLikeActivity);

    void inject(CreatePostActivity createPostActivity);

    void inject(CreatePostSharedIntentActivity createPostSharedIntentActivity);
}
