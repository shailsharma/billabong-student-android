package in.securelearning.lil.android.resources.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.courses.views.fragment.CourseListFragment;
import in.securelearning.lil.android.resources.adapter.RecommendedAdapter;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.resources.view.fragment.FavouriteListFragment;
import in.securelearning.lil.android.resources.view.fragment.RecommendResourceFragment2;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;

/**
 * Created by Secure on 12-06-2017.
 */

public interface ResourcesBaseComponent extends BaseComponent {
    void inject(ResourcesMapModel youtubeMapModel);

    void inject(RecommendedListFragment firstFragment);

    void inject(RecommendedAdapter recycleAdapter);

    void inject(FavouriteListFragment favouriteListFragment);

//    void inject(FavouriteAdapter favouriteAdapter);

    void inject(CourseListFragment courseListFragment);

    void inject(RecommendResourceFragment2 recommendResourceFragment2);

    void inject(YoutubePlayActivity.VideoFragment videoFragment);

    void inject(YoutubePlayActivity youtubePlayActivity);

    void inject(FavouriteListFragment.FavouriteAdapter favouriteAdapter);
}
