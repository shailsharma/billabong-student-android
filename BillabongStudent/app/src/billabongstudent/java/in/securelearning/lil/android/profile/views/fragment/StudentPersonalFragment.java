package in.securelearning.lil.android.profile.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfilePersonalBinding;
import in.securelearning.lil.android.base.dataobjects.ClassGroup;
import in.securelearning.lil.android.base.dataobjects.LocationCourse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.profile.dataobject.UserInterest;
import in.securelearning.lil.android.profile.dataobject.UserInterestParent;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.profile.views.activity.StudentProfileCoCurricularActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileGoalActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileHobbyActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileSubjectActivity;
import in.securelearning.lil.android.profile.views.adapter.UserInterestAdapter;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentPersonalFragment extends Fragment {

    @Inject
    ProfileModel mProfileModel;

    LayoutProfilePersonalBinding mBinding;
    private static final String CLASS_GROUP = "classGroup";
    private static final String ENROLLMENT_NUMBER = "enrollmentNumber";
    private static final String ADMISSION_DATE = "admissionDate";
    private static final String DOB = "dob";
    private static final String ADDRESS = "address";
    private static final String LOCATION = "location";
    private static final String GRADE_ID = "gradeId";
    private static final String USER_INTEREST = "userInterest";

    private Context mContext;
    private String mGradeId, mUserInterestId;
    private UserInterestParent mUserInterestParent;

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @return A new instance of activity ChaptersFragment.
     */
    public static StudentPersonalFragment newInstance(ClassGroup classGroup, String enrollmentNumber,
                                                      String admissionDate, String dob, String address,
                                                      LocationCourse location, String gradeId,
                                                      UserInterestParent userInterest) {
        StudentPersonalFragment fragment = new StudentPersonalFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_GROUP, classGroup);
        args.putString(ENROLLMENT_NUMBER, enrollmentNumber);
        args.putString(ADMISSION_DATE, admissionDate);
        args.putString(DOB, dob);
        args.putString(ADDRESS, address);
        args.putSerializable(LOCATION, location);
        args.putString(GRADE_ID, gradeId);
        args.putSerializable(USER_INTEREST, userInterest);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_profile_personal, container, false);

        setPersonalData();
        initializeViewsAndListeners();
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initializeViewsAndListeners() {

        mBinding.layoutAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (mUserInterestParent != null) {
                        mContext.startActivity(StudentProfileGoalActivity.getStartIntent(getContext(), mUserInterestId, mUserInterestParent.getDailyTarget()));
                    }
                } else {
                    GeneralUtils.showToastShort(mContext, getString(R.string.error_message_no_internet));
                }

            }
        });

        mBinding.layoutAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    getStudentInterestData(ConstantUtil.PROFILE_ACADEMIC_SUBJECT);
                } else {
                    GeneralUtils.showToastShort(mContext, getString(R.string.error_message_no_internet));
                }

            }
        });

        mBinding.layoutAddCoCurricularActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    getStudentInterestData(ConstantUtil.PROFILE_CO_CURRICULAR_ACTIVITY);
                } else {
                    GeneralUtils.showToastShort(mContext, getString(R.string.error_message_no_internet));
                }

            }
        });

        mBinding.layoutAddHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    getStudentInterestData(ConstantUtil.PROFILE_HOBBY);
                } else {
                    GeneralUtils.showToastShort(mContext, getString(R.string.error_message_no_internet));
                }

            }
        });

    }

    private void setPersonalData() {
        if (getArguments() != null) {

            ClassGroup classGroup = (ClassGroup) getArguments().getSerializable(CLASS_GROUP);
            String enrollmentNumber = getArguments().getString(ENROLLMENT_NUMBER);
            String admissionDate = getArguments().getString(ADMISSION_DATE);
            String dob = getArguments().getString(DOB);
            String address = getArguments().getString(ADDRESS);
            LocationCourse location = (LocationCourse) getArguments().getSerializable(LOCATION);

            mGradeId = getArguments().getString(GRADE_ID);
            mUserInterestParent = (UserInterestParent) getArguments().getSerializable(USER_INTEREST);


            if (classGroup != null && !TextUtils.isEmpty(classGroup.getName())) {
                mBinding.textViewClassGroup.setText(classGroup.getName());
            } else {
                mBinding.linearLayoutClassGroup.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(enrollmentNumber)) {
                mBinding.textViewEnrollmentCode.setText(enrollmentNumber);
            } else {
                mBinding.linearLayoutEnrollmentCode.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(admissionDate)) {
                mBinding.textViewDateOfAdmission.setText(DateUtils.getMonthDayYearStringFromISODateString(admissionDate));
            } else {
                mBinding.linearLayoutDateOfAdmission.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(address)) {
                mBinding.textViewAddress.setText(address);
            } else if (location != null && !TextUtils.isEmpty(location.getAddress())) {
                mBinding.textViewAddress.setText(location.getAddress());

            } else {
                mBinding.linearLayoutAddress.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(dob)) {
                mBinding.textViewDateOfBirth.setText(DateUtils.getMonthDayYearStringFromISODateString(dob));
            } else {
                mBinding.linearLayoutDateOfBirth.setVisibility(View.GONE);
            }

            if (mUserInterestParent != null) {
                if (!TextUtils.isEmpty(mUserInterestParent.getId())) {
                    mUserInterestId = mUserInterestParent.getId();
                }

                //Goal
                if (mUserInterestParent.getDailyTarget() > 0) {

                    mBinding.imageViewAddGoal.setImageDrawable(getResources().getDrawable(R.drawable.action_edit_w));

                    mBinding.textViewGoal.setText(String.valueOf(mUserInterestParent.getDailyTarget()));
                    mBinding.textViewGoal.setVisibility(View.VISIBLE);

                } else {
                    mBinding.textViewGoal.setVisibility(View.GONE);
                }

                //Academic Subjects
                if (mUserInterestParent.getAcademicSubjects() != null && !mUserInterestParent.getAcademicSubjects().isEmpty()) {

                    mBinding.imageViewAddSubject.setImageDrawable(getResources().getDrawable(R.drawable.action_edit_w));

                    mBinding.recyclerViewSubject.setVisibility(View.VISIBLE);
                    setAcademicSubjects(mUserInterestParent.getAcademicSubjects());

                } else {
                    mBinding.recyclerViewSubject.setVisibility(View.GONE);
                }

                //Co-curricular Activity
                if (mUserInterestParent.getCoCurricularActivities() != null && !mUserInterestParent.getCoCurricularActivities().isEmpty()) {

                    mBinding.imageViewAddCoCurricularActivity.setImageDrawable(getResources().getDrawable(R.drawable.action_edit_w));

                    mBinding.recyclerViewCoCurricularActivity.setVisibility(View.VISIBLE);
                    setCoCurricularActivities(mUserInterestParent.getCoCurricularActivities());

                } else {
                    mBinding.recyclerViewCoCurricularActivity.setVisibility(View.GONE);
                }

                //Hobby
                if (mUserInterestParent.getHobbies() != null && !mUserInterestParent.getHobbies().isEmpty()) {

                    mBinding.imageViewAddHobby.setImageDrawable(getResources().getDrawable(R.drawable.action_edit_w));

                    mBinding.recyclerViewHobby.setVisibility(View.VISIBLE);
                    setHobbies(mUserInterestParent.getHobbies());

                } else {
                    mBinding.recyclerViewHobby.setVisibility(View.GONE);
                }
            }

        }
    }

    //Academic Subjects
    private void setAcademicSubjects(ArrayList<UserInterest> list) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext);
        mBinding.recyclerViewSubject.setLayoutManager(layoutManager);
        mBinding.recyclerViewSubject.setAdapter(new UserInterestAdapter(mContext, list));
    }

    //Co-curricular Activities
    private void setCoCurricularActivities(ArrayList<UserInterest> list) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext);
        mBinding.recyclerViewCoCurricularActivity.setLayoutManager(layoutManager);
        mBinding.recyclerViewCoCurricularActivity.setAdapter(new UserInterestAdapter(mContext, list));
    }

    //Hobbies
    private void setHobbies(ArrayList<UserInterest> list) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext);
        mBinding.recyclerViewHobby.setLayoutManager(layoutManager);
        mBinding.recyclerViewHobby.setAdapter(new UserInterestAdapter(mContext, list));
    }


    /**
     * To fetch Student interest Data list according to interest type
     *
     * @param interestType => Academic Subject: 2,
     *                     Co Curricular Activities: 3,
     *                     Hobby: 4
     */
    @SuppressLint("CheckResult")
    private void getStudentInterestData(final int interestType) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);

            mProfileModel.fetchStudentInterestData(mGradeId, interestType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<UserInterest>>() {
                        @Override
                        public void accept(ArrayList<UserInterest> list) throws Exception {

                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            if (list != null && !list.isEmpty()) {

                                if (interestType == ConstantUtil.PROFILE_ACADEMIC_SUBJECT) {

                                    mContext.startActivity(StudentProfileSubjectActivity.getStartIntent(getContext(), list,
                                            mUserInterestId, mUserInterestParent.getAcademicSubjects()));

                                } else if (interestType == ConstantUtil.PROFILE_CO_CURRICULAR_ACTIVITY) {

                                    mContext.startActivity(StudentProfileCoCurricularActivity.getStartIntent(getContext(), list,
                                            mUserInterestId, mUserInterestParent.getCoCurricularActivities()));

                                } else if (interestType == ConstantUtil.PROFILE_HOBBY) {

                                    mContext.startActivity(StudentProfileHobbyActivity.getStartIntent(getContext(), list, mUserInterestId, mUserInterestParent.getHobbies()));

                                }

                            } else {
                                GeneralUtils.showToastShort(mContext, getString(R.string.messageNoDataFound));
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            GeneralUtils.showToastShort(mContext, getString(R.string.error_something_went_wrong));
                        }
                    });

        } else {
            GeneralUtils.showToastShort(mContext, getString(R.string.error_message_no_internet));
        }
    }

}
