package in.securelearning.lil.android.assignments.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.events.LoadOverDueNdNewCountEvent;
import in.securelearning.lil.android.assignments.events.LoadPendingAssignmentResponseListStudent;
import in.securelearning.lil.android.assignments.events.LoadSubmittedAssignmentResponseListStudent;
import in.securelearning.lil.android.assignments.events.LoadTodaysAssignedAssignmentResponseListEvent;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.submittedassignment.SubmittedAssignmentFragment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.SubmittedBy;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT_RESPONSE;

/**
 * Created by Pushkar Raj on 7/5/2016.
 */

public class AssignmentResponseStudentModel implements AssignmentResponseStudentModelInterface {


    @Inject
    RxBus mRxBus;

    @Inject
    AssignmentResponseModel mAssignmentResponseModel;

    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    Context mAppContext;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    public AssignmentResponseStudentModel() {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }


    @Override
    public void getFilterAssignmentListByAttribute(String filterAttribute, final Object callingFragment, String assignmentStage) {
        mAssignmentResponseModel.getFilterAssignmentResponseListByAttribute(filterAttribute, assignmentStage).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())

                .subscribe(new Consumer<ArrayList<AssignmentResponse>>() {

                    @Override
                    public void accept(ArrayList<AssignmentResponse> assignmentResponses) {
                        mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));

//                            mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//                        if (callingFragment instanceof PendingAssignmentFragmentStudent)
//
//                        else if (callingFragment instanceof SubmittedAssignmentFragmentStudent)
//                            mRxBus.send(new LoadSubmittedAssignmentResponseListStudent(assignmentResponses));
//
//                        else if (callingFragment instanceof GradedAssignmentFragmentStudent)
//                            mRxBus.send(new LoadGradedAssignmentResponseListStudent(assignmentResponses));

                    }
                });
    }


    /**
     * @param documentId
     * @param callingFragment
     */
    public void getAssignmentResponseForStudent(String documentId, final Object callingFragment) {
        mAssignmentResponseModel.getAssignmentResponseObservable(documentId).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<AssignmentResponse>() {
            @Override
            public void accept(final AssignmentResponse assignmentResponse) {


                //     mRxBus.send(new LoadPendingAssignmentResponseStudent(assignmentResponse));

//                if (callingFragment instanceof PendingAssignmentFragmentStudent)
//                    mRxBus.send(new LoadPendingAssignmentResponseStudent(assignmentResponse));
//                else if (callingFragment instanceof SubmittedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadSubmittedAssignmentResponseStudent(assignmentResponse));
//                else if (callingFragment instanceof GradedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadGradedAssignmentResponseStudent(assignmentResponse));

            }
        });
    }

    /**
     * @param filterAttribute
     * @param callingFragment
     */
    @Override
    public void getFilterPendingAssignmentListByAttribute(String filterAttribute, final Object callingFragment) {

        mAssignmentResponseModel.getFilterPendingAssignmentResponseListByAttribute(filterAttribute).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<AssignmentResponse>>() {

            @Override
            public void accept(ArrayList<AssignmentResponse> assignmentResponses) {
                mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//                if (callingFragment instanceof PendingAssignmentFragmentStudent)
//                    mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof SubmittedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadSubmittedAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof GradedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadGradedAssignmentResponseListStudent(assignmentResponses));
            }
        });
    }

    /**
     * @param assignmentResStage
     * @param callingFragment
     */

    public void getAssignmentResponseListForStudent(String assignmentResStage, final Object callingFragment) {
        mAssignmentResponseModel.getAssignmentResponseListByStage(assignmentResStage).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
            @Override
            public void accept(final ArrayList<AssignmentResponse> assignmentResponses) {
                mAssignmentResponseModel.getAssignmentResponseListByStage(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
                    @Override
                    public void accept(ArrayList<AssignmentResponse> assignmentResponseList) throws Exception {
                        assignmentResponses.addAll(assignmentResponseList);
                        if (callingFragment instanceof SubmittedAssignmentFragment)
                            mRxBus.send(new LoadSubmittedAssignmentResponseListStudent(assignmentResponses));
                        else if (callingFragment instanceof PendingAssignmentFragment)
                            mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
                    }
                });

            }
        });
    }

    /**
     * @param assignmentResStage
     * @param callingFragment
     */

    public void getALLPendingAssignmentResponseListForStudent(String assignmentResStage, final Object callingFragment) {
        mAssignmentResponseModel.getALLPendingAssignmentResponseListForStudent(assignmentResStage).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
            @Override
            public void accept(final ArrayList<AssignmentResponse> assignmentResponses) {

                mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//                if (callingFragment instanceof PendingAssignmentFragmentStudent)
//                    mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof SubmittedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadSubmittedAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof GradedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadGradedAssignmentResponseListStudent(assignmentResponses));
            }

        });
    }


    public void getTodaysAssignedListForStudent(String assignmentResStage, final Object callingFragment) {
        mAssignmentResponseModel.getALLPendingAssignmentResponseListForStudent(assignmentResStage)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
                    @Override
                    public void accept(ArrayList<AssignmentResponse> assignmentResponses) {

                        if (assignmentResponses != null)
                            assignmentResponses = PredicateListFilter.filter((ArrayList) assignmentResponses, new Predicate<AssignmentResponse>() {
                                @Override
                                public boolean apply(AssignmentResponse assignmentResponse) {

                                    Calendar calendar1 = Calendar.getInstance();
                                    Calendar calendar2 = Calendar.getInstance();

                                    calendar1.setTime(DateUtils.convertrIsoDate(assignmentResponse.getAssignedDateTime()));
                                    if (DateUtils.compareTwoDate(calendar1, calendar2) == 0)
                                        return true;
                                    else return false;
                                }
                            });


                        mRxBus.send(new LoadTodaysAssignedAssignmentResponseListEvent(assignmentResponses));
//                if (callingFragment instanceof PendingAssignmentFragmentStudent)
//                    mRxBus.send(new LoadPendingAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof SubmittedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadSubmittedAssignmentResponseListStudent(assignmentResponses));
//
//                else if (callingFragment instanceof GradedAssignmentFragmentStudent)
//                    mRxBus.send(new LoadGradedAssignmentResponseListStudent(assignmentResponses));
                    }

                });
    }


    public void getOverDueAndNewAssignmentsCounts(String assignmentResStage) {
        mAssignmentResponseModel.getALLPendingAssignmentResponseListForStudent(assignmentResStage)
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
                .subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
                    public int mAssignmentOverdue = 0;
                    public int mAssignmentDue = 0;
                    public int mAssignmentNewAssigned = 0;

                    @Override
                    public void accept(final ArrayList<AssignmentResponse> assignmentResponses) {

                        if (assignmentResponses != null) {
                            for (AssignmentResponse assignmentResponse : assignmentResponses) {
                                AssignmentStatus assignmentStatus;
                                assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());


                                if (assignmentStatus == AssignmentStatus.OVERDUE) {
                                    mAssignmentOverdue++;
                                } else if (assignmentStatus == AssignmentStatus.DUE) {
                                    mAssignmentDue++;
                                } else if (assignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage())) {
                                    mAssignmentNewAssigned++;
                                }


                            }

                        }

                        LoadOverDueNdNewCountEvent loadOverDueNdNewCountEvent = new LoadOverDueNdNewCountEvent(mAssignmentOverdue, mAssignmentNewAssigned, mAssignmentDue);
                        mRxBus.send(loadOverDueNdNewCountEvent);


                    }
                });
    }


    public Observable<Integer> getNewAssignmentsCount() {
        return
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> subscriber) {

                        subscriber.onNext(mAssignmentResponseModel.getNewAssignmentsCount());
                        subscriber.onComplete();
                    }
                });

    }

    public void deleteNewAssignmentStudentStatus(String assignmentId) {

        AssignmentStudent assignmentStudent1 = mAssignmentResponseModel.isNewAssignment(assignmentId);
        if (assignmentStudent1 != null && !TextUtils.isEmpty(assignmentStudent1.getDocId())) {
            mDeleteObjectModel.deleteJsonAssignments(assignmentStudent1.getDocId());
        }

    }

    public Observable<ArrayList<AssignmentStudent>> getPendingAssignmentList(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentStudent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentStudent>> subscriber) {

                        ArrayList<AssignmentStudent> assignmentStudents = mAssignmentResponseModel.getPendingAssignmentList(fromDate, toDate, subject, skip, limit);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentStudent>> getOverDueAssignmentList(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {


        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentStudent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentStudent>> subscriber) {

                        ArrayList<AssignmentStudent> assignmentStudents = mAssignmentResponseModel.getOverDueAssignmentList(fromDate, toDate, subject, skip, limit);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentStudent>> getSubmittedAssignmentList(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {


        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentStudent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentStudent>> subscriber) {

                        ArrayList<AssignmentStudent> assignmentStudents = mAssignmentResponseModel.getSubmittedAssignmentList(fromDate, toDate, subject, skip, limit);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentStudent>> getAssignmentListByGroupId(final String fromDate, final String toDate, final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentStudent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentStudent>> subscriber) {

                        ArrayList<AssignmentStudent> assignmentStudents = mAssignmentResponseModel.getPendingAssignmentsByGroupIdAndDueDateQuery(fromDate, toDate, groupId, skip, limit);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentStudent>> getSubmittedAssignmentListByGroupId(final String fromDate, final String toDate, final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentStudent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentStudent>> subscriber) {

                        ArrayList<AssignmentStudent> assignmentStudents = mAssignmentResponseModel.getSubmittedAssignmentsByGroupIdAndDueDateQuery(fromDate, toDate, groupId, skip, limit);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public void changeAssignmentStudent(AssignmentResponse assignmentResponse, String oldAssignmentStudentDocId) {
        AssignmentStudent assignmentStudentNew = new AssignmentStudent();
        AssignmentStudent assignmentStudentOld = new AssignmentStudent();
        assignmentStudentOld = mAssignmentResponseModel.getDatabaseQueryHelper().retrieveAssignments(oldAssignmentStudentDocId, AssignmentStudent.class);
        assignmentStudentNew = assignmentStudentOld;
        assignmentStudentNew.setStage(assignmentResponse.getStage());
        assignmentStudentNew.setSubmissionDateTime(assignmentResponse.getSubmissionDateTime());
        assignmentStudentNew.setAssignmentScore(assignmentResponse.getAssignmentScore());
        assignmentStudentNew.setAssignedGroup(assignmentResponse.getAssignedGroup());
        assignmentStudentNew.setDocId("");
        assignmentStudentNew = mAssignmentResponseModel.saveAssignmentResponseToDatabase(assignmentStudentNew, assignmentResponse.getStage());

        mDeleteObjectModel.deleteJsonAssignments(oldAssignmentStudentDocId);
    }

    public Observable<AssignmentStudent> getSubmittedAssignmentFromObjectId(final String objectId) {
        return Observable.create(new ObservableOnSubscribe<AssignmentStudent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignmentStudent> e) throws Exception {
                e.onNext(mAssignmentResponseModel.getSubmittedAssignmentFromObjectId(objectId));
                e.onComplete();
            }
        });
    }

    public Observable<AssignmentStudent> getNotSubmittedAssignmentFromObjectId(final String objectId) {
        return Observable.create(new ObservableOnSubscribe<AssignmentStudent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignmentStudent> e) throws Exception {
                e.onNext(mAssignmentResponseModel.getNotSubmittedAssignmentFromObjectId(objectId));
                e.onComplete();
            }
        });
    }

    public SubmittedBy getSubmittedBy() {
        SubmittedBy submittedBy = new SubmittedBy();
        UserProfile appUser = mAppUserModel.getApplicationUser();
        submittedBy.setObjectId(appUser.getObjectId());
        submittedBy.setFirstName(appUser.getFirstName());
        submittedBy.setMiddleName(appUser.getMiddleName());
        submittedBy.setLastName(appUser.getLastName());
        Bitmap bitmap = ImageUtils.getScaledBitmapFromPath(mAppContext.getResources(), appUser.getThumbnail().getThumb());
        submittedBy.setUserPic(ImageUtils.encodeToBase64(bitmap));
        return submittedBy;
    }

    public int saveAssignmentResponse(AssignmentResponse assignmentResponse) {
        int saveAssignmentResponse = mAssignmentResponseModel.saveAssignmentResponse(assignmentResponse);
        createInternalNotificationForAssignmentResponse(assignmentResponse, ACTION_TYPE_NETWORK_UPLOAD);
        return saveAssignmentResponse;

    }

    private void createInternalNotificationForAssignmentResponse(AssignmentResponse assignmentResponse, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, assignmentResponse.getAlias());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(assignmentResponse.getAssignmentType());
            internalNotification.setObjectDocId(assignmentResponse.getDocId());
            internalNotification.setObjectId(assignmentResponse.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_ASSIGNMENT_RESPONSE);
            internalNotification.setTitle(assignmentResponse.getAssignmentTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }


}
