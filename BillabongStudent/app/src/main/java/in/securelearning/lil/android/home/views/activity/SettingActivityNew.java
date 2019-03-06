//package in.securelearning.lil.android.home.views.activity;
//
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.databinding.DataBindingUtil;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.CompoundButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import in.securelearning.lil.android.app.BuildConfig;
//import in.securelearning.lil.android.app.R;
//import in.securelearning.lil.android.app.databinding.LayoutSettingNewBinding;
//import in.securelearning.lil.android.base.dataobjects.SettingUtilClass;
//import in.securelearning.lil.android.home.InjectorHome;
//import in.securelearning.lil.android.home.model.HomeModel;
//import in.securelearning.lil.android.refreshToken.views.activity.LoginActivity;
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
//
//public class SettingActivityNew extends Activity implements
//        CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
//    private LayoutSettingNewBinding sBinding;
//    @Inject
//    HomeModel mHomeModel;
//    private String mWebUrl = "http://securelearning.in";
//    private ArrayAdapter<String> dataAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        InjectorHome.INSTANCE.getComponent().inject(this);
//        super.onCreate(savedInstanceState);
//        sBinding = DataBindingUtil.setContentView(this, R.layout.layout_setting_new);
//        getWindow().setStatusBarColor(ContextCompat.getColor(SettingActivityNew.this, R.color.colorPrimary));
//        initializeViews();
//        initializeAdapterForSpinner();
//        getDefaultSettingValue();
//    }
//
//    private void getDefaultSettingValue() {
//        Observable.create(new ObservableOnSubscribe<SettingUtilClass>() {
//            @Override
//            public void subscribe(ObservableEmitter<SettingUtilClass> e) throws Exception {
//                SettingUtilClass setting = mHomeModel.getSetting();
//                if (setting != null) {
//                    e.onNext(setting);
//                }
//                e.onComplete();
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<SettingUtilClass>() {
//            @Override
//            public void accept(SettingUtilClass setting) throws Exception {
//                String icon_language_c = setting.getLanguage();
//                // Here we are setting default values in view
//                sBinding.settingDownloadWifi.setChecked(setting.isDownloadOnwifi());
//                sBinding.settingDownloadMdata.setChecked(setting.isMediaAutoDownload());
//                if (!icon_language_c.equals(null)) {
//                    int spinnerPosition = dataAdapter.getPosition(icon_language_c);
//                    sBinding.settingLanguageSpinner.setSelection(spinnerPosition);
//                }
//            }
//        });
//    }
//
//    private void initializeViews() {
//        sBinding.settingDownloadWifi.setOnCheckedChangeListener(this);
//        sBinding.settingDownloadMdata.setOnCheckedChangeListener(this);
//        sBinding.layoutSettingStorage.setOnClickListener(this);
//        sBinding.layoutSettingAccount.setOnClickListener(this);
//        sBinding.layoutSettingNotification.setOnClickListener(this);
//        sBinding.layoutSettingHelp.setOnClickListener(this);
//        sBinding.layoutSettingShare.setOnClickListener(this);
//        sBinding.layoutSettingAbout.setOnClickListener(this);
//        sBinding.layoutSettingRate.setOnClickListener(this);
//        sBinding.layoutSettingLanguage.setOnClickListener(this);
//        sBinding.settingLanguageSpinner.setOnItemSelectedListener(this);
//        sBinding.settingVersion.setText(getString(R.string.setting_version) + ": " + BuildConfig.VERSION_NAME);
//        sBinding.imagebuttonBack.setOnClickListener(this);
//        sBinding.btnSettingLogout.setOnClickListener(this);
//    }
//
//    private void initializeAdapterForSpinner() {
//        List<String> categories = new ArrayList<>();
//        categories.add("English");
////        categories.add("Hindi");
//        // Creating adapter for spinner
//        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
//        // Drop down layout style - list view with icon_radio_button_multiple button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // attaching data adapter to spinner
//        sBinding.settingLanguageSpinner.setAdapter(dataAdapter);
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        SettingUtilClass setting = mHomeModel.getSetting();
//        switch (buttonView.getId()) {
//            case R.id.setting_download_wifi:
//                setting.setDownloadOnwifi(isChecked);
//                Log.i("setting_download_wifi", isChecked + "");
//                break;
//
//            case R.id.setting_download_mdata:
//                setting.setMediaAutoDownload(isChecked);
//                Log.i("setting_download_mdata", isChecked + "");
//                break;
//        }
//        mHomeModel.saveSetting(setting);
//    }
//
//
//    @Override
//    public void onClick(View view) {
////        mSetting = mHomeModel.saveSetting(mSetting);
//        Intent intent;
//        switch (view.getId()) {
//            case R.id.layout_setting_storage:
//                intent = new Intent(SettingActivityNew.this, SettingStorageActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.layout_setting_account:
//                intent = new Intent(SettingActivityNew.this, SettingAccountActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.layout_setting_notification:
//                intent = new Intent(SettingActivityNew.this, SettingNotificationActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.layout_setting_help:
//                intent = new Intent(SettingActivityNew.this, SettingHelpActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.layout_setting_about:
//                intent = new Intent(SettingActivityNew.this, SettingAboutActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.layout_setting_language:
//                sBinding.settingLanguageSpinner.performClick();
//                break;
//
//            case R.id.layout_setting_rate:
//                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }
//                break;
//            case R.id.btn_setting_logout:
//                intent = LoginActivity.getLogoutIntent(this);
//                startActivity(intent);
//                finishAffinity();
//                int pendingIntentId = 123456;
//                PendingIntent mPendingIntent = PendingIntent.getActivity(this, pendingIntentId, intent,
//                        PendingIntent.FLAG_CANCEL_CURRENT);
//                AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                System.exit(0);
//                break;
//
//            case R.id.layout_setting_share:
//                intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, mWebUrl);
//                intent.putExtra(Intent.EXTRA_TEXT, mWebUrl);
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "Send to"));
//                break;
//
//            case R.id.imagebutton_back:
//                onBackPressed();
//                break;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        mSetting = mHomeModel.saveSetting(mSetting);
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        // On selecting a spinner item
//        String item = adapterView.getItemAtPosition(i).toString();
//
//        // Showing selected spinner item
////        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
//}
