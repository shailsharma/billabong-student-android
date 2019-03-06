package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutTrainingItemBinding;
import in.securelearning.lil.android.base.dataobjects.Coordinator;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.TrainingDetailsActivity;
import in.securelearning.lil.android.syncadapter.events.RefreshTrainingListEvent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 29-Jan-18.
 */

public class MyTrainingsFragment extends Fragment {

    @Inject
    HomeModel mHomeModel;
    @Inject
    RxBus mRxBus;
    public static final String COLUMN_COUNT = "columnCount";
    LayoutRecyclerViewBinding mBinding;
    private int mColumnCount;
    private TrainingAdapter mTrainingAdapter;
    private Disposable mDisposable;

    public static MyTrainingsFragment newInstance(int columnCount) {
        MyTrainingsFragment fragment = new MyTrainingsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_recycler_view, container, false);
        mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        listenRxEvent();
        initializeRecyclerView(new ArrayList<Training>());
        getMyTrainings();
        return mBinding.getRoot();
    }

    private void setDefault() {
        initializeRecyclerView(new ArrayList<Training>());
    }

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof RefreshTrainingListEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            setDefault();
                            getMyTrainings();
                        }
                    });
                }
            }
        });
    }

    private void initializeRecyclerView(ArrayList<Training> trainings) {
        if (mColumnCount > 1) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
            mBinding.list.setLayoutManager(layoutManager);
        } else {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        }

        mTrainingAdapter = new TrainingAdapter(getContext(), trainings);
        mBinding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.list.setAdapter(mTrainingAdapter);

        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeRefreshLayout.setEnabled(true);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDefault();
                getMyTrainings();
            }
        });

    }

    private void getMyTrainings() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<ArrayList<Training>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Training>> e) throws Exception {
                e.onNext(mHomeModel.getTrainingList());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Training>>() {
                    @Override
                    public void accept(ArrayList<Training> trainings) throws Exception {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        if (trainings != null && !trainings.isEmpty()) {
                            if (mTrainingAdapter != null) {
                                mBinding.list.setVisibility(View.VISIBLE);
                                mTrainingAdapter.clear();
                                mTrainingAdapter.addValues(trainings);
                            }
                        } else {
                            noResultFound();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.swipeRefreshLayout.setRefreshing(false);

                    }
                });
    }

    private void noResultFound() {

        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        mBinding.imageViewNoResult.setImageResource(R.drawable.logo_training_g);
        mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoTrainingsFound));


    }

    private class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {
        private Context mContext;
        private java.util.ArrayList<Training> mTrainings;

        public TrainingAdapter(Context context, java.util.ArrayList<Training> trainings) {
            mContext = context;
            mTrainings = trainings;
        }

        @Override
        public TrainingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutTrainingItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_training_item, parent, false);
            return new TrainingAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TrainingAdapter.ViewHolder holder, int position) {
            final Training training = mTrainings.get(position);
            setTrainingCoordinators(training.getGroupId(), holder.mBinding);
            setThumbnail(training.getThumbnail(), holder.mBinding.imageView);
            setTrainingDate(training.getStartDate(), training.getEndDate(), holder.mBinding);
            holder.mBinding.textViewTitle.setText(training.getTitle());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(TrainingDetailsActivity.getStartIntent(getContext(), training.getObjectId(), training.getTitle(), training.getGroupId(), getSubjectIds(training.getSessions()), 0));
                }
            });
        }

        private void setTrainingDate(String startDate, String endDate, LayoutTrainingItemBinding binding) {
            if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                binding.textViewDate.setText("From " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(startDate)) + " - To " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(endDate)));
            }

        }

        private void setTrainingCoordinators(final String trainingGroupId, final LayoutTrainingItemBinding binding) {
            Observable.create(new ObservableOnSubscribe<Group>() {
                @Override
                public void subscribe(ObservableEmitter<Group> e) throws Exception {
                    Group group = mHomeModel.getGroupFromId(trainingGroupId);
                    e.onNext(group);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Group>() {
                        @Override
                        public void accept(Group group) throws Exception {
                            java.util.ArrayList<String> names = new java.util.ArrayList<>();
                            for (Coordinator coordinator : group.getCoordinators()) {
                                names.add(coordinator.getName());
                            }
                            if (names != null && !names.isEmpty()) {
                                binding.textViewCoordinatorName.setText(TextUtils.join(",", names));

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }

        private java.util.ArrayList<String> getSubjectIds(java.util.ArrayList<TrainingSession> trainingSessions) {
            java.util.ArrayList<String> subjectIds = new java.util.ArrayList<>();
            if (trainingSessions != null && !trainingSessions.isEmpty()) {
                for (int i = 0; i < trainingSessions.size(); i++) {
                    SubjectSuper subject = trainingSessions.get(i).getSubject();
                    if (subject != null) {
                        subjectIds.add(trainingSessions.get(i).getSubject().getId());

                    }
                }
            }

            return subjectIds;
        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(getContext()).load(thumbnail.getLocalUrl()).resize(360, 240).centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(getContext()).load(thumbnail.getUrl()).resize(360, 240).centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(getContext()).load(thumbnail.getThumb()).resize(360, 240).centerCrop().into(imageView);
            } else {
                Picasso.with(getContext()).load(R.drawable.noimage).resize(360, 240).centerCrop().into(imageView);
            }
        }

        @Override
        public int getItemCount() {
            return mTrainings.size();
        }

        public void addValues(ArrayList<Training> list) {
            if (mTrainings != null) {
                mTrainings.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mTrainings != null) {
                mTrainings.clear();
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutTrainingItemBinding mBinding;

            public ViewHolder(LayoutTrainingItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
