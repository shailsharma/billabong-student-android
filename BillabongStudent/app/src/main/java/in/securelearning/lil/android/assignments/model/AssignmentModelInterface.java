package in.securelearning.lil.android.assignments.model;

/**
 * Created by Pushkar Raj on 5/31/2016.
 */
public interface AssignmentModelInterface {


    void getAssignmentResponseListForStudent(String assignmentResStage, final Object callingFragment);

    void getAssignmentResponseForStudent(String documentId, Object callingFrom);


       void getFilterPendingAssignmentListByAttribute(String filterAttribute, Object callingFragment);
    void getFilterAssignmentListByAttribute(String filterAttribute, final Object callingFragment, String assignmentStage);

    void getSortAssignmentListByAttribute(String filterAttribute, final Object callingFragment);


}
