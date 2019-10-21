package in.securelearning.lil.android.profile.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfileParentBinding;
import in.securelearning.lil.android.home.InjectorHome;

public class StudentParentFragment extends Fragment {

    LayoutProfileParentBinding mBinding;
    private static final String FATHER_NAME = "fatherName";
    private static final String FATHER_MOBILE = "fatherMobile";
    private static final String FATHER_EMAIL = "fatherEmail";
    private static final String MOTHER_NAME = "motherName";
    private static final String MOTHER_MOBILE = "motherMobile";
    private static final String MOTHER_EMAIL = "motherEmail";

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @return A new instance of activity ChaptersFragment.
     */
    public static StudentParentFragment newInstance(String fatherName, String fatherMobileNo, String fatherEmail,
                                                    String motherName, String motherMobileNo, String motherEmail) {
        StudentParentFragment fragment = new StudentParentFragment();
        Bundle args = new Bundle();
        args.putString(FATHER_NAME, fatherName);
        args.putString(FATHER_MOBILE, fatherMobileNo);
        args.putString(FATHER_EMAIL, fatherEmail);
        args.putString(MOTHER_NAME, motherName);
        args.putString(MOTHER_MOBILE, motherMobileNo);
        args.putString(MOTHER_EMAIL, motherEmail);
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_profile_parent, container, false);
        setParentData();

        return mBinding.getRoot();
    }

    private void setParentData() {
        if (getArguments() != null) {

            String fatherName = getArguments().getString(FATHER_NAME);
            String fatherMobileNo = getArguments().getString(FATHER_MOBILE);
            String fatherEmail = getArguments().getString(FATHER_EMAIL);
            String motherName = getArguments().getString(MOTHER_NAME);
            String motherMobileNo = getArguments().getString(MOTHER_MOBILE);
            String motherEmail = getArguments().getString(MOTHER_EMAIL);

            if (!TextUtils.isEmpty(fatherName)) {
                mBinding.textViewFatherName.setText(fatherName);
            } else {
                mBinding.linearLayoutFatherName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(fatherMobileNo)) {
                mBinding.textViewFatherMobile.setText(fatherMobileNo);
            } else {
                mBinding.linearLayoutFatherMobile.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(fatherEmail)) {
                mBinding.textViewFatherEmail.setText(fatherEmail);
            } else {
                mBinding.linearLayoutFatherEmail.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(motherName)) {
                mBinding.textViewMotherName.setText(motherName);
            } else {
                mBinding.linearLayoutMotherName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(motherMobileNo)) {
                mBinding.textViewMotherMobile.setText(motherMobileNo);
            } else {
                mBinding.linearLayoutMotherMobile.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(motherEmail)) {
                mBinding.textViewMotherEmail.setText(motherEmail);
            } else {
                mBinding.linearLayoutMotherEmail.setVisibility(View.GONE);
            }

            boolean isFatherDataVisible = false, isMotherDataVisible = false;
            if (TextUtils.isEmpty(fatherName) && TextUtils.isEmpty(fatherMobileNo) && TextUtils.isEmpty(fatherEmail)) {
                mBinding.layoutParentFather.setVisibility(View.GONE);
            } else {
                isFatherDataVisible = true;
                mBinding.layoutParentFather.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(motherName) && TextUtils.isEmpty(motherMobileNo) && TextUtils.isEmpty(motherEmail)) {
                mBinding.layoutParentMother.setVisibility(View.GONE);
            } else {
                isMotherDataVisible = true;
                mBinding.layoutParentMother.setVisibility(View.VISIBLE);
            }

            if (!isFatherDataVisible && !isMotherDataVisible) {
                mBinding.layoutParentFather.setVisibility(View.GONE);
                mBinding.layoutParentMother.setVisibility(View.GONE);
                mBinding.textViewError.setVisibility(View.VISIBLE);
            }

            if (isFatherDataVisible && isMotherDataVisible) {
                mBinding.viewDivider.setVisibility(View.VISIBLE);
            } else {
                mBinding.viewDivider.setVisibility(View.GONE);
            }

        }
    }
}
