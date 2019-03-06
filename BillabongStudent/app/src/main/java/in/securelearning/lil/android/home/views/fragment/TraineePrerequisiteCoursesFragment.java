package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.PrerequisiteCoursesPostData;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TraineePrerequisiteCoursesFragment extends Fragment {
    @Inject
    HomeModel mHomeModel;
    @Inject
    NetworkModel mNetworkModel;
    public static final String TRAINING_ID = "trainingId";
    public static final String COLUMN_COUNT = "columnCount";
    private int mColumnCount;
    private String mTrainingId;
    CourseAdapter mCourseAdapter;
    LayoutRecyclerViewBinding mBinding;
    private int mSkip = 0;
    private int mLimit = 10;
    private ArrayList<String> mPrerequisiteCoursesIds;

    public static Fragment newInstance(int columnCount, String trainingId) {
        TraineePrerequisiteCoursesFragment traineePrerequisiteCoursesFragment = new TraineePrerequisiteCoursesFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingId);
        traineePrerequisiteCoursesFragment.setArguments(args);
        return traineePrerequisiteCoursesFragment;
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
            mTrainingId = getArguments().getString(TRAINING_ID);
        }
        initializeRecyclerView(new ArrayList<AboutCourseExt>());
        getPrerequisiteCoursesIdsFromTraining(mTrainingId);
        return mBinding.getRoot();
    }

    private void getPrerequisiteCoursesIdsFromTraining(final String trainingId) {
        Observable.create(new ObservableOnSubscribe<Training>() {
            @Override
            public void subscribe(ObservableEmitter<Training> e) throws Exception {
                Training training = mHomeModel.getTrainingById(trainingId);
                e.onNext(training);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Training>() {
                    @Override
                    public void accept(Training training) throws Exception {
                        //mPrerequisiteCoursesIds = training.getPrerequisiteId();
                        if (mPrerequisiteCoursesIds != null && !mPrerequisiteCoursesIds.isEmpty()) {
                            getPrerequisiteCourses(mPrerequisiteCoursesIds, mSkip, mLimit);

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getPrerequisiteCourses(final ArrayList<String> courseIds, final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            if (skip > 0) {
                mBinding.bottomProgress.setVisibility(View.VISIBLE);
            } else {
                mBinding.progressBar.setVisibility(View.VISIBLE);
                mBinding.list.setVisibility(View.GONE);
            }

            Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourseExt>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<AboutCourseExt>> subscriber) throws Exception {
                    List<String> ids = getCourseIdsList(courseIds, skip, limit);
                    if (ids != null && !ids.isEmpty()) {
                        PrerequisiteCoursesPostData prerequisiteCoursesPostData = new PrerequisiteCoursesPostData();
                        prerequisiteCoursesPostData.setCourseIds(ids);
                        final Call<ArrayList<AboutCourseExt>> call = mNetworkModel.getPrerequisiteCourses(prerequisiteCoursesPostData);
                        Response<ArrayList<AboutCourseExt>> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            ArrayList<AboutCourseExt> aboutCourses = response.body();
                            Log.e("PrerequisiteCourses1--", "Successful");
                            subscriber.onNext(aboutCourses);
                        } else if (response.code() == 404) {
                            throw new Exception("No Prerequisite Courses found.");
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                            Response<ArrayList<AboutCourseExt>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<AboutCourseExt> aboutCourses = response.body();
                                Log.e("PrerequisiteCourses2--", "Successful");
                                subscriber.onNext(aboutCourses);
                            } else if ((response2.code() == 401)) {
                                startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                            } else if (response2.code() == 404) {
                                throw new Exception("No Prerequisite Courses found.");
                            } else {
                                Log.e("PrerequisiteCourses2--", "Failed");
                                throw new Exception("Unable to fetch Prerequisite Courses");
                            }
                        } else {
                            Log.e("PrerequisiteCourses1--", "Failed");
                            throw new Exception("Unable to fetch Prerequisite Courses");
                        }
                    } else {
                        subscriber.onNext(new ArrayList<AboutCourseExt>());
                    }

                    subscriber.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<AboutCourseExt>>() {
                        @Override
                        public void accept(ArrayList<AboutCourseExt> list) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            mSkip += list.size();
                            noResultFound(mSkip);
                            if (mCourseAdapter != null) {
                                mCourseAdapter.addValues(list);
                            }
                            if (list.size() < limit) {
                                mBinding.list.clearOnScrollListeners();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            if (throwable.getMessage().equals(getContext().getString(R.string.messageNoPrerequisiteCoursesFound))) {
                                noResultFound(skip);
                            } else {
                                unableToFetch(courseIds, skip, limit);
                            }
                        }
                    });
        } else {
            noInternet(courseIds, skip, limit);
        }

    }

    private List<String> getCourseIdsList(ArrayList<String> allIds, int skip, int limit) {
        if (allIds != null && allIds.size() > skip) {
            if (allIds.size() >= limit + skip) {
                return allIds.subList(skip, limit + skip);
            } else {
                return allIds.subList(skip, allIds.size());
            }
        }
        return null;
    }

    private void noInternet(final ArrayList<String> courseIds, final int skip, final int limit) {
        String message = getContext().getString(R.string.error_message_no_internet);
        if (skip > 0) {
            Snackbar.make(mBinding.list, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction((R.string.labelRetry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getPrerequisiteCourses(courseIds, skip, limit);
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
                    getPrerequisiteCourses(courseIds, skip, limit);
                }
            });
        }

    }

    private void unableToFetch(final ArrayList<String> courseIds, final int skip, final int limit) {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.course_gray);
        mBinding.textViewRetry.setText(getContext().getString(R.string.messagePrerequisiteCoursesFailed));
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPrerequisiteCourses(courseIds, skip, limit);
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
            mBinding.imageViewNoResult.setImageResource(R.drawable.course_gray);
            mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoPrerequisiteCoursesFound));
        }

    }

    private void initializeRecyclerView(ArrayList<AboutCourseExt> aboutCourses) {

        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
            mBinding.list.setLayoutManager(layoutManager);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        }

        mCourseAdapter = new CourseAdapter(aboutCourses, getContext());
        mBinding.list.setAdapter(mCourseAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            getPrerequisiteCourses(mPrerequisiteCoursesIds, mSkip, mLimit);

                        }
                    }

                }

            });
        }


    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
        private ArrayList<AboutCourseExt> mValues = new ArrayList<>();
        Context mContext;


        public void dispose() {
            if (mValues != null) {
                mValues.clear();
                mValues = null;
            }
        }

        public CourseAdapter(ArrayList<AboutCourseExt> aboutCourseExts, Context context) {
            this.mValues = aboutCourseExts;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            VideoListItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
            return new ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final AboutCourseExt video = mValues.get(position);
            holder.mBinding.imageViewThumbnail.setTag(video.getName());
            if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());

            String imagePath = video.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = video.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = video.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(video.getThumbnail().getThumb()).into(holder.mBinding.imageViewThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            Class objectClass = null;
            String type = "";
            int typeImage = R.drawable.digital_book;
            String typeExt = video.getMicroCourseType().toLowerCase();
            if (video.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (video.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
                typeImage = R.drawable.video_course;
            } else if (typeExt.contains("map")) {
                objectClass = ConceptMap.class;
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (typeExt.contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
                type = "Interactive Image";
                typeImage = R.drawable.interactive_image;
            } else if (typeExt.contains("video")) {
                objectClass = InteractiveVideo.class;
                type = "Interactive Video";
                typeImage = R.drawable.interactive_image;
            } else {
                type = "Pop Up";
                if (video.getPopUpType() != null && !TextUtils.isEmpty(video.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                    type = video.getPopUpType().getValue();
                    typeImage = R.drawable.popup;
                }
            }
            final Class finalObjectClass = objectClass;
            holder.mBinding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        mContext.startActivity(CourseDetailActivity.getStartActivityIntent(mContext, video.getObjectId(), finalObjectClass, ""));

                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), v);
                    }
                }
            });
            holder.mBinding.text.setText(video.getTitle());
            holder.mBinding.textViewVideoDuration.setVisibility(View.GONE);
            holder.mBinding.favoriteImg.setVisibility(View.GONE);
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addValues(ArrayList<AboutCourseExt> aboutCourseExts) {
            if (mValues != null) {
                mValues.addAll(aboutCourseExts);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mValues != null) {
                mValues.clear();
                notifyDataSetChanged();
            }
        }

        public void refresh(String objectId) {
            for (int i = 0; i < mValues.size(); i++) {
                if (mValues.get(i).getObjectId().equalsIgnoreCase(objectId)) {
                    mValues.get(i).setDocId("");
                    notifyItemChanged(i);
                    break;
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            VideoListItemBinding mBinding;

            ViewHolder(VideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                mBinding.imgShare.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_share:
                        shareVideo(mBinding.thumbnail.getTag().toString());
                        break;
                }
            }

            private void shareVideo(String path) {
                Toast.makeText(mContext, "in Share  mode " + path, Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(path);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                shareIntent.setType("video/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(shareIntent, "send"));
            }
        }

        public String convert(int seconds) {
            String strMinutes = "";
            String strSeconds = "";
            int minutes;
            int hours;
            int newSeconds;
            int secondMod = (seconds % 3600);
            hours = seconds / 3600;
            minutes = secondMod / 60;
            newSeconds = secondMod % 60;

            strMinutes = String.valueOf(minutes);
            strSeconds = String.valueOf(newSeconds);

            if (minutes >= 0 && minutes <= 9) {
                strMinutes = "0" + strMinutes;
            }
            if (newSeconds >= 0 && newSeconds <= 9) {
                strSeconds = "0" + strSeconds;

            }
            if (hours == 0) {
                return strMinutes + ":" + strSeconds;

            } else {
                return String.valueOf(hours) + ":" + strMinutes + ":" + strSeconds;
            }

        }
    }
}
