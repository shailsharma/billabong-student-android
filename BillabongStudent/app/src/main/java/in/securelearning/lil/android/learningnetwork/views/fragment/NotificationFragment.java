package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentBulletinBinding;
import in.securelearning.lil.android.app.databinding.FragmentPlaceholderItemBinding;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.UserNotification;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.UserNotificationModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Secure on 27-09-2017
 */

public class NotificationFragment extends Fragment {
    RecyclerViewAdapter mAdapter;
    FragmentBulletinBinding mBinding;
    @Inject
    UserNotificationModel mUserNotificationModel;
    @Inject
    public RxBus mRxBus;
    private static String COLUMN_COUNT = "column_count";
    private int mColumnCount = 1;
    private Disposable mSubscription;


    public NotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bulletin, container, false);
        View rootView = mBinding.getRoot();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
        }
        return rootView;
    }

    public static NotificationFragment newInstance(int colCount) {
        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        args.putInt(COLUMN_COUNT, colCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        listenRxBusEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter = null;
        }
    }


    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(AboutCourse.class)) {
                    getNotificationData();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.recyclerViewBulletin.setHasFixedSize(true);
        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        mBinding.recyclerViewBulletin.setLayoutManager(layoutManager);
        getNotificationData();
    }

    private void getNotificationData() {
        Observable.create(new ObservableOnSubscribe<ArrayList<UserNotification>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<UserNotification>> e) throws Exception {
                ArrayList<UserNotification> mUserNArrayList = mUserNotificationModel.getCompleteList();
                if (mUserNArrayList != null) {
                    e.onNext(mUserNArrayList);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<UserNotification>>() {
                    @Override
                    public void accept(ArrayList<UserNotification> userNotification) throws Exception {
                        if (userNotification.size() > 0) {
                            mBinding.layoutNoBulletin.setVisibility(View.GONE);
                            mAdapter = new RecyclerViewAdapter(userNotification);
                            mBinding.recyclerViewBulletin.setAdapter(mAdapter);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    class RecyclerViewAdapter
            extends RecyclerView.Adapter
            <RecyclerViewAdapter.ListItemViewHolder> {
        ArrayList<UserNotification> mArrayList;

        public void clear() {
            if (mArrayList != null) {
                mArrayList.clear();
            }
        }

        RecyclerViewAdapter(ArrayList<UserNotification> userNotificationsList) {
            mArrayList = userNotificationsList;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            FragmentPlaceholderItemBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.fragment_placeholder_item, viewGroup, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
            final UserNotification data = mArrayList.get(position);
            try {
                if (data.getThumbnail() != null && data.getThumbnail().getUrl() != null) {
                    Picasso.with(getContext()).load(data.getThumbnail().getUrl()).placeholder(R.drawable.image_loading_thumbnail).into(viewHolder.mViewBinding.notificationImage);
                } else {
                    Picasso.with(getContext()).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(viewHolder.mViewBinding.notificationImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(getContext()).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(viewHolder.mViewBinding.notificationImage);
            }
            viewHolder.mViewBinding.textName.setText(String.valueOf(data.getTitle()));
            viewHolder.mViewBinding.textType.setText(String.valueOf(data.getObjectType()));

            viewHolder.mViewBinding.removeNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNotification(data.getDocId(), position);
                }
            });
            viewHolder.mViewBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetailActivity(data);
                }
            });
        }

        private void openCourseDetailActivity(UserNotification item) {
            Class objectClass = null;
            if (item.getObjectType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (item.getObjectType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (item.getObjectType().toLowerCase().contains("map")) {
                objectClass = ConceptMap.class;
            } else if (item.getObjectType().toLowerCase().contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (item.getObjectType().toLowerCase().contains("pop")) {
                objectClass = PopUps.class;
            } else if (item.getObjectType().toLowerCase().contains("video")) {
                objectClass = InteractiveVideo.class;
            }
            if (objectClass != null)
                startActivity(CourseDetailActivity.getStartActivityIntent(getContext(), item.getObjectId(), objectClass, ""));
        }

        private void deleteNotification(String docId, int position) {
            mUserNotificationModel.delete(docId);
            mArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mArrayList.size());
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        class ListItemViewHolder extends RecyclerView.ViewHolder {
            FragmentPlaceholderItemBinding mViewBinding;

            ListItemViewHolder(FragmentPlaceholderItemBinding view) {
                super(view.getRoot());
                mViewBinding = view;
            }
        }
    }

}
