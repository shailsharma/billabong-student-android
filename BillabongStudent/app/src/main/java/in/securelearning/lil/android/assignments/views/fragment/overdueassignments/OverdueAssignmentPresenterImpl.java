/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific icon_language_c governing permissions and
 *  * limitations under the License.
 *
 */

package in.securelearning.lil.android.assignments.views.fragment.overdueassignments;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.assignments.events.LoadPendingAssignmentResponseListStudent;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentView;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.submittedassignment.SubmittedAssignmentFragment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj on 5/11/2016.
 */

public class OverdueAssignmentPresenterImpl implements OverDueAssignmentPresenter {

    private AssignmentView mAssignmentView;
    private Disposable mSubscription;
    private boolean mShouldAssignmentUpdate;
    private String mSearchQuery;

    public OverdueAssignmentPresenterImpl(AssignmentView pendingAssignmentView) {
        this.mAssignmentView = pendingAssignmentView;
        // this.assignmentInteractor = new AssignmentInteractorImpl();
        listenRxBusEvents();
    }

    @Override
    public void unsubscribeEvent() {
        mSubscription.dispose();
    }


    @Override
    public void getAssignmentsList() {
        ((OverDueAssignmentFragment) mAssignmentView).mAssignmentResponseStudentModel.getALLPendingAssignmentResponseListForStudent(AssignmentStage.STAGE_WIP.getAssignmentStage(), mAssignmentView);
    }

    @Override
    public void changeAssignmentResponseStage(AssignmentResponse assignmentResponse) {

    }


    private void listenRxBusEvents() {
        mSubscription = ((OverDueAssignmentFragment) mAssignmentView).mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {

                        if (event instanceof LoadPendingAssignmentResponseListStudent) {

                            List<AssignmentResponse> assignmentResponses=new ArrayList<AssignmentResponse>();
                            for(AssignmentResponse assignmentResponse:((LoadPendingAssignmentResponseListStudent) event).getAsiAssignmentResponse())
                            {
                                if(isOverDueAssignment(assignmentResponse))
                                    assignmentResponses.add(assignmentResponse);
                            }
                            mAssignmentView.setupRecyclerViewForList(assignmentResponses);

                        } else if (event instanceof AssignmentSubmittedEvent) {
                            AssignmentResponse assignmentResponse = ((AssignmentSubmittedEvent) event).getmAssignmentResponse();
                            mAssignmentView.refreshAssignmentList(assignmentResponse);
                        }
                        else if (event instanceof SearchSubmitEvent) {
                            mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                            if(OverDueAssignmentFragment.mAssignmentAdapter!=null)
                            OverDueAssignmentFragment.mAssignmentAdapter.search(mSearchQuery);
                        } else if (event instanceof SearchOpenEvent) {
                            mSearchQuery = "";
                        } else if (event instanceof SearchCloseEvent) {
                            mSearchQuery = "";
                            if(OverDueAssignmentFragment.mAssignmentAdapter!=null)
                            OverDueAssignmentFragment.mAssignmentAdapter.clearSearch();
                        }
                    }
                });
    }

    boolean isOverDueAssignment(AssignmentResponse assignmentResponse) {
        boolean isOverDueAssinment = false;
        AssignmentStatus assignmentStatus;
        assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());

        if (assignmentStatus == AssignmentStatus.OVERDUE)
            isOverDueAssinment = true;

        return isOverDueAssinment;

    }


}
