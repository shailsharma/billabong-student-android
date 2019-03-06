package in.securelearning.lil.android.assignments.model;

import android.text.TextUtils;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.events.AllStudentSubmittedEvent;
import in.securelearning.lil.android.assignments.events.LoadPendingAssignmentListByTeacher;
import in.securelearning.lil.android.assignments.events.LoadTodaysSubmittedAssignmentResponseListEvent;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.dataobjects.AssignedBy;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj on 23-11-2016.
 */
public class AssignmentTeacherModel {

    @Inject
    RxBus mRxBus;

    @Inject
    QuizModel mQuizModel;

    @Inject
    AssignmentModel mAssignmentModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    public AssignmentTeacherModel() {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    /**
     * asynchronous load of assignments for Pending view
     */
    public Observable<ArrayList<Assignment>> getAssignmentListForPendingView() {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Assignment>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Assignment>> subscriber) {

                        ArrayList<Assignment> assignments = mAssignmentModel.getAssignmentListFromAssignedBySync(getAssignedBy());
                        subscriber.onNext(assignments);
                        subscriber.onComplete();
                    }
                });


    }

    public Observable<ArrayList<Quiz>> getQuizzesList(final int skip, final int limit, final String subject) {


        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Quiz>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Quiz>> subscriber) {

                        ArrayList<Quiz> quizzes = mQuizModel.getQuizList(skip, limit, subject);
                        subscriber.onNext(quizzes);
                        subscriber.onComplete();

                        //mRxBus.send(new LoadQuizListEvent(quizzes));
                    }
                });
    }

    public void fetchTodaysTurnInAssignmentsResponse(final ArrayList<Assignment> teachersAllAssignedAssignments) {

        Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AssignmentResponse>> subscriber) {

                ArrayList<AssignmentResponse> assignmentResponses = mAssignmentModel.fetchAllTurnedInAssignmentList(teachersAllAssignedAssignments);
                subscriber.onNext(assignmentResponses);
                subscriber.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<ArrayList<AssignmentResponse>>() {
                    @Override
                    public void accept(ArrayList<AssignmentResponse> assignmentResponses) {
                        ArrayList<AssignmentResponse> localCopyResponses = new ArrayList<AssignmentResponse>();
                        localCopyResponses.addAll(assignmentResponses);
                        int mTodaysturnedIn = 0;
                        if (assignmentResponses != null)
                            for (AssignmentResponse assignmentResponse : assignmentResponses) {
                                Calendar calendar1 = Calendar.getInstance();
                                Calendar calendar2 = Calendar.getInstance();

                                calendar1.setTime(DateUtils.convertrIsoDate(assignmentResponse.getSubmissionDateTime()));
                                calendar2.setTime(new Date());

                                if (assignmentResponse.getSubmissionDateTime() != null && !assignmentResponse.getSubmissionDateTime().isEmpty() && compareTwoDate(calendar1, calendar2) == 0)
                                    mTodaysturnedIn++;
                                else {
                                    if (assignmentResponse.getSubmissionDateTime().isEmpty())
                                        localCopyResponses.remove(assignmentResponse);
                                }
                            }

                        mRxBus.send(new LoadTodaysSubmittedAssignmentResponseListEvent(localCopyResponses.size(), mTodaysturnedIn));
                    }
                });
    }

    /**
     * asynchronous load of assignments for Pending view
     */
    public void getAssignmentListAssignedByTeacherToday(final Date assignedDate) {
        Observable.create(new ObservableOnSubscribe<ArrayList<Assignment>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Assignment>> subscriber) {
                ArrayList<Assignment> assignments = mAssignmentModel.getAssignmentListFromAssignedBySync(getAssignedBy());
                subscriber.onNext(assignments);
                subscriber.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<ArrayList<Assignment>>() {
                    @Override
                    public void accept(ArrayList<Assignment> assignments) {

                        ArrayList<Assignment> todaysAssignedAssignments = new ArrayList<Assignment>();
                        todaysAssignedAssignments.addAll(assignments);
                        if (assignments != null)
                            todaysAssignedAssignments = PredicateListFilter.filter(todaysAssignedAssignments, new Predicate<Assignment>() {
                                @Override
                                public boolean apply(Assignment assignment) {

                                    Calendar calendar1 = Calendar.getInstance();
                                    Calendar calendar2 = Calendar.getInstance();

                                    calendar1.setTime(DateUtils.convertrIsoDate(assignment.getAssignedDateTime()));
                                    calendar2.setTime(assignedDate);

                                    if (assignment.getAssignedDateTime() != null && !assignment.getAssignedDateTime().isEmpty() && compareTwoDate(calendar1, calendar2) == 0)
                                        return true;
                                    else return false;
                                }
                            });

                        mRxBus.send(new LoadPendingAssignmentListByTeacher(todaysAssignedAssignments, assignments));
                    }
                });

    }

    /**
     * Compare two calendar without the time portions
     *
     * @param c1
     * @param c2
     * @return
     */
    public int compareTwoDate(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * create assignedBy object from current user.
     *
     * @return AssignedBy
     */
    public AssignedBy getAssignedBy() {
        return new AssignedBy(mAppUserModel.getObjectId(), mAppUserModel.getApplicationUser().getName());
    }

    public Observable<ArrayList<Assignment>> getAssignmentListForPendingView(final int skip, final int limit, final String subject) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Assignment>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Assignment>> subscriber) {

                        ArrayList<Assignment> assignments = mAssignmentModel.getAssignmentListFromAssignedBySync(getAssignedBy(), skip, limit, subject, "", "");
                        subscriber.onNext(assignments);
                        subscriber.onComplete();
                    }
                });

    }

    public Observable<ArrayList<Assignment>> getAssignmentListForPendingView(final int skip, final int limit, final String subject, final String startDate, final String endDate, final String groupId) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Assignment>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Assignment>> subscriber) {
//                        ArrayList<Assignment> assignments = getAssignmentListFromAssignedByGroupId(getAssignedBy(), skip, limit, subject, startDate, endDate, groupId);
                        ArrayList<Assignment> assignments = mAssignmentModel.getAssignmentListFromAssignedByGroupId(getAssignedBy(), skip, limit, subject, startDate, endDate, groupId);
                        subscriber.onNext(assignments);
                        subscriber.onComplete();
                    }
                });

    }

    public Observable<ArrayList<Assignment>> getAssignmentsFromDueDate(final String startDate, final String endDate) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Assignment>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Assignment>> subscriber) {

                        ArrayList<Assignment> assignmentStudents = mAssignmentModel.getAssignmentListByDueDate(startDate, endDate);
                        subscriber.onNext(assignmentStudents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<QuizMinimal>> getQuizzesMinimalList(final int skip, final int limit, final String subject) {


        return
                Observable.create(new ObservableOnSubscribe<ArrayList<QuizMinimal>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<QuizMinimal>> subscriber) {

                        ArrayList<QuizMinimal> quizzesMinimal = mQuizModel.getNotPublishedQuizMinimalList(skip, limit, subject);
                        subscriber.onNext(quizzesMinimal);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentMinimal>> getIncompleteAssignmentMinimalList(final String assignById, final String subject, final String startDate, final String endDate, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentMinimal>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentMinimal>> subscriber) {
                        if (!TextUtils.isEmpty(subject)) {
                            ArrayList<AssignmentMinimal> assignments = mAssignmentModel.getIncompleteAssignmentsBySubject(assignById, subject, startDate, endDate, skip, limit);
                            subscriber.onNext(assignments);
                            subscriber.onComplete();
                        } else {
                            ArrayList<AssignmentMinimal> assignments = mAssignmentModel.getIncompleteAssignmentsByDate(assignById, startDate, endDate, skip, limit);
                            subscriber.onNext(assignments);
                            subscriber.onComplete();
                        }

                    }
                });

    }

    public Observable<ArrayList<AssignmentMinimal>> getCompletedAssignmentMinimalList(final String assignById, final String subjectId, final String startDate, final String endDate, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentMinimal>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentMinimal>> subscriber) {
                        if (!TextUtils.isEmpty(subjectId)) {
                            ArrayList<AssignmentMinimal> assignments = mAssignmentModel.getCompletedAssignmentsBySubject(assignById, subjectId, startDate, endDate, skip, limit);
                            subscriber.onNext(assignments);
                            subscriber.onComplete();
                        } else {
                            ArrayList<AssignmentMinimal> assignments = mAssignmentModel.getCompletedAssignmentsByDate(assignById, startDate, endDate, skip, limit);
                            subscriber.onNext(assignments);
                            subscriber.onComplete();
                        }

                    }
                });

    }

    public Observable<Integer> getSubmittedAssignmentResponseCount(final String assignmentId, final String subject) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                e.onNext(mAssignmentModel.getSubmittedAssignmentResponseCounts(assignmentId, subject));
                e.onComplete();
            }
        });
    }

    public Observable<Integer> getAllPendingAndOverDueAssignmentCounts(final String assignById, final String subject, final String startDate, final String endDate, final int skip, final int limit) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(mAssignmentModel.getAllPendingAndOverDueAssignmentCounts(assignById, startDate, endDate, skip, limit));
                e.onComplete();
            }
        });
    }

    public void deleteAssignmentResponsesForCurrentAssignment(final String assignmentId) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mAssignmentModel.deleteAssignmentStudentsByAssignmentId(assignmentId);

            }
        });
    }

    public void changeAssignmentMinimal(String oldAssignmentMinimalDocId) {
        try {
            AssignmentMinimal assignmentMinimalNew = new AssignmentMinimal();
            AssignmentMinimal assignmentMinimalOld = new AssignmentMinimal();
            assignmentMinimalOld = mAssignmentModel.getDatabaseQueryHelper().retrieveAssignments(oldAssignmentMinimalDocId, AssignmentMinimal.class);
            assignmentMinimalNew = assignmentMinimalOld;
            assignmentMinimalNew.setStage(AssignmentStage.STAGE_GRADED.getAssignmentStage());
            assignmentMinimalNew.setDocId("");
            assignmentMinimalNew = mAssignmentModel.saveCompletedAssignmentMinimalToDatabase(assignmentMinimalNew);
            mDeleteObjectModel.deleteJsonAssignments(oldAssignmentMinimalDocId);
            mRxBus.send(new AllStudentSubmittedEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Observable<ArrayList<AssignmentMinimal>> getAssignmentListByGroupId(final String fromDate, final String toDate, final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentMinimal>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentMinimal>> subscriber) {

                        ArrayList<AssignmentMinimal> list = mAssignmentModel.getPendingAssignmentsByGroupIdAndDueDateQuery(fromDate, toDate, groupId, skip, limit);
                        subscriber.onNext(list);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<AssignmentMinimal>> getCompletedAssignmentListByGroupId(final String fromDate, final String toDate, final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<AssignmentMinimal>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AssignmentMinimal>> subscriber) {

                        ArrayList<AssignmentMinimal> list = mAssignmentModel.getCompletedAssignmentsByGroupIdAndDueDateQuery(fromDate, toDate, groupId, skip, limit);
                        subscriber.onNext(list);
                        subscriber.onComplete();
                    }
                });
    }

}
