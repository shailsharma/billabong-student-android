package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Subject;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadSubjectListEvent {
    private final ArrayList<Subject> mSubjects;

    public LoadSubjectListEvent(ArrayList<Subject> subjects) {
        this.mSubjects = subjects;
    }

    public ArrayList<Subject> getSubjectList() {
        return mSubjects;
    }
}
