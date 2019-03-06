package in.securelearning.lil.android.blog.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.blog.di.module.BlogModule;


/**
 * Created by Pushkar Raj on 02-12-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class , modules = BlogModule.class)
public interface BlogAppComponent extends BlogBaseComponent {


}
