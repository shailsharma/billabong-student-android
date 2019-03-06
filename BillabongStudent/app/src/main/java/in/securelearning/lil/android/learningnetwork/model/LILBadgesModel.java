package in.securelearning.lil.android.learningnetwork.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.UserEarnBadges;
import in.securelearning.lil.android.learningnetwork.events.LoadAssignedBadgeEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadAssignedBadgesListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadLilBadgesListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadUserEarnBadgeListEvent;
import in.securelearning.lil.android.learningnetwork.model.interfaces.PostDataModelInterface;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.db.DatabaseHandler;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.Keys;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pushkar Raj on 9/1/2016.
 */
public class LILBadgesModel extends BaseModelLearningNetwork {

    @Inject
    RxBus mRxBus;

    @Inject
    Context mAppContext;

    @Inject
    BadgesModel mBadgesModel;

    public LILBadgesModel() {
        super();

        getLearningNetworkComponent().inject(this);
    }


    public void getAssignedBadgesList() {
        Observable<ArrayList<AssignedBadges>> fetchLILBadgesObservable = mBadgesModel.fetchAssignedBadgesList();
        fetchLILBadgesObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<AssignedBadges>>() {
            @Override
            public void accept(ArrayList<AssignedBadges> badges) {
                mRxBus.send(new LoadAssignedBadgesListEvent(badges));
            }
        });
    }

    public void getLilBadgesList() {
        Observable<ArrayList<LILBadges>> fetchLILBadgesObservable = mBadgesModel.fetchLilBadgesList();
        fetchLILBadgesObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<LILBadges>>() {
            @Override
            public void accept(ArrayList<LILBadges> badges) {
                mRxBus.send(new LoadLilBadgesListEvent(badges));
            }
        });
    }

    /**
     * @param uid
     */
    public void getAssignedBadgesByUid(String uid) {
        Observable<AssignedBadges> fetchLILBadgesObservable = mBadgesModel.fetchAssignedBadgesFromUid(uid);
        fetchLILBadgesObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<AssignedBadges>() {
            @Override
            public void accept(AssignedBadges assignedBadges) {
                mRxBus.send(new LoadAssignedBadgeEvent(assignedBadges));
            }
        });
    }

    /**
     * @param uid
     */
    public void getAssignedBadges(String uid) {
        Observable<AssignedBadges> fetchLILBadgesObservable = mBadgesModel.fetchAssignedBadgesFromUid(uid);
        fetchLILBadgesObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<AssignedBadges>() {
            @Override
            public void accept(AssignedBadges assignedBadges) {
                mRxBus.send(new LoadAssignedBadgeEvent(assignedBadges));
            }
        });
    }


    /**
     * @param uid
     */
    public void getUserEarnBadges(String uid) {
        Observable<ArrayList<LILBadges>> fetchLILBadgesObservable = mBadgesModel.fetchUserEarnLilBadgesList(uid);
        fetchLILBadgesObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<LILBadges>>() {
            @Override
            public void accept(ArrayList<LILBadges> lilBadges) {
                mRxBus.send(new LoadUserEarnBadgeListEvent(lilBadges));
            }
        });
    }


}
