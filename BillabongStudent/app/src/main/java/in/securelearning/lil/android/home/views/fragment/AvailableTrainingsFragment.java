package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutTrainingItemBinding;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.TrainingDetailsActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.events.RefreshAvailableTrainingListEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 29-Jan-18.
 */

public class AvailableTrainingsFragment extends Fragment {

    @Inject
    NetworkModel mNetworkModel;
    @Inject
    HomeModel mHomeModel;
    @Inject
    RxBus mRxBus;
    public static final String COLUMN_COUNT = "columnCount";
    private boolean isRefreshing = false;
    LayoutRecyclerViewBinding mBinding;
    private int mSkip = 0;
    private int mLimit = 10;
    private int mColumnCount;
    private TrainingAdapter mTrainingAdapter;
    private Disposable mDisposable;

    public static AvailableTrainingsFragment newInstance(int columnCount) {
        AvailableTrainingsFragment fragment = new AvailableTrainingsFragment();
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
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
        }
//        initializeRecyclerView(new ArrayList<Training>());
//        fetchAvailableTrainings(mSkip, mLimit);
        listenRxEvent();
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDefault();
        fetchAvailableTrainings(mSkip, mLimit);
    }

    private void setDefault() {
        initializeRecyclerView(new ArrayList<Training>());
        mSkip = 0;
    }

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof RefreshAvailableTrainingListEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (!isRefreshing) {
                                setDefault();
                                fetchAvailableTrainings(mSkip, mLimit);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initializeRecyclerView(ArrayList<Training> list) {
        LinearLayoutManager layoutManager = null;
//        if (mColumnCount > 1) {
//            layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
//            mBinding.list.setLayoutManager(layoutManager);
//        } else {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.list.setLayoutManager(layoutManager);
//        }

        mTrainingAdapter = new TrainingAdapter(getContext(), list);
        mBinding.list.setAdapter(mTrainingAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.list.clearOnScrollListeners();
            mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            fetchAvailableTrainings(mSkip, mLimit);

                        }
                    }

                }

            });
        }
    }

    private void fetchAvailableTrainings(final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            if (mSkip > 0) {
                mBinding.bottomProgress.setVisibility(View.VISIBLE);
            } else {
                mBinding.progressBar.setVisibility(View.VISIBLE);
                mBinding.list.setVisibility(View.GONE);
            }

            Observable.create(new ObservableOnSubscribe<java.util.ArrayList<Training>>() {
                @Override
                public void subscribe(ObservableEmitter<java.util.ArrayList<Training>> subscriber) throws Exception {
                    isRefreshing = true;
                    Call<java.util.ArrayList<Training>> call = mNetworkModel.fetchUpcomingTrainings(skip, limit);
                    Response<java.util.ArrayList<Training>> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        java.util.ArrayList<Training> list = response.body();
                        Log.e("UpcomingTrainings1--", "Successful");
                        subscriber.onNext(list);
                    } else if (response.code() == 404) {
                        throw new Exception(getString(R.string.messageNoTrainingsFound));
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                        Response<java.util.ArrayList<Training>> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            java.util.ArrayList<Training> list = response.body();
                            Log.e("UpcomingTrainings2--", "Successful");
                            subscriber.onNext(list);
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                        } else if (response2.code() == 404) {
                            throw new Exception(getString(R.string.messageNoTrainingsFound));
                        } else {
                            Log.e("UpcomingTrainings2--", "Failed");
                            throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                        }
                    } else {
                        Log.e("UpcomingTrainings1--", "Failed");
                        throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                    }
                    isRefreshing = false;

                    subscriber.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<java.util.ArrayList<Training>>() {
                        @Override
                        public void accept(java.util.ArrayList<Training> list) throws Exception {
                            isRefreshing = false;
                            mBinding.progressBar.setVisibility(View.GONE);
                            mSkip += list.size();
                            noResultFound(mSkip);
                            if (mTrainingAdapter != null) {
                                mTrainingAdapter.addValues(list);
                            }
                            if (list.size() < limit) {
                                mBinding.list.clearOnScrollListeners();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            isRefreshing = false;
                            throwable.printStackTrace();
                            if (throwable.getMessage().equals(getString(R.string.messageNoTrainingsFound))) {
                                noResultFound(skip);
                            } else {
                                unableToFetch(skip, limit);
                            }
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            isRefreshing = false;
                        }
                    });

        } else {
            noInternet(skip, limit);
        }
    }

    private void noInternet(final int skip, final int limit) {
        String message = getContext().getString(R.string.error_message_no_internet);
        if (skip > 0) {
            Snackbar.make(mBinding.list, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction((R.string.labelRetry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fetchAvailableTrainings(skip, limit);
                        }
                    })
                    .show();
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutRetry.setVisibility(View.VISIBLE);
            mBinding.imageViewRetry.setImageResource(R.drawable.no_internet);
            mBinding.textViewRetry.setText(getContext().getString(R.string.error_message_no_internet));
            mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchAvailableTrainings(skip, limit);
                }
            });
        }

    }

    private void unableToFetch(final int skip, final int limit) {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.logo_training_g);
        mBinding.textViewRetry.setText(getContext().getString(R.string.messageTrainingsFetchFailed));
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAvailableTrainings(skip, limit);
            }
        });
    }

    private void noResultFound(int skip) {
        if (skip > 0) {
            mBinding.list.setVisibility(View.VISIBLE);
            mBinding.layoutNoResult.setVisibility(View.GONE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.imageViewNoResult.setImageResource(R.drawable.logo_training_g);
            mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoTrainingsFound));
        }

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
            setTrainingCoordinators(training, holder.mBinding);
            setThumbnail(training.getThumbnail(), holder.mBinding.imageView);
            setTrainingDate(training.getStartDate(), training.getEndDate(), holder.mBinding);
            holder.mBinding.textViewTitle.setText(training.getTitle());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(training.getObjectId())) {
                        getContext().startActivity(TrainingDetailsActivity.getStartIntent(getContext(), training.getObjectId(), training.getTitle(), training.getGroupId(), getSubjectIds(training.getSessions()), 1));

                    } else {
                        Toast.makeText(mContext, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void setTrainingDate(String startDate, String endDate, LayoutTrainingItemBinding binding) {
            if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                binding.textViewDate.setText("From " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(startDate)) + " - To " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(endDate)));
            }

        }

        private void setTrainingCoordinators(final Training training, final LayoutTrainingItemBinding binding) {
            binding.textViewCoordinatorName.setVisibility(View.INVISIBLE);
//            Observable.create(new ObservableOnSubscribe<Group>() {
//                @Override
//                public void subscribe(ObservableEmitter<Group> e) throws Exception {
//                    Call<Group> call = mNetworkModel.fetchGroup(trainingGroupId);
//                    Response<Group> response = call.execute();
//                    if (response != null && response.isSuccessful()) {
//                        Group group = response.body();
//                        Log.e("TrainingGroup1", "successful");
//                        e.onNext(group);
//                    } else if (response.code() == 404) {
//                        throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
//                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
//                        Response<Group> response2 = call.clone().execute();
//                        if (response2 != null && response2.isSuccessful()) {
//                            Group group = response.body();
//                            Log.e("TrainingGroup2--", "Successful");
//                            e.onNext(group);
//                        } else if ((response2.code() == 401)) {
//                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
//                        } else if (response2.code() == 404) {
//                            throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
//                        } else {
//                            Log.e("TrainingGroup2--", "Failed");
//                            throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
//                        }
//                    } else {
//                        Log.e("TrainingGroup1--", "Failed");
//                        throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
//                    }
//
//                    e.onComplete();
//                }
//            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Group>() {
//                        @Override
//                        public void accept(Group group) throws Exception {
//                            java.util.ArrayList<String> names = new java.util.ArrayList<>();
//                            for (Coordinator coordinator : group.getCoordinators()) {
//                                names.add(coordinator.getName());
//                            }
            if (training != null && training.getAuthor() != null && !TextUtils.isEmpty(training.getAuthor().getName())) {
                binding.textViewCoordinatorName.setText(training.getAuthor().getName());
                binding.textViewCoordinatorName.setVisibility(View.VISIBLE);

            }
            //}
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            throwable.printStackTrace();
//                        }
//                    });
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

        public void addValues(java.util.ArrayList<Training> list) {
            if (mTrainings != null) {
                mTrainings.addAll(list);
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
