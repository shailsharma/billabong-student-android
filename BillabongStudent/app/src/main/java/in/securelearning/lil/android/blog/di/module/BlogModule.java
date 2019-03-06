package in.securelearning.lil.android.blog.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.blog.model.BlogListModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Created by Pushkar Raj on 02-12-2016.
 */
@Module
public class BlogModule {

    @Provides
    @ActivityScope
    public BlogListModel blogListModel() {
        return new BlogListModel();
    }

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }
}
