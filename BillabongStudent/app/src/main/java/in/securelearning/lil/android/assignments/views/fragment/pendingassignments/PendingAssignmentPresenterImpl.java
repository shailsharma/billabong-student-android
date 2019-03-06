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

package in.securelearning.lil.android.assignments.views.fragment.pendingassignments;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.events.LoadPendingAssignmentResponseListStudent;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentView;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import io.reactivex.disposables.Disposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj on 5/11/2016.
 */


public class PendingAssignmentPresenterImpl implements PendingAssignmentPresenter {

    private AssignmentView mAssignmentView;
    private Disposable mSubscription;
    private boolean mShouldAssignmentUpdate;
    private String mSearchQuery;

    public PendingAssignmentPresenterImpl(AssignmentView pendingAssignmentView) {
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
        ((PendingAssignmentFragment) mAssignmentView).mAssignmentResponseStudentModel.getALLPendingAssignmentResponseListForStudent(AssignmentStage.STAGE_WIP.getAssignmentStage(), mAssignmentView);
    }

    @Override
    public void changeAssignmentResponseStage(AssignmentResponse assignmentResponse) {

    }


    private void listenRxBusEvents() {
        mSubscription = ((PendingAssignmentFragment) mAssignmentView).mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {

                        if (event instanceof LoadPendingAssignmentResponseListStudent) {

                            List<AssignmentResponse> assignmentResponses = new ArrayList<AssignmentResponse>();
                            for (AssignmentResponse assignmentResponse : ((LoadPendingAssignmentResponseListStudent) event).getAsiAssignmentResponse()) {
                                if (isNewOrPendingAssignment(assignmentResponse))
                                    assignmentResponses.add(assignmentResponse);

//                                if (assignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage())) {
//                                    assignmentResponse.setStage(AssignmentStage.STAGE_WIP.getAssignmentStage());
//                                    mAssignmentResponseModel.saveAssignmentResponse(assignmentResponse);
//                                }

                            }
                            mAssignmentView.setupRecyclerViewForList(assignmentResponses);

                        } else if (event instanceof AssignmentSubmittedEvent) {
                            AssignmentResponse assignmentResponse = ((AssignmentSubmittedEvent) event).getmAssignmentResponse();
                            mAssignmentView.refreshAssignmentList(assignmentResponse);
                        }
                        else if (event instanceof SearchSubmitEvent) {
                            mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                            if(PendingAssignmentFragment.mAssignmentAdapter!=null)
                            PendingAssignmentFragment.mAssignmentAdapter.search(mSearchQuery);
                        } else if (event instanceof SearchOpenEvent) {
                            mSearchQuery = "";
                        } else if (event instanceof SearchCloseEvent) {
                            mSearchQuery = "";
                            if(PendingAssignmentFragment.mAssignmentAdapter!=null)
                            PendingAssignmentFragment.mAssignmentAdapter.clearSearch();
                        }
                    }
                });
    }

    boolean isNewOrPendingAssignment(AssignmentResponse assignmentResponse) {
        boolean isOverDueAssinment = false;
        AssignmentStatus assignmentStatus;
        assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());

        if (assignmentStatus == AssignmentStatus.PENDING)
            isOverDueAssinment = true;
        else if (assignmentStatus == AssignmentStatus.DUE)
            isOverDueAssinment = true;
        else if (assignmentStatus == AssignmentStatus.NEW)
            isOverDueAssinment = true;

        return isOverDueAssinment;

    }

}
