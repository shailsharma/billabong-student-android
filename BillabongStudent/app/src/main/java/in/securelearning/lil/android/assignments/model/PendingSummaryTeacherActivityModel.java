package in.securelearning.lil.android.assignments.model;

import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.events.LoadAssignmentResponseListTeacher;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.dataobjects.AssignedGroup;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Model class for Pending Summary Teacher Activity.
 */
public class PendingSummaryTeacherActivityModel {
    @Inject
    Context mContext;

    @Inject
    GroupModel mGroupModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AssignmentResponseModel mAssignmentResponseModel;

    @Inject
    AssignmentModel mAssignmentModel;

    public PendingSummaryTeacherActivityModel() {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    /**
     * asynchronous load of student list for a group
     *
     * @param assignmentDocId document id of the assignment
     * @param group
     */
    public void getStudentList(final String assignmentDocId, final Group group) {
        mAssignmentModel.getAssignment(assignmentDocId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Assignment>() {
                    @Override
                    public void accept(Assignment assignment) {
                        ArrayList<AssignmentResponse> arrayList = mAssignmentResponseModel.getSubmittedAssignmentResponseForMembersSync(group, assignment.getObjectId());
//                        ArrayList<UiElement> arrayList1 = getUiElementListForPendingSummaryView(group, arrayList);
                        mRxBus.send(new LoadAssignmentResponseListTeacher(arrayList));
                    }
                });
    }

    /**
     * asynchronous load of student list for a group
     *
     * @param assignmentDocId document id of the assignment
     * @param groups
     */
    public void getAllStudentList(final String assignmentDocId, final ArrayList<Group> groups) {
        mAssignmentModel.getAssignment(assignmentDocId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Assignment>() {
                    @Override
                    public void accept(Assignment assignment) {
                        ArrayList<AssignmentResponse> list = new ArrayList<AssignmentResponse>();
                        for (Group group :
                                groups) {
                            ArrayList<AssignmentResponse> arrayList = mAssignmentResponseModel.getSubmittedAssignmentResponseForMembersSync(group, assignment.getObjectId());
                            list.addAll(arrayList);
                        }

//                        ArrayList<UiElement> arrayList1 = getUiElementListForPendingSummaryView(group, arrayList);
                        mRxBus.send(new LoadAssignmentResponseListTeacher(list));
                    }
                });
    }

    /**
     * synchronous load of group from document id
     *
     * @param groupDocId document id of the group
     * @return Group
     */
    public Group getGroup(String groupDocId) {
        return mGroupModel.getGroupSync(groupDocId);
    }

    public ArrayList<Group> getGroups(ArrayList<AssignedGroup> assignedGroups) {
        ArrayList<Group> list = new ArrayList<>();
        for (AssignedGroup assignedGroup :
                assignedGroups) {
            list.add(mGroupModel.getGroupFromUidSync(assignedGroup.getId()));
        }
        return list;
    }

    /**
     * synchronous load of assignment from document id
     *
     * @param assignmentDocId document id of the assignment
     * @return Assignment
     */
    public Assignment getAssignment(String assignmentDocId) {
        return mAssignmentModel.getAssignmentSync(assignmentDocId);
    }

    /**
     * save assignment response
     *
     * @param assignmentResponse
     */
    public void saveAssignmentResponse(AssignmentResponse assignmentResponse) {
        mAssignmentResponseModel.saveAssignmentResponse(assignmentResponse);
    }
}
