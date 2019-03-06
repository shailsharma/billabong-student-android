package in.securelearning.lil.android.blog.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.blog.model.BlogListModel;
import in.securelearning.lil.android.blog.views.fragment.BlogFragment;


/**
 * Created by Pushkar Raj on 02-12-2016.
 */

public interface BlogBaseComponent extends BaseComponent {
    //    void inject(LoginActivity activity);
    void inject(BlogFragment activity);

    void inject(BlogListModel object);

}
