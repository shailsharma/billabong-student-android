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
import in.securelearning.lil.android.home.InjectorHome;

public class UserProfilePersonalFragment extends Fragment {

    LayoutProfilePersonalBinding mBinding;
    private static String ADHARNUMBER = "adhar_number";
    private static String CLASSGROUP = "class_group";
    private static String ENROLLMENTNUMBER = "enrolment";
    private static String DOB = "dob";
    private static String BLOODGROUP = "blood_group";
    private static String ADDRESS = "address";
    private static String INTEREST = "interest";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChaptersFragment.
     */
    public static UserProfilePersonalFragment newInstance(String mAdharNumber,String mClassGroup,String mEnrollmentNo
    ,String mDob,String mBloodGroup,String mAddress,String mInterest) {
        UserProfilePersonalFragment fragment = new UserProfilePersonalFragment();
        Bundle args = new Bundle();
        args.putString(ADHARNUMBER, mAdharNumber);
        args.putString(CLASSGROUP, mClassGroup);
        args.putString(ENROLLMENTNUMBER, mEnrollmentNo);
        args.putString(DOB, mDob);
        args.putString(BLOODGROUP, mBloodGroup);
        args.putString(ADDRESS, mAddress);
        args.putString(INTEREST, mInterest);
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

        if (getArguments() != null) {
            String mAdharNumber = getArguments().getString(ADHARNUMBER);
            String mClassGroup = getArguments().getString(CLASSGROUP);
            String mEnrollmentNumber = getArguments().getString(ENROLLMENTNUMBER);
            String mAddress = getArguments().getString(ADDRESS);
            String mDob = getArguments().getString(DOB);
            String mBloodGroup = getArguments().getString(BLOODGROUP);
            String mInterest = getArguments().getString(INTEREST);
            if(TextUtils.isEmpty(mAdharNumber))
            {
                mBinding.linearLayoutAadhaarNumber.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewAadhaarNumber.setText(mAdharNumber);
            }
            if(TextUtils.isEmpty(mClassGroup))
            {
                mBinding.linearLayoutClassGroup.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewClassGroup.setText(mClassGroup);
            }
            if(TextUtils.isEmpty(mEnrollmentNumber))
            {
                mBinding.linearLayoutEnrollmentCode.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewEnrollmentCode.setText(mEnrollmentNumber);
            }
            if(TextUtils.isEmpty(mAddress))
            {
                mBinding.linearLayoutAddress.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewAddress.setText(mAddress);
            }
            if(TextUtils.isEmpty(mDob))
            {
                mBinding.linearLayoutDateOfBirth.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewDateOfBirth.setText(mDob);
            }
            if(TextUtils.isEmpty(mBloodGroup))
            {
                mBinding.linearLayoutBloodGroup.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewBloodGroup.setText(mBloodGroup);
            }
            if(TextUtils.isEmpty(mInterest))
            {
                mBinding.linearLayoutInterest.setVisibility(View.GONE);
            }
            else
            {
                mBinding.textViewInterest.setText(mInterest);
            }
        }
        return mBinding.getRoot();
    }
}
