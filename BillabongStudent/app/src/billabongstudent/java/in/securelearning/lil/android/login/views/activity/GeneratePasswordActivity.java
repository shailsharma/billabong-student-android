package in.securelearning.lil.android.login.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutGeneratePasswordBinding;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.PasswordChangeActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.home.views.activity.PasswordChangeActivity.FROM_OTHER;

public class GeneratePasswordActivity extends AppCompatActivity {

    LayoutGeneratePasswordBinding mBinding;

    @Inject
    HomeModel mHomeModel;
    private Snackbar mIndefiniteSnackBar;
    private SmsVerifyCatcher mSmsVerifyCatcher;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, GeneratePasswordActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (mBinding.layoutSubmitOTP.getVisibility() == View.VISIBLE || mBinding.layoutProgress.getVisibility() == View.VISIBLE) {
            showRequestOTPLayout();
        } else {
            finish();
            startActivity(LoginActivity.getStartIntent(getBaseContext(), Intent.ACTION_MAIN));


        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_generate_password);
        setUpToolbar();
        initializeViewAndListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mSmsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSmsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSmsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeViewAndListeners() {

        mBinding.buttonRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBinding.editTextLoginPhone.getText().toString().trim().isEmpty()) {
                    clearOTPEditTexts();
                    hideSoftKeyboard();
                    requestOTP(mBinding.editTextLoginPhone.getText().toString().trim(), false);
                } else {
                    SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.enter_your_enrollment_number));
                }


            }
        });

        mBinding.buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearOTPEditTexts();
                hideSoftKeyboard();
                if (mBinding.editTextLoginPhone.getText() != null
                        && !TextUtils.isEmpty(mBinding.editTextLoginPhone.getText().toString().trim())) {

                    requestOTP(mBinding.editTextLoginPhone.getText().toString().trim(), true);

                } else {
                    SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.error_something_went_wrong));
                }
            }
        });

        mBinding.buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateOTP()) {
                    hideSoftKeyboard();
                    if (mBinding.editTextLoginPhone.getText() != null
                            && !TextUtils.isEmpty(mBinding.editTextLoginPhone.getText().toString().trim())) {
                        verifyOTP(mBinding.editTextLoginPhone.getText().toString().trim(), getOTPFromEditText());

                    } else {
                        SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.error_something_went_wrong));
                    }
                }
            }
        });

        mBinding.editTextLoginPhone.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mIndefiniteSnackBar != null && mIndefiniteSnackBar.isShown()) {
                    mIndefiniteSnackBar.dismiss();
                }
                return false;
            }
        });

