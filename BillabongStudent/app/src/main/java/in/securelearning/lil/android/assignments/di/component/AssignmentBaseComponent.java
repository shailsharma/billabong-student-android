package in.securelearning.lil.android.assignments.di.component;

import in.securelearning.lil.android.assignments.model.AssignResourceActivityModel;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.model.MetaDataScreenModel;
import in.securelearning.lil.android.assignments.model.PendingSummaryTeacherActivityModel;
import in.securelearning.lil.android.assignments.model.QuizAssemblerModel;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizAssemblerActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizMetaDataActivity;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentCompletedStudentFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentCompletedTeacherFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentCompletedTraineeFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentCompletedTrainerFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentStudentFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentStudentClassDetails;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentTeacher;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentTeacherForClassDetails;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentTeacherFragment;
import in.securelearning.lil.android.assignments.views.fragment.QuizzesFragment;
import in.securelearning.lil.android.assignments.views.fragment.StudentListFragment;
import in.securelearning.lil.android.assignments.views.fragment.overdueassignments.OverDueAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.submittedassignment.SubmittedAssignmentFragment;
import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.home.views.activity.CalendarAssignmentActivity;
import in.securelearning.lil.android.home.views.fragment.TraineeAssignmentsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainerAssignmentsFragment;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface AssignmentBaseComponent extends BaseComponent {

    void inject(AssignmentResponseStudentModel model);

    void inject(PendingSummaryTeacherActivityModel model);

    void inject(AssignmentTeacherModel model);

    void inject(AssignResourceActivityModel model);

    void inject(PendingAssignmentFragment fragment);

    void inject(StudentSummaryActivity studentSummaryActivity);

    void inject(AssignActivity activity);

    void inject(SubmittedAssignmentFragment fragment);

    void inject(OverDueAssignmentFragment fragment);

    void inject(QuizzesFragment fragment);

    void inject(StudentListFragment fragment);

    void inject(MetaDataScreenModel fragment);

    void inject(AssignmentFragmentTeacher fragment);

    void inject(QuizMetaDataActivity quizMetaDataActivity);

    void inject(AssignmentDetailActivity assignmentDetailActivity);

    void inject(CalendarAssignmentActivity calendarAssignmentActivity);

    void inject(QuizAssemblerModel quizAssemblerModel);

    void inject(QuizAssemblerActivity quizAssemblerActivity);

    void inject(AssignmentStudentFragment assignmentStudentFragment);

    void inject(AssignmentFragmentStudentClassDetails assignmentFragmentStudentClassDetails);

    void inject(AssignmentFragmentTeacherForClassDetails assignmentFragmentTeacherForClassDetails);

    void inject(AssignmentTeacherFragment assignmentTeacherFragment);

    void inject(AssignmentCompletedTeacherFragment assignmentCompletedTeacherFragment);

    void inject(AssignmentCompletedStudentFragment assignmentCompletedStudentFragment);

    void inject(TraineeAssignmentsFragment traineeAssignmentsFragment);

    void inject(TrainerAssignmentsFragment trainerAssignmentsFragment);

    void inject(AssignmentCompletedTrainerFragment assignmentCompletedTrainerFragment);

    void inject(AssignmentCompletedTraineeFragment assignmentCompletedTraineeFragment);
}
