package in.securelearning.lil.android.home.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import in.securelearning.lil.android.base.dataobjects.Settings;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.app.R;

/**
 * Created by Cp on 2/8/2017.
 */
public class FTPSettingActivity extends AppCompatActivity {

    private ImageButton mBackButton, mUpdateButton;
    private TextInputLayout mURLLayout, mBaseApiUrlLayout, mUsernameLayout, mPasswordLayout, mDirectoryLayout, mPortLayout;
    private EditText mUrlEditText, mBaseApiUrlEditText, mUsernameEditText, mPasswordEditText, mDirectoryEditText, mPortEditText;
    private CheckBox mFTPLoginCheckBox;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ftp_setting);
        getWindow().setStatusBarColor(ContextCompat.getColor(FTPSettingActivity.this, R.color.colorPrimary));
        initializeViews();
        initializeUIAndListeners();
        setDefaultConfiguration();
    }

    private void setDefaultConfiguration() {

        Settings settings = AppPrefs.getSettings(FTPSettingActivity.this);
        if (settings != null) {
            mUrlEditText.setText(settings.getFtpUrl());
            mBaseApiUrlEditText.setText(settings.getBaseWepApiUrl().toString());
            mUsernameEditText.setText(settings.getFtpUsername().toString());
            mPasswordEditText.setText(settings.getFtPassword().toString());
            mDirectoryEditText.setText(settings.getFtpDirectory().toString());
            mPortEditText.setText("" + settings.getFtpPort());
        }

    }

    private void initializeViews() {
        mBackButton = (ImageButton) findViewById(R.id.imagebutton_back);
        mUpdateButton = (ImageButton) findViewById(R.id.imagebutton_update);
        mFTPLoginCheckBox = (CheckBox) findViewById(R.id.checkbox_ftp);
        mURLLayout = (TextInputLayout) findViewById(R.id.input_layout_ftpurl);
        mBaseApiUrlLayout = (TextInputLayout) findViewById(R.id.input_layout_baseurl);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.input_layout_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        mDirectoryLayout = (TextInputLayout) findViewById(R.id.input_layout_directory);
        mPortLayout = (TextInputLayout) findViewById(R.id.input_layout_port);

        mUrlEditText = (EditText) findViewById(R.id.edittext_ftp_url);
        mBaseApiUrlEditText = (EditText) findViewById(R.id.edittext_ftp_baseurl);
        mUsernameEditText = (EditText) findViewById(R.id.edittext_username);
        mPasswordEditText = (EditText) findViewById(R.id.edittext_password);
        mDirectoryEditText = (EditText) findViewById(R.id.edittext_directory);
        mPortEditText = (EditText) findViewById(R.id.edittext_port);
    }

    private void initializeUIAndListeners() {

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateURL()) {
                    return;
                } else if (!validateBaseApiUrl()) {
                    return;
                } else if (!validateUsername()) {
                    return;
                } else if (!validatePassword()) {
                    return;
                } else {
                    Settings settings = new Settings();
                    String baseApiUrl = mBaseApiUrlEditText.getText().toString();
                    String baseFtpUrl = mUrlEditText.getText().toString();

                    if (!baseApiUrl.endsWith("/"))
                        settings.setBaseWepApiUrl(mBaseApiUrlEditText.getText().toString() + "/");
                    else
                        settings.setBaseWepApiUrl(mBaseApiUrlEditText.getText().toString());

                    if (!baseFtpUrl.endsWith("/"))
                        settings.setFtpUrl(mUrlEditText.getText().toString() + "/");
                    else
                        settings.setFtpUrl(mUrlEditText.getText().toString());

                    settings.setFtPassword(mPasswordEditText.getText().toString());
                    settings.setFtpUsername(mUsernameEditText.getText().toString());
//                    settings.setFtpUrl(mUrlEditText.getText().toString());
                    //settings.setFtpDirectory(mDirectoryEditText.getText().toString());
                    //settings.setFtpPort(Integer.parseInt(mPortEditText.getText().toString()));

                    AppPrefs.setSettings(FTPSettingActivity.this, settings);
                    finish();
                }

            }
        });

        findViewById(R.id.secure_learning_ip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseApiUrlEditText.setText("https://www.securelearning.in/securelearning/");
            }
        });

    }

    private boolean validateURL() {
        if (mUrlEditText.getText().toString().trim().isEmpty()) {
            mURLLayout.setError("Enter FTP Url");
            requestFocus(mUrlEditText);
            return false;
        } else {
            mUrlEditText.clearFocus();
            mURLLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateBaseApiUrl() {
        if (mBaseApiUrlEditText.getText().toString().trim().isEmpty()) {
            mBaseApiUrlLayout.setError("Enter Base Api Url");
            requestFocus(mBaseApiUrlEditText);
            return false;
        } else {
            mBaseApiUrlEditText.clearFocus();
            mBaseApiUrlLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateUsername() {
        if (mUsernameEditText.getText().toString().trim().isEmpty()) {
            mUsernameLayout.setError("Enter FTP Username");
            requestFocus(mUsernameEditText);
            return false;
        } else {
            mUsernameEditText.clearFocus();
            mUsernameLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (mPasswordEditText.getText().toString().trim().isEmpty()) {
            mPasswordLayout.setError("Enter FTP Password");
            requestFocus(mPasswordEditText);
            return false;
        } else {
            mPasswordEditText.clearFocus();
            mPasswordLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDirectory() {
        if (mDirectoryEditText.getText().toString().trim().isEmpty()) {
            mDirectoryLayout.setError("Enter FTP Directory path");
            requestFocus(mDirectoryEditText);
            return false;
        } else {
            mDirectoryEditText.clearFocus();
            mDirectoryLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePort() {
        if (mPortEditText.getText().toString().trim().isEmpty()) {
            mPortLayout.setError("Enter FTP Port");
            requestFocus(mPortEditText);
            return false;
        } else {
            mPortEditText.clearFocus();
            mPortLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
