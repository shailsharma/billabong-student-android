package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfilePersonalBinding;
import in.securelearning.lil.android.base.dataobjects.ClassGroup;
import in.securelearning.lil.android.base.dataobjects.LocationCourse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;

public class StudentPersonalFragment extends Fragment {

    LayoutProfilePersonalBinding mBinding;
    private static final String CLASS_GROUP = "classGroup";
    private static final String ENROLLMENT_NUMBER = "enrollmentNumber";
    private static final String DOB = "dob";
    private static final String ADDRESS = "address";
    private static final String LOCATION = "location";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChaptersFragment.
     */
    public static StudentPersonalFragment newInstance(ClassGroup classGroup, String enrollmentNumber
            , String dob, String address, LocationCourse location) {
        StudentPersonalFragment fragment = new StudentPersonalFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_GROUP, classGroup);
        args.putString(ENROLLMENT_NUMBER, enrollmentNumber);
        args.putString(DOB, dob);
        args.putString(ADDRESS, address);
        args.putSerializable(LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_profile_personal, container, false);
        setPersonalData();
        return mBinding.getRoot();
    }

    private void setPersonalData() {
        if (getArguments() != null) {
            ClassGroup classGroup = (ClassGroup) getArguments().getSerializable(CLASS_GROUP);
            String enrollmentNumber = getArguments().getString(ENROLLMENT_NUMBER);
            String address = getArguments().getString(ADDRESS);
            String dob = getArguments().getString(DOB);
            LocationCourse location = (LocationCourse) getArguments().getSerializable(LOCATION);

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

        }
    }
}
