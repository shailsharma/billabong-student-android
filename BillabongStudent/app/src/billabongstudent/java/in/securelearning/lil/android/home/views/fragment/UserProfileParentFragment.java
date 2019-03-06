package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfileParentBinding;
import in.securelearning.lil.android.home.InjectorHome;

public class UserProfileParentFragment extends Fragment {

    LayoutProfileParentBinding mBinding;
    private static String FATHERNAME = "Fname";
    private static String FATHERMOBILE = "Fmobilno";
    private static String FATHEREMAIL = "Femail";
    private static String MOTHERNAME = "Mname";
    private static String MOTHERMOBILE = "Mmobileno";
    private static String MOTHEREMAIL = "Memail";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChaptersFragment.
     */
    public static UserProfileParentFragment newInstance(String mFatherName, String mFatherMobileNo, String mFatherEmail
            , String mMotherName, String mMotherMobileNo, String mMotherEmail) {
        UserProfileParentFragment fragment = new UserProfileParentFragment();
        Bundle args = new Bundle();
        args.putString(FATHERNAME, mFatherName);
        args.putString(FATHERMOBILE, mFatherMobileNo);
        args.putString(FATHEREMAIL, mFatherEmail);
        args.putString(MOTHERNAME, mMotherName);
        args.putString(MOTHERMOBILE, mMotherMobileNo);
        args.putString(MOTHEREMAIL, mMotherEmail);
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_profile_parent, container, false);
        if (getArguments() != null) {
            String mFatherName = getArguments().getString(FATHERNAME);
            String mFatherMobileNo = getArguments().getString(FATHERMOBILE);
            String mFatherEmail = getArguments().getString(FATHEREMAIL);
            String mMotherName = getArguments().getString(MOTHERNAME);
            String mMotherMobileNo = getArguments().getString(MOTHERMOBILE);
            String mMotherEmail = getArguments().getString(MOTHEREMAIL);
            if (TextUtils.isEmpty(mFatherName)) {
                mBinding.linearLayoutFatherName.setVisibility(View.GONE);
            } else {
                mBinding.textViewFatherName.setText(mFatherName);
            }
            if (TextUtils.isEmpty(mFatherMobileNo)) {
                mBinding.linearLayoutFatherMobile.setVisibility(View.GONE);
            } else {
                mBinding.textViewFatherMobile.setText(mFatherMobileNo);
            }
            if (TextUtils.isEmpty(mFatherEmail)) {
                mBinding.linearLayoutFatherEmail.setVisibility(View.GONE);
            } else {
                mBinding.textViewFatherEmail.setText(mFatherEmail);
            }
            if (TextUtils.isEmpty(mMotherName)) {
                mBinding.linearLayoutMotherName.setVisibility(View.GONE);
            } else {
                mBinding.textViewMotherName.setText(mMotherName);
            }
            if (TextUtils.isEmpty(mMotherMobileNo)) {
                mBinding.linearLayoutMotherMobile.setVisibility(View.GONE);
            } else {
                mBinding.textViewMotherMobile.setText(mMotherMobileNo);
            }
            if (TextUtils.isEmpty(mMotherEmail)) {
                mBinding.linearLayoutMotherEmail.setVisibility(View.GONE);
            } else {
                mBinding.textViewMotherEmail.setText(mMotherEmail);
            }
        }

        return mBinding.getRoot();
    }
}
