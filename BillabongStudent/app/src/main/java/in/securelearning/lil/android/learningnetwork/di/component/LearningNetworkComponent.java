package in.securelearning.lil.android.learningnetwork.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.learningnetwork.adapter.LearningNetworkDetailAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.PostDataBadgeAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.PostResponseBadgeAdapter;
import in.securelearning.lil.android.learningnetwork.model.LILBadgesModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostSharedIntentActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.GroupDetailActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.LearningNetworkDetailActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostLikeActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.SearchPostDetailActivity;
import in.securelearning.lil.android.learningnetwork.views.fragment.BulletinFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.FavoritePostFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.GroupDetailFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.MembersFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.NotificationFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostFragmentForClassDetail;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostFragmentNew;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostResponseListFragment;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public interface LearningNetworkComponent extends BaseComponent {

    PostDataModel postDataModel();

    BadgesModel badgesModel();

    /*inject into models*/
    void inject(PostDataLearningModel postDataLearningModel);

    void inject(LILBadgesModel model);

    /*inject into adapter*/
    void inject(LearningNetworkDetailAdapter learningNetworkDetailAdapter);

    void inject(PostDataBadgeAdapter postDataBadgeAdapter);

    void inject(PostResponseBadgeAdapter adapter);

    /*inject into fragments*/

    void inject(LearningNetworkDetailActivity learningNetworkDetailActivity);

    void inject(PostFragment postFragment);

    void inject(FavoritePostFragment favoritePostFragment);

    void inject(MembersFragment membersFragment);

    void inject(CreatePostActivity createPostActivity);

    void inject(BulletinFragment bulletinFragment);

    void inject(SearchPostDetailActivity searchPostDetailActivity);

    void inject(PostFragmentNew postFragmentNew);

    void inject(PostFragmentForClassDetail postFragmentForClassDetail);

    void inject(PostListFragment postListFragment);

    void inject(PostListActivity groupPostListActivity);

    void inject(PostResponseListFragment postResponseListFragment);

    void inject(LearningNetworkGroupListFragment learningNetworkGroupListFragment);

    void inject(GroupDetailFragment groupDetailFragment);

    void inject(GroupDetailActivity groupDetailActivity);

    void inject(PostLikeActivity postLikeActivity);

    void inject(NotificationFragment notificationFragment);

    void inject(CreatePostSharedIntentActivity createPostSharedIntentActivity);
}
