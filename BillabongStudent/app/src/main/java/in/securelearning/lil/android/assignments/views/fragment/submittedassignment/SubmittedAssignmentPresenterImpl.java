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

package in.securelearning.lil.android.assignments.views.fragment.submittedassignment;


import in.securelearning.lil.android.assignments.events.LoadSubmittedAssignmentResponseListStudent;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentView;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentFragment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
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
 * Created by Pushkar Raj on 6/2/2016.
 */

public class SubmittedAssignmentPresenterImpl implements SubmittedAssignmentPresenter {


    private AssignmentView mAssignmentView;
    private Disposable mSubscription;
    private String mSearchQuery;

    public SubmittedAssignmentPresenterImpl(AssignmentView submittedAssignmentView) {
        this.mAssignmentView = submittedAssignmentView;

        listenRxBusEvents();
    }

    @Override
    public void unsubscribeEvent() {
        mSubscription.dispose();
    }


    @Override
    public void getAssignmentsList() {
        ((SubmittedAssignmentFragment) mAssignmentView).mAssignmentResponseStudentModel.getAssignmentResponseListForStudent(AssignmentStage.STAGE_GRADED.getAssignmentStage(), mAssignmentView);
    }


    private void listenRxBusEvents() {
        mSubscription = ((SubmittedAssignmentFragment) mAssignmentView).mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {
                        if (event instanceof LoadSubmittedAssignmentResponseListStudent) {
                            mAssignmentView.setupRecyclerViewForList(((LoadSubmittedAssignmentResponseListStudent) event).getAsiAssignmentResponse());
                        } else if (event instanceof AssignmentSubmittedEvent) {
                            AssignmentResponse assignmentResponse = ((AssignmentSubmittedEvent) event).getmAssignmentResponse();
                            mAssignmentView.refreshAssignmentList(assignmentResponse);
                        }
                        else if (event instanceof SearchSubmitEvent) {
                            mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                            if(SubmittedAssignmentFragment.mAssignmentAdapter!=null)
                            SubmittedAssignmentFragment.mAssignmentAdapter.search(mSearchQuery);
                        } else if (event instanceof SearchOpenEvent) {
                            mSearchQuery = "";
                        } else if (event instanceof SearchCloseEvent) {
                            mSearchQuery = "";
                            if(SubmittedAssignmentFragment.mAssignmentAdapter!=null)
                            SubmittedAssignmentFragment.mAssignmentAdapter.clearSearch();
                        }

                    }
                });
    }
}
