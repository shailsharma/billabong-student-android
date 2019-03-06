package in.securelearning.lil.android.home.views.activity;

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

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutChangePasswordBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
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
    AppUserModel mAppUserModel;
    @Inject
    NetworkModel mNetworkModel;
    LayoutChangePasswordBinding mBinding;

    private String mNewPassword, mConfirmPassword;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_change_password);
        setUpToolbar();
        initializeUiAndListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, PasswordChangeActivity.class);
        return intent;
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.labelChangePassword);
    }

    private void initializeUiAndListeners() {

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
                    performPasswordChange(mConfirmPassword);
                }
            }
        });
    }

    private void performPasswordChange(final String password) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.messagePleaseWait), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            io.reactivex.Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                    PasswordChange passwordChange = new PasswordChange();
                    passwordChange.setId(mAppUserModel.getObjectId());
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
                                new AlertDialog.Builder(PasswordChangeActivity.this)
                                        .setMessage(R.string.messagePasswordChangeSuccess)
                                        .setCancelable(false)
                                        .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                finish();

                                            }

                                        }).show();
                            } else {
                                SnackBarUtils.showSuccessSnackBar(getBaseContext(), mBinding.layoutPassword, getString(R.string.messagePasswordChangeFailed));
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            progressDialog.dismiss();
                            SnackBarUtils.showSuccessSnackBar(getBaseContext(), mBinding.layoutPassword, throwable.getMessage());
                        }
                    });
        } else {
            SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutPassword);
        }

    }

    private boolean validatePassword() {
        mNewPassword = mBinding.editTextNewPassword.getText().toString().trim();
        if (mNewPassword.isEmpty()) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (mNewPassword.equals(mNewPassword.toLowerCase())) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_upper_case));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (mNewPassword.equals(mNewPassword.toUpperCase())) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_lower_case));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (!hasNumericCharacter(mNewPassword)) {
            mBinding.inputLayoutNewPassword.setError(getString(R.string.error_password_numeric_character));
            requestFocus(mBinding.editTextNewPassword);
            return false;
        } else if (mNewPassword.length() < 8) {
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
