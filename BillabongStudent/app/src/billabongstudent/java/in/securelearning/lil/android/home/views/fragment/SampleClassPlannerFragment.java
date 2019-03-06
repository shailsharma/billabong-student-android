package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import in.securelearning.lil.android.app.databinding.LayoutClassPlannerSampleBinding;
import in.securelearning.lil.android.app.databinding.LayoutStudyReferenceItemBinding;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

/**
 * Created by Chaitendra on 10-Feb-18.
 */

public class SampleClassPlannerFragment extends Fragment {
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    AssignmentTeacherModel mAssignmentTeacherModel;

    LayoutClassPlannerSampleBinding mBinding;
    private int mSkip = 0;
    private int mLimit = 10;

    public static SampleClassPlannerFragment newInstance() {
        SampleClassPlannerFragment sampleClassPlannerFragment = new SampleClassPlannerFragment();
        return sampleClassPlannerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_class_planner_sample, container, false);
        setCourseAndLessonPlanRecyclerView();
        setPrescribedResourcesRecyclerView();
        setAssessmentsRecyclerView();
        setReferenceResourcesRecyclerView();
        return mBinding.getRoot();
    }

    private void setCourseAndLessonPlanRecyclerView() {

        AboutCourseExt aboutCourseExt1 = new AboutCourseExt();
        aboutCourseExt1.setObjectId("59afe0137c105a1c00aeaf53");
        aboutCourseExt1.setTitle("Control and Coordination");
        aboutCourseExt1.setName("digitalbook");
        aboutCourseExt1.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1504697933/e3x0mssolixcjyobht1j.jpg");

//        AboutCourseExt aboutCourseExt2 = new AboutCourseExt();
//        aboutCourseExt2.setObjectId("5a7d13eb39682c120058b73a");
//        aboutCourseExt2.setTitle("Basic parts of the cell: Cell membrane and wall");
//        aboutCourseExt2.setName("digitalbook");
//        aboutCourseExt2.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518146412/hcvsn2r9oevn0m1omnvj.jpg");
//
//        AboutCourseExt aboutCourseExt3 = new AboutCourseExt();
//        aboutCourseExt3.setObjectId("5a7fcfa439682c12005932a0");
//        aboutCourseExt3.setTitle("Session :- 2 Protoplasm and Nucleus");
//        aboutCourseExt3.setName("digitalbook");
//        aboutCourseExt3.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518325656/y2fht2lpi2xxjgl5tsqe.jpg");
//
//        AboutCourseExt aboutCourseExt4 = new AboutCourseExt();
//        aboutCourseExt4.setObjectId("5a7fd23e39682c12005932b9");
//        aboutCourseExt4.setTitle("Session :- 3 Cell Organelles");
//        aboutCourseExt4.setName("digitalbook");
//        aboutCourseExt4.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518326317/s0ha6ay7h5udcdwhcs9g.jpg");
//
//        AboutCourseExt aboutCourseExt5 = new AboutCourseExt();
//        aboutCourseExt5.setObjectId("/5a7fd2a639682c12005932ea");
//        aboutCourseExt5.setTitle("Session :- 4 Cell Organelles");
//        aboutCourseExt5.setName("digitalbook");
//        aboutCourseExt5.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518326432/sfrnb2sjc09wd4kttk9y.jpg");
//
//        AboutCourseExt aboutCourseExt6 = new AboutCourseExt();
//        aboutCourseExt6.setObjectId("5a7fd35939682c120059331b");
//        aboutCourseExt6.setTitle("Session :- 5 Golgi Bodies, Lysosome , Plastid and ");
//        aboutCourseExt6.setName("digitalbook");
//        aboutCourseExt6.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518326595/fizwqdwqdl7ixroyab5a.jpg");

//
//        AboutCourseExt aboutCourseExt7 = new AboutCourseExt();
//        aboutCourseExt7.setObjectId("5b9799a0186a96001c189f21");
//        aboutCourseExt7.setTitle("The Cell");
//        aboutCourseExt7.setName("rapidLearning");
//        aboutCourseExt7.setThumbnailUrl("https://lilcdn.azureedge.net/lil-upload/1536624000000/livingcellss_a82ZSJh_KMHuX-a68ZSyJYYfSu7.jpg");

        ArrayList<AboutCourseExt> list = new ArrayList<>();
        list.add(aboutCourseExt1);
//        list.add(aboutCourseExt2);O
//        list.add(aboutCourseExt3);
//        list.add(aboutCourseExt4);
//        list.add(aboutCourseExt5);
//        list.add(aboutCourseExt6);
        //  list.add(aboutCourseExt7);

        mBinding.recyclerViewCourseAndLessonPlanner.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        CourseAndLessonPlanAdapter courseAndLessonPlanAdapter = new CourseAndLessonPlanAdapter(list);
        mBinding.recyclerViewCourseAndLessonPlanner.setAdapter(courseAndLessonPlanAdapter);
    }

    private void setPrescribedResourcesRecyclerView() {

        SampleObject sampleObject1 = new SampleObject();
        sampleObject1.setId("59b0eca87c105a1c00aeb9ed");
        sampleObject1.setName("popup");
        sampleObject1.setThumbUrl("http://res.cloudinary.com/learnindialearn/image/upload/v1504766908/kkzt8vc09j6fjkrrr1bt.jpg");
        sampleObject1.setTitle("Sensory Organs - Tongue");

        SampleObject sampleObject2 = new SampleObject();
        sampleObject2.setId("59b8b2cf1bafe71b008f135b");
        sampleObject2.setName("popup");
        sampleObject2.setThumbUrl("http://res.cloudinary.com/learnindialearn/image/upload/v1505276566/z0ktrzz8ej6haueduven.jpg");
        sampleObject2.setTitle("Facts on Nervous System");

        SampleObject sampleObject3 = new SampleObject();
        sampleObject3.setId("59b6679b1bafe71b008eec09");
        sampleObject3.setName("popup");
        sampleObject3.setThumbUrl("http://res.cloudinary.com/learnindialearn/image/upload/v1505126091/j4fvy4edtgrlhxle9s2i.jpg");
        sampleObject3.setTitle("Plants Response to Light");

        SampleObject sampleObject4 = new SampleObject();
        sampleObject4.setId("59b90d651bafe71b008f2010");
        sampleObject4.setName("popup");
        sampleObject4.setThumbUrl("http://res.cloudinary.com/learnindialearn/image/upload/v1505299632/airefecow7eivkp4b9rm.jpg");
        sampleObject4.setTitle("Endocrine glands");

//        SampleObject sampleObject5 = new SampleObject();
//        sampleObject5.setId("5a7c278839682c1200588ba7");
//        sampleObject5.setName("popup");
//        sampleObject5.setThumbUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518085924/tsplkc208eb8xishl0zc.jpg");
//        sampleObject5.setTitle("Activity on Structure of Cell");
//
//        SampleObject sampleObject6 = new SampleObject();
//        sampleObject6.setId("59dced2c84111b1a00faddcc");
//        sampleObject6.setName("popup");
//        sampleObject6.setThumbUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1507650771/eog1zpyfmwgtt41gat1n.jpg");
//        sampleObject6.setTitle("Facts About Cells");

        ArrayList<SampleObject> list = new ArrayList<>();
        list.add(sampleObject1);
        list.add(sampleObject2);
        list.add(sampleObject3);
        list.add(sampleObject4);
//        list.add(sampleObject5);
//        list.add(sampleObject6);
        mBinding.recyclerViewPrescribedResources.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        PrescribedResourcesAdapter prescribedResourcesAdapter = new PrescribedResourcesAdapter(list);
        mBinding.recyclerViewPrescribedResources.setAdapter(prescribedResourcesAdapter);
    }

    private void setAssessmentsRecyclerView() {


        SampleObject sampleObject2 = new SampleObject();
        sampleObject2.setId("5ae30e4081215e1300a76f09");
        sampleObject2.setName("quiz");
        sampleObject2.setThumbUrl("https://lil.azureedge.net/lil-upload/1524787200000/Quiz_Image_a59ZrJWFXRKxpM-a88ZHkiXRtx6f.jpg");
        sampleObject2.setTitle("Control and Coordination Quiz :- 1");

        SampleObject sampleObject3 = new SampleObject();
        sampleObject3.setId("5ae32ead81215e1300a77088");
        sampleObject3.setName("quiz");
        sampleObject3.setThumbUrl("https://res.cloudinary.com/dra5yicjk/image/upload/v1509791498/taxqgdeifdi6tpdtbsy2.jpg");
        sampleObject3.setTitle("Control and Coordination Quiz :- 2");

        SampleObject sampleObject4 = new SampleObject();
        sampleObject4.setId("5ae32fc781215e1300a77097");
        sampleObject4.setName("quiz");
        sampleObject4.setThumbUrl("https://res.cloudinary.com/dra5yicjk/image/upload/v1509791498/taxqgdeifdi6tpdtbsy2.jpg");
        sampleObject4.setTitle("Control and Coordination Quiz :- 3");

        SampleObject sampleObject5 = new SampleObject();
        sampleObject5.setId("5ae3314d81215e1300a770a2");
        sampleObject5.setName("quiz");
        sampleObject5.setThumbUrl("https://res.cloudinary.com/dra5yicjk/image/upload/v1509791498/taxqgdeifdi6tpdtbsy2.jpg");
        sampleObject5.setTitle("Control and Coordination Quiz :- 4");

        SampleObject sampleObject6 = new SampleObject();
        sampleObject6.setId("5ae43df781215e1300a771bf");
        sampleObject6.setName("quiz");
        sampleObject6.setThumbUrl("https://lil.azureedge.net/lil-upload/1524873600000/Quiz_sign_a28ZS1P0p3ZpG-a22ZH1lvCTnb6G.jpg");
        sampleObject6.setTitle("Quiz: Control and Coordination");

        SampleObject sampleObject7 = new SampleObject();
        sampleObject7.setId("5ae43e9b81215e1300a771c3");
        sampleObject7.setName("quiz");
        sampleObject7.setThumbUrl("https://lil.azureedge.net/lil-upload/1524873600000/Quiz_sign_a93ZHk89RhZaM-a12ZHkeIcRn-TM.jpg");
        sampleObject7.setTitle("Quiz");

        SampleObject sampleObject8 = new SampleObject();
        sampleObject8.setId("5ae43ffe81215e1300a771cf");
        sampleObject8.setName("quiz");
        sampleObject8.setThumbUrl("https://lil.azureedge.net/lil-upload/1524873600000/Quiz_sign_a95ZHyYlgTbTz-a97ZS1eFxg6bTz.jpg");
        sampleObject8.setTitle("Living World");

        ArrayList<SampleObject> list = new ArrayList<>();

        list.add(sampleObject2);
        list.add(sampleObject3);
        list.add(sampleObject4);
        list.add(sampleObject5);
        list.add(sampleObject6);
        list.add(sampleObject7);
        list.add(sampleObject8);

        mBinding.recyclerViewAssessments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        AssessmentsAdapter assessmentsAdapter = new AssessmentsAdapter(list);
        mBinding.recyclerViewAssessments.setAdapter(assessmentsAdapter);

    }

    private void setReferenceResourcesRecyclerView() {
        ArrayList<FavouriteResource> list = new ArrayList<>();
        FavouriteResource favouriteResource1 = new FavouriteResource();
        favouriteResource1.setName("5pN5e-ZhkNg");
        favouriteResource1.setUrlThumbnail("https://i.ytimg.com/vi/5pN5e-ZhkNg/hqdefault.jpg");
        favouriteResource1.setTitle("Types of tropisms - Control and Coordination");

        FavouriteResource favouriteResource2 = new FavouriteResource();
        favouriteResource2.setName("7grvMQjOM5k");
        favouriteResource2.setUrlThumbnail("https://i.ytimg.com/vi/7grvMQjOM5k/hqdefault.jpg");
        favouriteResource2.setTitle("Involuntary Muscles");

        FavouriteResource favouriteResource3 = new FavouriteResource();
        favouriteResource3.setName("w8_4ufWN1C0");
        favouriteResource3.setUrlThumbnail("https://i.ytimg.com/vi/w8_4ufWN1C0/hqdefault.jpg");
        favouriteResource3.setTitle("Voluntary Muscle");

        FavouriteResource favouriteResource4 = new FavouriteResource();
        favouriteResource4.setName("Nn2RHLWST-k");
        favouriteResource4.setUrlThumbnail("https://i.ytimg.com/vi/Nn2RHLWST-k/hqdefault.jpg");
        favouriteResource4.setTitle("What is a Reflex Arc");

        FavouriteResource favouriteResource5 = new FavouriteResource();
        favouriteResource5.setName("44B0ms3XPKU");
        favouriteResource5.setUrlThumbnail("https://i.ytimg.com/vi/44B0ms3XPKU/hqdefault.jpg");
        favouriteResource5.setTitle("The Nervous System");

        list.add(favouriteResource1);
        list.add(favouriteResource2);
        list.add(favouriteResource3);
        list.add(favouriteResource4);
        list.add(favouriteResource5);

        mBinding.recyclerViewReferenceResources.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ReferenceResourcesAdapter referenceResourcesAdapter = new ReferenceResourcesAdapter(list);
        mBinding.recyclerViewReferenceResources.setAdapter(referenceResourcesAdapter);
    }

    private class CourseAndLessonPlanAdapter extends RecyclerView.Adapter<CourseAndLessonPlanAdapter.ViewHolder> {
        ArrayList<AboutCourseExt> mList;

        public CourseAndLessonPlanAdapter(ArrayList<AboutCourseExt> list) {
            mList = list;
        }

        @Override
        public CourseAndLessonPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutStudyReferenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_study_reference_item, parent, false);
            return new CourseAndLessonPlanAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CourseAndLessonPlanAdapter.ViewHolder holder, int position) {
            final AboutCourseExt object = mList.get(position);
            holder.mBinding.textViewTitle.setText(object.getTitle());
            String typeExt = object.getName();
            Class objectClass = null;
            if (typeExt.equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (typeExt.equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (typeExt.equalsIgnoreCase("conceptmap")) {
                objectClass = ConceptMap.class;
            } else if (typeExt.equalsIgnoreCase("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (typeExt.equalsIgnoreCase("video")) {
                objectClass = InteractiveVideo.class;
            } else if (typeExt.equalsIgnoreCase("rapidLearning")) {
                objectClass = MicroLearningCourse.class;
            } else {
                if (object.getPopUpType() != null && !TextUtils.isEmpty(object.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                }
            }

            String imagePath = object.getThumbnailUrl();

            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
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

            final Class finalObjectClass = objectClass;

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(getContext(), object.getObjectId()));
                        } else if (finalObjectClass.equals(VideoCourse.class) || finalObjectClass.equals(InteractiveVideo.class) || object.getTotalResourceCount().getVideoCourses() > 0 || object.getTotalResourceCount().getVideos() > 0) {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), object.getObjectId(), object.getMetaInformation().getSubject().getId(), object.getMetaInformation().getTopic().getId(), finalObjectClass, "", false);
                        } else {
                            WebPlayerLiveActivity.startWebPlayer(getContext(), object.getObjectId(), object.getMetaInformation().getSubject().getId(), object.getMetaInformation().getTopic().getId(), finalObjectClass, "", false, true);
                        }
                    } else {
                        ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutStudyReferenceItemBinding mBinding;

            public ViewHolder(LayoutStudyReferenceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class PrescribedResourcesAdapter extends RecyclerView.Adapter<PrescribedResourcesAdapter.ViewHolder> {
        ArrayList<SampleObject> mList;

        public PrescribedResourcesAdapter(ArrayList<SampleObject> list) {
            mList = list;
        }

        @Override
        public PrescribedResourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutStudyReferenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_study_reference_item, parent, false);
            return new PrescribedResourcesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(PrescribedResourcesAdapter.ViewHolder holder, int position) {
            final SampleObject object = mList.get(position);
            holder.mBinding.textViewTitle.setText(object.getTitle());
            String imagePath = object.getThumbUrl();

            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
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

            String typeExt = object.getName();
            Class objectClass = null;
            if (typeExt.equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (typeExt.equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (typeExt.equalsIgnoreCase("conceptmap")) {
                objectClass = ConceptMap.class;
            } else if (typeExt.equalsIgnoreCase("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (typeExt.equalsIgnoreCase("interactivevideo")) {
                objectClass = InteractiveVideo.class;
            } else if (typeExt.equalsIgnoreCase("popup")) {
                objectClass = PopUps.class;
            }

            final Class finalObjectClass = objectClass;
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        if (finalObjectClass.equals(FavouriteResource.class)) {
                            FavouriteResource favouriteResource = new FavouriteResource();
                            favouriteResource.setName(object.getId());
                            favouriteResource.setUrlThumbnail(object.getThumbUrl());
                            favouriteResource.setTitle(object.getTitle());
                            getContext().startActivity(PlayYouTubeFullScreenActivity.getStartIntent(getContext(), favouriteResource, false));
                        } else {
                            if (finalObjectClass.equals(VideoCourse.class) || finalObjectClass.equals(InteractiveVideo.class)) {
                                WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), object.getId(), "", "", finalObjectClass, "", false);
                            } else {
                                WebPlayerLiveActivity.startWebPlayer(getContext(), object.getId(), "", "", finalObjectClass, "", false, true);
                            }
                        }

                    } else {
                        ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutStudyReferenceItemBinding mBinding;

            public ViewHolder(LayoutStudyReferenceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class AssessmentsAdapter extends RecyclerView.Adapter<AssessmentsAdapter.ViewHolder> {
        ArrayList<SampleObject> mList;

        public AssessmentsAdapter(ArrayList<SampleObject> list) {
            mList = list;
        }

        @Override
        public AssessmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutStudyReferenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_study_reference_item, parent, false);
            return new AssessmentsAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(AssessmentsAdapter.ViewHolder holder, int position) {
            final SampleObject object = mList.get(position);
            holder.mBinding.textViewTitle.setText(object.getTitle());
            setAssignmentThumbnail(object.getThumbUrl(), holder.mBinding.imageViewThumbnail);
            final Class finalObjectClass = Quiz.class;
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {

                        WebPlayerLiveActivity.startWebPlayer(getContext(), object.getId(), "", "", finalObjectClass, "", false, true);

                    } else {
                        ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                    }
                }
            });
        }

        private void setAssignmentThumbnail(String thumbnailPath, ImageView imageView) {
            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getContext()).load(thumbnailPath).resize(600, 440).centerInside().into(imageView);
                } else {
                    Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(imageView);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutStudyReferenceItemBinding mBinding;

            public ViewHolder(LayoutStudyReferenceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class ReferenceResourcesAdapter extends RecyclerView.Adapter<ReferenceResourcesAdapter.ViewHolder> {
        ArrayList<FavouriteResource> mList;

        public ReferenceResourcesAdapter(ArrayList<FavouriteResource> list) {
            mList = list;
        }

        @Override
        public ReferenceResourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutStudyReferenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_study_reference_item, parent, false);
            return new ReferenceResourcesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ReferenceResourcesAdapter.ViewHolder holder, int position) {
            final FavouriteResource favouriteResource = mList.get(position);
            holder.mBinding.textViewTitle.setText(favouriteResource.getTitle());
            try {
                Picasso.with(getContext()).load(favouriteResource.getUrlThumbnail()).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(getContext()).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            }

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        getContext().startActivity(PlayYouTubeFullScreenActivity.getStartIntent(getContext(), favouriteResource, false));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), view);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutStudyReferenceItemBinding mBinding;

            public ViewHolder(LayoutStudyReferenceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    public class SampleObject {
        private String mId;
        private String mTitle;
        private String mThumbUrl;
        private String mName;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getThumbUrl() {
            return mThumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            mThumbUrl = thumbUrl;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }
    }

}
