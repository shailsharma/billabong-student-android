package in.securelearning.lil.android.quizcreator;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.quizcreator.di.component.DaggerQuizCreatorAppComponent;
import in.securelearning.lil.android.quizcreator.di.component.QuizCreatorBaseComponent;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 03-06-2016.
 */
public class InjectorQuizCreator {

    public static final InjectorQuizCreator INSTANCE=new InjectorQuizCreator();

    protected QuizCreatorBaseComponent mComponent;

    public QuizCreatorBaseComponent getComponent() {
        return mComponent;
    }

    public void setComponent(QuizCreatorBaseComponent component) {
        mComponent = component;
    }

    public static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerQuizCreatorAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();

        }
    }

}
