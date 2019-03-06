package in.securelearning.lil.android.login.views.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.LilPrivacyPolicy;
import in.securelearning.lil.android.login.dataobject.SignUpError;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Cp on 11/9/2016.
 */
public class SignUpActivity extends AppCompatActivity {

    String strInstituteId = "";
    private TextInputLayout mFirstNameLayout, mLastNameLayout, mEmailLayout, mPasswordLayout, mConfirmPasswordLayout, mAssociationLayout, mOtherAssociationLayout;
    private EditText mFirstNameEditText, mLastNameEditText, mEmailEditText, mPasswordEditText, mConfirmPasswordEditText, mAssociationEditText, mOtherAssociationEditText;
    private String strFirstName, strLastName, strEmail, strPassword, strConfirmPassword, strGender, strAssociationName, strOtherAssociationName;
    private RadioGroup mGenderRadioGroup;
    private RadioButton mMaleRadioButton, mFemaleRadioButton;
    private TextView mTermsConditionTextView, mErrorGenderTextView, mLoadingMessageTextView;
    private Button mSignUpButton;
    private CardView mGooglePlusButton, mFacebookButton;
    private RelativeLayout mProgressLayout, mSignUpLayout;
    private PopupWindow mAssociationPopup;
    private ImageButton mBackButton;
    private ArrayList<Institution> mInstitutionsList = new ArrayList<>();
    /**
     * LIL institute id
     */
//    private String strAssociationId = "LILOPENINSTITUTE";
    /**
     * id for MDS School
     */
    private final String strAssociationId = BuildConfig.ASSOCIATION_ID;

    @Override
    public void onBackPressed() {
        if (mProgressLayout.getVisibility() == View.GONE) {
            hideSoftKeyboard();
            finish();
            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_app_signup);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        initializeViews();
        initializeUiandClickListeners();

    }

