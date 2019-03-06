package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Grade;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadGradeListEvent {
    private final ArrayList<Grade> mGrades;

    public LoadGradeListEvent(ArrayList<Grade> grades) {
        this.mGrades = grades;
    }

    public ArrayList<Grade> getGradeList() {
        return mGrades;
    }
}
