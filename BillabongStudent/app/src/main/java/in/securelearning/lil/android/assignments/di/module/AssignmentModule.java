package in.securelearning.lil.android.assignments.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.assignments.model.AssignResourceActivityModel;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.model.QuizAssemblerModel;
import in.securelearning.lil.android.assignments.model.MetaDataScreenModel;
import in.securelearning.lil.android.assignments.model.PendingSummaryTeacherActivityModel;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.MetaDataModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@Module
public class AssignmentModule {


    @Provides
    @ActivityScope
    public AssignmentResponseStudentModel assignmentResponseStudentModel() {
        return new AssignmentResponseStudentModel();
    }

    @Provides
    @ActivityScope
    public QuizAssemblerModel instantQuizMetaDataModel() {
        return new QuizAssemblerModel();
    }

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public AssignmentTeacherModel teacherModel() {
        return new AssignmentTeacherModel();
    }


    @Provides
    @ActivityScope
    public PendingSummaryTeacherActivityModel pendingSummaryTeacherActivityModel() {
        return new PendingSummaryTeacherActivityModel();
    }

    @Provides
    @ActivityScope
    public AssignResourceActivityModel assignResourceActivityModel() {
        return new AssignResourceActivityModel();
    }


    @Provides
    @ActivityScope
    public GroupModel groupModel() {
        return new GroupModel();
    }


    @Provides
    @ActivityScope
    public MetaDataScreenModel metaDataScreenModel() {
        return new MetaDataScreenModel();
    }

    @Provides
    @ActivityScope
    public MetaDataModel metaDataModel() {
        return new MetaDataModel();
    }



}
