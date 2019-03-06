package in.securelearning.lil.android.tracking.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.tracking.model.TrackingMapModel;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForStudent;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForTeacher;
import in.securelearning.lil.android.tracking.view.fragment.DialogFragment;

/**
 * Created by Secure on 26-04-2017.
 */

public interface TrackingBaseComponent extends BaseComponent {

    void inject(TrackingMapModel object);

    void inject(TrackingActivityForStudent trackingActivityForStudent);

    void inject(TrackingActivityForTeacher trackingActivityForTeacher);

    void inject(DialogFragment dialogFragment);
}
