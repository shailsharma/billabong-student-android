package in.securelearning.lil.android.courses.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.courses.models.CoursesModel;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.courses.views.fragment.CourseFavouritesFragment;
import in.securelearning.lil.android.courses.views.fragment.CourseFragmentNew;
import in.securelearning.lil.android.courses.views.fragment.CourseRecommendFragment;
import in.securelearning.lil.android.courses.views.fragment.DemoCourseFragment;
import in.securelearning.lil.android.courses.views.fragment.DownloadCourseFragment;
import in.securelearning.lil.android.courses.views.fragment.FavouriteListCourseFragment;
import in.securelearning.lil.android.courses.views.fragment.RecommendedCourseFragment2;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface CoursesBaseComponent extends BaseComponent {

    void inject(ItemListModel t);

    void inject(CoursesModel t);

    void inject(CourseRecommendFragment t);

    void inject(CourseFavouritesFragment t);

    void inject(DemoCourseFragment t);

    void inject(CourseDetailActivity t);

    void inject(CourseFragmentNew t);

    void inject(DownloadCourseFragment downloadCourseFragment);

    void inject(FavouriteListCourseFragment favouriteListCourseFragment);

    void inject(RecommendedCourseFragment2 recommendedCourseFragment2);
}