    /**
     * initialize views and find ids
     */
    private void initializeViews() {
        mProgressLayout = (RelativeLayout) findViewById(R.id.layoutProgress);
        mSignUpLayout = (RelativeLayout) findViewById(R.id.layoutSignUp);
        mFirstNameLayout = (TextInputLayout) findViewById(R.id.inputLayoutSignUpFirstName);
        mLastNameLayout = (TextInputLayout) findViewById(R.id.inputLayoutSignUpLastName);
        mEmailLayout = (TextInputLayout) findViewById(R.id.inputLayoutSignUpEmail);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.inputLayoutSignUpPassword);
        mConfirmPasswordLayout = (TextInputLayout) findViewById(R.id.inputLayoutConfirmPassword);
        mAssociationLayout = (TextInputLayout) findViewById(R.id.inputLayoutSignUpAssociation);
        mOtherAssociationLayout = (TextInputLayout) findViewById(R.id.inputLayouSignUpAssociationOther);
        mFirstNameEditText = (EditText) findViewById(R.id.editTextSignUpFirstName);
        mLastNameEditText = (EditText) findViewById(R.id.editTextSignUpLastName);
        mEmailEditText = (EditText) findViewById(R.id.editTextSignUpEmail);
        mPasswordEditText = (EditText) findViewById(R.id.editTextSignUpPassword);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.editTextSignUpConfirmPassword);
        mAssociationEditText = (EditText) findViewById(R.id.editTextSignUpAssociation);
        mOtherAssociationEditText = (EditText) findViewById(R.id.editTextSignUpAssociationOther);
        mGenderRadioGroup = (RadioGroup) findViewById(R.id.radioGroupSignUpGender);
        mMaleRadioButton = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemaleRadioButton = (RadioButton) findViewById(R.id.radioButtonFemale);
        mTermsConditionTextView = (TextView) findViewById(R.id.textViewTerms);
        mErrorGenderTextView = (TextView) findViewById(R.id.textViewGenderEmpty);
        mLoadingMessageTextView = (TextView) findViewById(R.id.textViewLoadingMessage);
        mSignUpButton = (Button) findViewById(R.id.buttonSignUp);
        mGooglePlusButton = (CardView) findViewById(R.id.imageButtonSignUpFacebook);
        mFacebookButton = (CardView) findViewById(R.id.imageButtonSignUpGooglePlus);
        mBackButton = (ImageButton) findViewById(R.id.buttonBack);


    }

    /**
     * initialize ui like set text and click listeners
     */
    private void initializeUiandClickListeners() {

        String s = getString(R.string.policy_tandc);
        SpannableString ss = new SpannableString(s);
        String first = getString(R.string.privacy_policies);
        String second = getString(R.string.terms_of_services);
        int firstIndex = s.toString().indexOf(first);
        int secondIndex = s.toString().indexOf(second);

        ClickableSpan Privacy_Policy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignUpActivity.this, LilPrivacyPolicy.class);
                intent.putExtra("header", getString(R.string.privacy_policies));
                startActivity(intent);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan Terms_of_Service = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignUpActivity.this, LilPrivacyPolicy.class);
                intent.putExtra("header", getString(R.string.terms_of_services));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }

        };
        ss.setSpan(Privacy_Policy, firstIndex, firstIndex + first.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(Terms_of_Service, secondIndex, secondIndex + second.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTermsConditionTextView.setLinksClickable(true);
        mTermsConditionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTermsConditionTextView.setText(ss, TextView.BufferType.SPANNABLE);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                KeyBoardUtil.hideSoftKeyboard(mGenderRadioGroup, getBaseContext());
                if (checkedId == R.id.radiobutton_male) {
                    mMaleRadioButton.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.colorWhite));
                    mFemaleRadioButton.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.hintColorWhite66));
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);
                    strGender = (String) radioButton.getText();
                    mErrorGenderTextView.setVisibility(View.GONE);
                } else if (checkedId == R.id.radiobutton_female) {
                    mFemaleRadioButton.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.colorWhite));
                    mMaleRadioButton.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.hintColorWhite66));
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);
                    strGender = (String) radioButton.getText();
                    mErrorGenderTextView.setVisibility(View.GONE);
                }

            }
        });

        mOtherAssociationLayout.setVisibility(View.GONE);
        mAssociationEditText.setClickable(false);
        mAssociationEditText.setEnabled(false);
        mAssociationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInstitutionsList != null || mInstitutionsList.size() > 0) {
                    showAssociationPopup(view);

                } else {
                    ToastUtils.showToastAlert(SignUpActivity.this, "No institute found");
                }
            }
        });

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                validateFirstName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                validateLastName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mConfirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateConfirmPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields(view);
            }
        });
        mOtherAssociationEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mConfirmPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

    }

    /**
     * validate edittext and gender icon_radio_button_multiple group
     *
     * @param view
     */
    private void validateFields(final View view) {
        if (!validateFirstName()) {
            return;
        }
        if (!validateLastName()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validateConfirmPassword()) {
            return;
        }
        if (validateGender()) {
            return;
        }
//        if (!validateAssociation()) {
//            return;
//        }
//        if (mOtherAssociationLayout.getVisibility() == View.VISIBLE) {
//            if (!validateOtherAssociation())
//                return;
//        }

        Observable.just(SignUpActivity.this).subscribeOn(Schedulers.computation()).subscribe(new Consumer<SignUpActivity>() {
            @Override
            public void accept(SignUpActivity signUpActivity) {

                performSignUp(view);
            }
        });


    }

    /**
     * validate first name
     *
     * @return
     */
    private boolean validateFirstName() {
        strFirstName = mFirstNameEditText.getText().toString().trim();
        if (strFirstName.isEmpty()) {
            mFirstNameLayout.setError(getString(R.string.error_firstname));
            requestFocus(mFirstNameEditText);
            return false;
        } else if (strFirstName.matches("^[0-9]*$")) {
            mFirstNameLayout.setError(getString(R.string.error_invalid_name));
            requestFocus(mFirstNameEditText);
            return false;
        } else {
            // mFirstNameEditText.clearFocus();
            mFirstNameLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate last name
     *
     * @return
     */
    private boolean validateLastName() {
        strLastName = mLastNameEditText.getText().toString().trim();
        if (strLastName.isEmpty()) {
            mLastNameLayout.setError(getString(R.string.error_lastname));
            requestFocus(mLastNameEditText);
            return false;
        } else if (strLastName.matches("^[0-9]*$")) {
            mLastNameLayout.setError(getString(R.string.error_invalid_name));
            requestFocus(mLastNameEditText);
            return false;
        } else {
            //mLastNameEditText.clearFocus();
            mLastNameLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate email
     *
     * @return
     */
    private boolean validateEmail() {
        strEmail = mEmailEditText.getText().toString().trim();
        if (strEmail.isEmpty()) {
            mEmailLayout.setError(getString(R.string.error_email));
            requestFocus(mEmailEditText);
            return false;
        } else if (!strEmail.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")) {
            mEmailLayout.setError(getString(R.string.error_invalid_email));
            requestFocus(mEmailEditText);
            return false;
        } else {
            //mEmailEditText.clearFocus();
            mEmailLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate password
     *
     * @return
     */
    private boolean validatePassword() {
        strPassword = mPasswordEditText.getText().toString().trim();
        if (strPassword.isEmpty()) {
            mPasswordLayout.setError(getString(R.string.error_password));
            requestFocus(mPasswordEditText);
            return false;
        } else if (strPassword.equals(strPassword.toLowerCase())) {
            mPasswordLayout.setError(getString(R.string.error_password_upper_case));
            requestFocus(mPasswordEditText);
            return false;
        } else if (strPassword.equals(strPassword.toUpperCase())) {
            mPasswordLayout.setError(getString(R.string.error_password_lower_case));
            requestFocus(mPasswordEditText);
            return false;
        } else if (!hasNumericCharacter(strPassword)) {
            mPasswordLayout.setError(getString(R.string.error_password_numeric_character));
            requestFocus(mPasswordEditText);
            return false;
        } else if (strPassword.length() < 8) {
            mPasswordLayout.setError(getString(R.string.error_password_length_eigth));
            requestFocus(mPasswordEditText);
            return false;
        } else {
            // mPasswordEditText.clearFocus();
            mPasswordLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean hasNumericCharacter(String strPassword) {
        for (int i = 0; i < strPassword.length(); i++) {
            if (Character.isDigit(strPassword.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * validate and confirm password
     *
     * @return
     */
    private boolean validateConfirmPassword() {
        strConfirmPassword = mConfirmPasswordEditText.getText().toString().trim();
        if (strConfirmPassword.isEmpty()) {
            mConfirmPasswordLayout.setError(getString(R.string.prompt_confirm_pass));
            requestFocus(mConfirmPasswordEditText);
            return false;
        } else if (!mConfirmPasswordEditText.getText().toString().trim().equals(mPasswordEditText.getText().toString().trim())) {
            mConfirmPasswordLayout.setError(getString(R.string.prompt_pass_match));
            requestFocus(mConfirmPasswordEditText);
            return false;
        } else {
            //  mConfirmPasswordEditText.clearFocus();
            mConfirmPasswordLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate gender selection
     *
     * @return
     */
    private boolean validateGender() {
        if (mMaleRadioButton.isChecked()) {
            mErrorGenderTextView.setVisibility(View.GONE);
            return false;
        } else if (mFemaleRadioButton.isChecked()) {
            mErrorGenderTextView.setVisibility(View.GONE);
            return false;
        } else {
            mErrorGenderTextView.setVisibility(View.VISIBLE);

        }
        return true;
    }

    /**
     * validate association field
     *
     * @return
     */
    private boolean validateAssociation() {
        strAssociationName = mAssociationEditText.getText().toString().trim();
        if (strAssociationName.isEmpty()) {
            mAssociationLayout.setError(getString(R.string.error_field_required));
            requestFocus(mAssociationEditText);
            return false;
        } else {
            mAssociationEditText.clearFocus();
            mAssociationLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate association name enter by user
     *
     * @return
     */
    private boolean validateOtherAssociation() {
        strOtherAssociationName = mOtherAssociationEditText.getText().toString().trim();
        if (strOtherAssociationName.isEmpty()) {
            mOtherAssociationLayout.setError(getString(R.string.error_field_required));
            requestFocus(mOtherAssociationEditText);
            return false;
        } else {
            mOtherAssociationEditText.clearFocus();
            mOtherAssociationLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * requesting keyboard for focused edittext
     *
     * @param view
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * fetch institutions list and save it to arraylist
     */
    private void getInstituteList() {
        ApiModule apiModule = new ApiModule(SignUpActivity.this);
        BaseApiInterface apiInterface = apiModule.getBaseClient();
        Call<ArrayList<Institution>> arrayListInstituteCall = apiInterface.fetchInstituteList();
        arrayListInstituteCall.enqueue(new Callback<ArrayList<Institution>>() {
            @Override
            public void onResponse(Call<ArrayList<Institution>> call, Response<ArrayList<Institution>> response) {

                if (response != null && response.isSuccessful()) {
                    Log.e("fetching institute list", "successful");
                    mInstitutionsList = response.body();
                    Log.e("institute list", "" + mInstitutionsList.size());
                    addNoneOfThemToInstitute();

                } else {

                    Log.e("Fetch Institute list", "err fetching" + response.message());
                    ToastUtils.showToastAlert(SignUpActivity.this, "Error fetching associations list");
                    addNoneOfThemToInstitute();

                }


            }

            @Override
            public void onFailure(Call<ArrayList<Institution>> call, Throwable t) {

                Log.e("Fetch Institute", "err fetching" + t.toString());
                ToastUtils.showToastAlert(SignUpActivity.this, getString(R.string.no_internet));
                addNoneOfThemToInstitute();
            }
        });


    }

    /**
     * add "None of them" item to institute array list
     */
    private void addNoneOfThemToInstitute() {
        mAssociationEditText.setClickable(true);
        mAssociationEditText.setEnabled(true);
        Institution otherInstitution = new Institution();
        otherInstitution.setId(strAssociationId);
        otherInstitution.setName("Lil Opencourses");
        mInstitutionsList.add(otherInstitution);
    }

    /**
     * fetch all values from fields and set it to dataobject
     * Then perform signup
     *
     * @param view
     */
    private void performSignUp(final View view) {
        showProgress(true);
        /**
         * Login through server if network is available
         */
        if (GeneralUtils.isNetworkAvailable(SignUpActivity.this)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AnimationUtils.continueBlinkAnimation(mLoadingMessageTextView);

                }
            });

            ApiModule apiModule = new ApiModule(SignUpActivity.this);
            BaseApiInterface apiInterface = apiModule.getBaseClient();

            final UserProfile userProfile = new UserProfile();
            userProfile.setFirstName(mFirstNameEditText.getText().toString());
            userProfile.setLastName(mLastNameEditText.getText().toString());
            userProfile.setEmail(mEmailEditText.getText().toString().trim().replace(" ", ""));
            userProfile.setPassword(mConfirmPasswordEditText.getText().toString());
            userProfile.setGender(strGender);
            Institution otherInstitution = new Institution();
            otherInstitution.setName("LIL Demo School");
            otherInstitution.setId("59bbf44f6199051b00fb86d6");
            otherInstitution.setSplashThumbnail(null);
            otherInstitution.setThumbnail(null);
            userProfile.setAssociation(otherInstitution);
            userProfile.setObjectId(null);
            userProfile.setAlias(null);
            userProfile.setGrade(null);
            userProfile.setThumbnail(null);
            userProfile.setTitle(null);
            userProfile.setAboutMe(null);
            userProfile.setAddress(null);
            userProfile.setBadgesEarned(null);
            userProfile.setBoard(null);
            userProfile.setCreationTime(null);
            userProfile.setDepartment(null);
            userProfile.setDesignation(null);
            userProfile.setDob(null);
            userProfile.setDoj(null);
            userProfile.setEmailVerified(null);
            userProfile.setLastUpdationTime(null);
            userProfile.setInterest(null);
            userProfile.setLocation(null);
            userProfile.setMobile(null);
            userProfile.setMemberGroups(null);
            userProfile.setModeratedGroups(null);
            userProfile.setQualification(null);
            userProfile.setRole(null);
            userProfile.setSection(null);
            userProfile.setUserPic(null);
            Call<ResponseBody> userProfileCall = apiInterface.login(userProfile);
            userProfileCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response != null && response.isSuccessful()) {
                        Log.e("SignUp", "successful");
                        showProgress(false);
//                        AppPrefs.setUserId(userProfile.getEmail(), getBaseContext());
//                        AppPrefs.setUserPassword(userProfile.getPassword(), getBaseContext());
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle(R.string.sign_up_success_title)
                                .setMessage(R.string.sign_up_success_message)
                                .setCancelable(false)
                                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }

                                }).show();

                    } else {
                        Log.e("SignUpActivity ", "err signUp" + response.message());
                        try {
                            final String errorMessage = response.errorBody().string();
                            SignUpError error = GeneralUtils.fromGson(errorMessage, SignUpError.class);
                            if (TextUtils.isEmpty(error.getErrorBody().getMessage())) {
                                Snackbar.make(view, getString(R.string.no_internet) + " " + getString(R.string.sign_up_failed), Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, error.getErrorBody().getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Snackbar.make(view, getString(R.string.no_internet) + " " + getString(R.string.sign_up_failed), Snackbar.LENGTH_LONG).show();
                        }


                        showProgress(false);

                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Log.e("signUpActivity", "err signUp" + t.toString());
                    Snackbar.make(view, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    showProgress(false);
                }
            });

        } else {
            Snackbar.make(view, getString(R.string.no_internet) + " " + getString(R.string.sign_up_failed), Snackbar.LENGTH_LONG).show();
            showProgress(false);
        }
    }

    /**
     * create and show category dialog
     *
     * @param view
     */
    private void showAssociationPopup(View view) {

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mPopupLayout = layoutInflater.inflate(R.layout.layout_recyclerview, null);
        RecyclerView mInstituteRecyclerView = (RecyclerView) mPopupLayout.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SignUpActivity.this);
        mInstituteRecyclerView.setLayoutManager(mLayoutManager);
        InstituteAdapter mInstituteAdapter = new InstituteAdapter(mInstitutionsList);
        mInstituteRecyclerView.setAdapter(mInstituteAdapter);

        int popupWidth = view.getWidth() - 10;
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        mAssociationPopup = new PopupWindow(SignUpActivity.this);
        mAssociationPopup.setContentView(mPopupLayout);
        mAssociationPopup.setWidth(popupWidth);
        mAssociationPopup.setHeight(popupHeight);
        mAssociationPopup.setFocusable(true);

        int OFFSET_X = 0;
        int OFFSET_Y = view.getHeight() - 30;
        mAssociationPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAssociationPopup.setOutsideTouchable(true);
        mAssociationPopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mAssociationPopup.showAtLocation(mPopupLayout, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);
    }

    /**
     * clear keyboard focus from edittext
     */
    private void clearFocus() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Shows the progress UI and hides the refreshToken form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
                 for very easy animations. If available, use these APIs to fade-in
                 the progress spinner.
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

                    mSignUpLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    mSignUpLayout.animate().setDuration(shortAnimTime).alpha(
                            show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSignUpLayout.setVisibility(show ? View.GONE : View.VISIBLE);

                        }
                    });

                    mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    mProgressLayout.animate().setDuration(shortAnimTime).alpha(
                            show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
                } else {
                    /**
                     * The ViewPropertyAnimator APIs are not available, so simply show
                     and hide the relevant UI components.
                     */
                    mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    mSignUpLayout.setVisibility(show ? View.GONE : View.VISIBLE);

                }

            }
        });
    }

    public class InstituteAdapter extends RecyclerView.Adapter<InstituteAdapter.ViewHolder> {

        ArrayList<Institution> institutionArrayList = new ArrayList<>();

        public InstituteAdapter(ArrayList<Institution> mInstitutionsList) {
            this.institutionArrayList = mInstitutionsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recyclerview_simple_item, parent, false);
            ViewHolder mViewHolder = new ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mInstituteNameTextView.setText(institutionArrayList.get(position).getName());

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holder.mInstituteNameTextView.getText().toString().equals("None of them")) {
                        mOtherAssociationLayout.setVisibility(View.VISIBLE);
                        requestFocus(mOtherAssociationEditText);
                    } else {
                        mOtherAssociationLayout.setVisibility(View.GONE);
                    }
                    strInstituteId = institutionArrayList.get(position).getId();
                    mAssociationEditText.setTag(institutionArrayList.get(position));
                    mAssociationEditText.setText(holder.mInstituteNameTextView.getText().toString());
                    mAssociationPopup.dismiss();
                    Log.e("Name--", "" + holder.mInstituteNameTextView.getText().toString() + " Id--" + strInstituteId);
                }
            });
        }

        @Override
        public int getItemCount() {
            return institutionArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mInstituteNameTextView;
            private View mRootView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mInstituteNameTextView = (TextView) mRootView.findViewById(R.id.textview_item);
            }
        }


    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}

