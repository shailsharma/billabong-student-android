package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutChangePasswordBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.events.PasswordChangeEvent;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.PasswordChange;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 05-Feb-18.
 */

public class PasswordChangeActivity extends AppCompatActivity {

    @Inject
    NetworkModel mNetworkModel;
    @Inject
    RxBus mRxBus;

    LayoutChangePasswordBinding mBinding;

    private String mConfirmPassword;
    private static final String USER_ID = "userId";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String TOOLBAR_TITLE = "toolbarTitle";
    private static final String FROM = "from";
    private String mUserId;
    private String mSuccessMessage;
    private String mToolbarTitle;
    private int mFrom;
    public static final int FROM_LOGIN = 0;
    public static final int FROM_OTHER = 1;

    @Override
    public void onBackPressed() {
        if (mFrom == FROM_LOGIN) {
            SyncServiceHelper.performUserLogout(PasswordChangeActivity.this, getString(R.string.messagePleaseWait));
        }
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_change_password);
        handleIntent();
        setUpToolbar();
        initializeUiAndListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, String userId, String passwordChangeMessage, String toolbarTitle, int from) {
        Intent intent = new Intent(context, PasswordChangeActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(SUCCESS_MESSAGE, passwordChangeMessage);
        intent.putExtra(TOOLBAR_TITLE, toolbarTitle);
        intent.putExtra(FROM, from);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mUserId = getIntent().getStringExtra(USER_ID);
            mSuccessMessage = getIntent().getStringExtra(SUCCESS_MESSAGE);
            mToolbarTitle = getIntent().getStringExtra(TOOLBAR_TITLE);
            mFrom = getIntent().getIntExtra(FROM, FROM_LOGIN);

        }
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(mToolbarTitle);
    }

    private void initializeUiAndListeners() {

        mBinding.buttonChangePassword.setText(getString(R.string.done));
        mBinding.editTextNewPassword.addTextChangedListener(new TextWatcher() {
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

        mBinding.editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
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

        mBinding.buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePassword()) {
                    return;
                } else if (!validateConfirmPassword()) {
                    return;
                } else {
                    hideSoftKeyboard();
                    performPasswordChange(mUserId, mConfirmPassword);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void performPasswordChange(final String userId, final String password) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.messagePleaseWait), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            io.reactivex.Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                    PasswordChange passwordChange = new PasswordChange();
                    passwordChange.setId(userId);
                    passwordChange.setPassword(password);
                    Call<ResponseBody> call = mNetworkModel.changePassword(passwordChange);
                    Response<ResponseBody> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("passwordChange1--", "successful");
                        e.onNext(response.body());
                    } else if (response.code() == 404) {
                        throw new Exception(getString(R.string.messagePasswordChangeFailed));
                    } else if (response.code() == 401 && SyncServiceHelper.refreshToken(getBaseContext())) {
                        Response<ResponseBody> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Log.e("passwordChange2--", "successful");
                            e.onNext(response2.body());
                        } else if (response2.code() == 401) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                        } else if (response.code() == 404) {
                            throw new Exception(getString(R.string.messagePasswordChangeFailed));
                        } else {
                            Log.e("passwordChange2--", "Failed");
                            throw new Exception(getString(R.string.messagePasswordChangeFailed));
                        }
                    } else {
                        Log.e("passwordChange1--", "Failed");
                        throw new Exception(getString(R.string.messagePasswordChangeFailed));
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {
                            progressDialog.dismiss();
                            if (responseBody != null) {
                                showAlert(mSuccessMessage);

                            } else {
                                SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.layoutPassword, getString(R.string.messageUnableToGetData));
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            progressDialog.dismiss();
                            SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.layoutPassword, throwable.getMessage());
                        }
                    });
        } else {
            SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutPassword);
        }

    }

    private void showAlert(String message) {
        new AlertDialog.Builder(PasswordChangeActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mRxBus.send(new PasswordChangeEvent());
                        if (mFrom == FROM_LOGIN) {
                            SyncServiceHelper.performUserLogout(PasswordChangeActivity.this, getString(R.string.messagePleaseWait));
                        }
                        finish();


                    }

                }).show();
    }

    private boolean validatePassword() {
        String newPassword = mBinding.editTextNewPassword.getText().toString().trim();
        if (newPassword.isEmpty()) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (newPassword.equals(newPassword.toLowerCase())) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_upper_case));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (newPassword.equals(newPassword.toUpperCase())) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_lower_case));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (!hasNumericCharacter(newPassword)) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_numeric_character));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (newPassword.length() < 8) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_length_eigth));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else {
            mBinding.inputLayoutNewPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        mConfirmPassword = mBinding.editTextConfirmPassword.getText().toString().trim();
        if (mConfirmPassword.isEmpty()) {
            mBinding.inputLayoutConfirmPassword.setError(getString(R.string.prompt_confirm_pass));
            requestFocus(mBinding.editTextConfirmPassword);
            return false;
        } else if (!mBinding.editTextConfirmPassword.getText().toString().trim().equals(mBinding.editTextNewPassword.getText().toString().trim())) {
            mBinding.inputLayoutConfirmPassword.setError(getString(R.string.prompt_pass_match));
            requestFocus(mBinding.editTextConfirmPassword);
            return false;
        } else {
            mBinding.inputLayoutConfirmPassword.setErrorEnabled(false);

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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