//        mBinding.editTextLoginPhone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!mBinding.editTextLoginPhone.getText().toString().trim().isEmpty()) {
//                    mBinding.buttonRequestOTP.setEnabled(true);
//                    mBinding.buttonRequestOTP.setClickable(true);
//                    mBinding.buttonRequestOTP.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.gradient_chip));
//                    mBinding.buttonRequestOTP.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
//                } else {
//                    mBinding.buttonRequestOTP.setEnabled(false);
//                    mBinding.buttonRequestOTP.setClickable(false);
//                    mBinding.buttonRequestOTP.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.chip_solid_disabled_ripple));
//                    mBinding.buttonRequestOTP.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey66));
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        mBinding.otpView1.addTextChangedListener(new GenericTextWatcher(mBinding.otpView1));
        mBinding.otpView2.addTextChangedListener(new GenericTextWatcher(mBinding.otpView2));
        mBinding.otpView3.addTextChangedListener(new GenericTextWatcher(mBinding.otpView3));
        mBinding.otpView4.addTextChangedListener(new GenericTextWatcher(mBinding.otpView4));

        mSmsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);

                mBinding.otpView1.append(String.valueOf(code.charAt(0)));
                mBinding.otpView2.append(String.valueOf(code.charAt(1)));
                mBinding.otpView3.append(String.valueOf(code.charAt(2)));
                mBinding.otpView4.append(String.valueOf(code.charAt(3)));

            }
        });

        mBinding.otpView3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBinding.otpView3.getText().toString().trim().length() == 1) {
                    mBinding.otpView4.requestFocus();
                } else if (mBinding.otpView3.getText().toString().trim().length() == 0) {
                    mBinding.otpView2.requestFocus();
                }
            }
        });

    }

    private void clearOTPEditTexts() {
        mBinding.otpView1.setText("");
        mBinding.otpView2.setText("");
        mBinding.otpView3.setText("");
        mBinding.otpView4.setText("");
    }

    private String getOTPFromEditText() {
        return mBinding.otpView1.getText().toString().trim()
                + mBinding.otpView2.getText().toString().trim()
                + mBinding.otpView3.getText().toString().trim()
                + mBinding.otpView4.getText().toString().trim();
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            switch (view.getId()) {

                case R.id.otpView1:
                    if (mBinding.otpView1.getText().toString().length() == 1) {
                        mBinding.otpView2.requestFocus();
                    }
                    break;
                case R.id.otpView2:
                    if (mBinding.otpView2.getText().toString().length() == 1) {
                        mBinding.otpView3.requestFocus();
                    } else if (mBinding.otpView2.getText().toString().length() == 0) {
                        mBinding.otpView1.requestFocus();
                    }
                    break;
//                case R.id.otpView3:
//                    if (mBinding.otpView3.getText().toString().length() == 1) {
//                        mBinding.otpView4.requestFocus();
//                    } else if (mBinding.otpView3.getText().toString().length() == 0) {
//                        mBinding.otpView2.requestFocus();
//                    }
//                    break;
                case R.id.otpView4:
                    if (mBinding.otpView4.getText().toString().length() == 1) {
                        hideSoftKeyboard();
                    } else if (mBinding.otpView4.getText().toString().length() == 0) {
                        mBinding.otpView3.requestFocus();
                    }
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    private void verifyOTP(final String mobileNumber, String verificationCode) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            showResendProgressLayout(getString(R.string.verifying_otp));

            mHomeModel.verifyOTP(mobileNumber, verificationCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AuthToken>() {
                        @Override
                        public void accept(AuthToken authToken) throws Exception {

                            if (!TextUtils.isEmpty(authToken.getToken()) && !TextUtils.isEmpty(authToken.getUserId())) {
                                AppPrefs.setIdToken(authToken.getToken(), getBaseContext());
                                Toast.makeText(getBaseContext(), getString(R.string.otp_verified_successfully), Toast.LENGTH_SHORT).show();
                                startActivity(PasswordChangeActivity.getStartIntent(getBaseContext(), authToken.getUserId(), getString(R.string.messagePasswordGenerateSuccess), getString(R.string.generate_password), FROM_OTHER));
                                finish();
                            } else {
                                showSubmitOTPLayout();
                                SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.messageUnableToGetData));
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            showSubmitOTPLayout();
                            SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), throwable.getMessage());


                        }
                    });
        } else {
            SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
        }
    }

    private boolean validateOTP() {
        if (getOTPFromEditText().isEmpty()) {
            String message = getString(R.string.please_enter_otp_sent_to)
                    + " " + mBinding.textViewOTPMobileNumber.getText().toString().trim();
            SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), message);
            return false;
        } else if (getOTPFromEditText().length() < 4) {
            SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.otp_should_be_4_digit_long));
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("CheckResult")
    private void requestOTP(final String mobileNumber, boolean isFromResend) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            if (isFromResend) {
                showResendProgressLayout(getString(R.string.verifying_your_enrollment_number));
            } else {
                showProgressLayout(getString(R.string.verifying_your_enrollment_number));
            }
            mHomeModel.requestOTP(mobileNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<RequestOTPResponse>() {
                        @Override
                        public void accept(RequestOTPResponse requestOTPResponse) throws Exception {
                            showSubmitOTPLayout();
                            mBinding.textViewOTPMobileNumber.setText(getSecuredMobileNumber(requestOTPResponse.getMobile()));
                            mBinding.textViewOTPMobileNumber.setTag(requestOTPResponse.getMobile());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            showRequestOTPLayout();

                            mIndefiniteSnackBar = Snackbar.make(mBinding.getRoot(), throwable.getMessage(), Snackbar.LENGTH_INDEFINITE);
                            View snackBarView = mIndefiniteSnackBar.getView();
                            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setMaxLines(6);
                            textView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                            mIndefiniteSnackBar.setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mIndefiniteSnackBar.dismiss();
                                }
                            });

                            mIndefiniteSnackBar.show();

                        }
                    });
        } else {
            SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
        }
    }

    private String getSecuredMobileNumber(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() == 10) {
                String temp = mobile.substring(7);
                return "XXXXXXX" + temp;
            } else if (mobile.length() == 12) {
                String temp = mobile.substring(9);
                return "XXXXXXXXX" + temp;
            } else if (mobile.length() == 13) {
                String temp = mobile.substring(10);
                return "XXXXXXXXXX" + temp;
            } else {
                return "";
            }
        } else {
            return "";
        }

    }

    private void showProgressLayout(String message) {
        mBinding.textViewProgress.setText(message);
        AnimationUtils.pushUpExit(getBaseContext(), mBinding.layoutRequestOTP);
        mBinding.layoutRequestOTP.setVisibility(View.GONE);

        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutProgress);
    }

    private void showResendProgressLayout(String message) {
        mBinding.textViewProgress.setText(message);
        AnimationUtils.pushUpExit(getBaseContext(), mBinding.layoutSubmitOTP);
        mBinding.layoutSubmitOTP.setVisibility(View.GONE);

        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutProgress);
    }

    private void showSubmitOTPLayout() {
        AnimationUtils.pushUpExit(getBaseContext(), mBinding.layoutProgress);
        mBinding.layoutProgress.setVisibility(View.GONE);

        mBinding.layoutSubmitOTP.setVisibility(View.VISIBLE);
        mBinding.otpView1.requestFocus();
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutSubmitOTP);

    }

    private void showRequestOTPLayout() {

        mBinding.layoutProgress.setVisibility(View.GONE);

        AnimationUtils.pushDownExit(getBaseContext(), mBinding.layoutSubmitOTP);
        mBinding.layoutSubmitOTP.setVisibility(View.GONE);

        mBinding.layoutRequestOTP.setVisibility(View.VISIBLE);
        AnimationUtils.pushDownEnter(getBaseContext(), mBinding.layoutRequestOTP);
    }

    /*setup toolbar*/
    private void setUpToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.generate_password));
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
}
