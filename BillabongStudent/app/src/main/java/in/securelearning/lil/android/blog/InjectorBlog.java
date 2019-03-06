package in.securelearning.lil.android.blog;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.blog.di.component.BlogBaseComponent;
import in.securelearning.lil.android.blog.di.component.DaggerBlogAppComponent;


/**
 * Created by Pushkar Raj on 02-12-2016.
 */

public class InjectorBlog {
    public static final InjectorBlog INSTANCE=new InjectorBlog();

    protected BlogBaseComponent mComponent;

    public BlogBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(BlogBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorBlog() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerBlogAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }

    }
}
