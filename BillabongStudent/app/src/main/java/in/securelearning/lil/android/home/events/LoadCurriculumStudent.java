package in.securelearning.lil.android.home.events;

import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.WeeklySchedule;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 7/7/2016.
 */
public class LoadCurriculumStudent {
    public Curriculum getCurriculum() {
        return mCurriculum;
    }

    private final Curriculum mCurriculum;

    public LoadCurriculumStudent(Curriculum curriculum) {
        this.mCurriculum = curriculum;
    }
}
