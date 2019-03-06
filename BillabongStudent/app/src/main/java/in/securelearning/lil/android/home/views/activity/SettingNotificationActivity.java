//package in.securelearning.lil.android.home.views.activity;
//
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
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
//import in.securelearning.lil.android.app.R;
//import in.securelearning.lil.android.app.databinding.LayoutSettingNotificationBinding;
//import in.securelearning.lil.android.base.dataobjects.AppUser;
//import in.securelearning.lil.android.base.model.AppUserModel;
//import in.securelearning.lil.android.home.InjectorHome;
//import in.securelearning.lil.android.home.model.HomeModel;
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * Created by lenovo on 3/25/2017.
// */
//
//public class SettingNotificationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
//    LayoutSettingNotificationBinding mBinding;
//    @Inject
//    HomeModel mHomeModel;
//    @Inject
//    AppUserModel mAppUserModel;
//    private ArrayAdapter<Integer> busArrivingAdapter;
//    private ArrayAdapter<Integer> busHasArrivedAdapter;
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        InjectorHome.INSTANCE.getComponent().inject(this);
//        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_setting_notification);
//        getWindow().setStatusBarColor(ContextCompat.getColor(SettingNotificationActivity.this, R.color.colorPrimary));
//        initializeListeners();
//        if (mAppUserModel.getApplicationUser().getUserType().equals(AppUser.USERTYPE.TEACHER)) {
//            mBinding.relNSetting.setVisibility(View.GONE);
//        }else{
//            mBinding.relNSetting.setVisibility(View.VISIBLE);
//        }
//        initializeAdapterForSpinner();
//        getDefaultSettingValue();
//    }
//
//    private void initializeAdapterForSpinner() {
//        List<Integer> b_arriving = new ArrayList<>();
//        b_arriving.add(500);
//        b_arriving.add(800);
//        b_arriving.add(1000);
//        busArrivingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, b_arriving);
//        busArrivingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mBinding.settingBusArriving.setAdapter(busArrivingAdapter);
//
//        List<Integer> b_has_arrived = new ArrayList<>();
//        b_has_arrived.add(50);
//        b_has_arrived.add(100);
//        b_has_arrived.add(200);
//        busHasArrivedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, b_has_arrived);
//        busHasArrivedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mBinding.settingBusHasArrive.setAdapter(busHasArrivedAdapter);
//    }
//
//    private void initializeListeners() {
//        mBinding.txtNotificationSound.setOnCheckedChangeListener(this);
//        mBinding.settingNotificationCourses.setOnCheckedChangeListener(this);
//        mBinding.settingNotificationLnetwork.setOnCheckedChangeListener(this);
//        mBinding.settingNotificationAssignment.setOnCheckedChangeListener(this);
//        mBinding.settingBusArriving.setOnItemSelectedListener(this);
//        mBinding.settingBusHasArrive.setOnItemSelectedListener(this);
//        mBinding.imagebuttonBack.setOnClickListener(this);
//        mBinding.laySettingBus.setOnClickListener(this);
//        mBinding.laySettingBusHasArrive.setOnClickListener(this);
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
//                mBinding.txtNotificationSound.setChecked(setting.isNotificationSound());
//                mBinding.settingNotificationCourses.setChecked(setting.isCourses());
//                mBinding.settingNotificationLnetwork.setChecked(setting.isLearningNetwork());
//                mBinding.settingNotificationAssignment.setChecked(setting.isAssignment());
//                int bus_arrive = setting.getBus_arriving();
//                int bus_has_arrived = setting.getBus_has_arrived();
//                if (bus_arrive != 0) {
//                    int spinnerPosition = busArrivingAdapter.getPosition(bus_arrive);
//                    mBinding.settingBusArriving.setSelection(spinnerPosition);
//                }
//                if (bus_has_arrived != 0) {
//                    int spinnerPosition = busHasArrivedAdapter.getPosition(bus_has_arrived);
//                    mBinding.settingBusHasArrive.setSelection(spinnerPosition);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//        SettingUtilClass setting = mHomeModel.getSetting();
//        switch (compoundButton.getId()) {
//            case R.id.txt_notification_sound:
//                setting.setNotificationSound(b);
//                break;
//
//            case R.id.setting_notification_courses:
//                setting.setCourses(b);
//                break;
//
//            case R.id.setting_notification_lnetwork:
//                setting.setLearningNetwork(b);
//                break;
//
//            case R.id.setting_notification_assignment:
//                setting.setAssignment(b);
//                break;
//        }
//        mHomeModel.saveSetting(setting);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.imagebutton_back:
//                super.onBackPressed();
//                finish();
//                break;
//            case R.id.lay_setting_bus:
//                mBinding.settingBusArriving.performClick();
//                break;
//            case R.id.lay_setting_bus_has_arrive:
//                mBinding.settingBusHasArrive.performClick();
//                break;
//        }
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        SettingUtilClass setting = mHomeModel.getSetting();
//        switch (parent.getId()) {
//            case R.id.setting_bus_arriving:
//                int item = (int) parent.getItemAtPosition(position);
//                setting.setBus_arriving(item);
//                break;
//            case R.id.setting_bus_has_arrive:
//                int item1 = (int) parent.getItemAtPosition(position);
//                setting.setBus_has_arrived(item1);
//                break;
//        }
//        mHomeModel.saveSetting(setting);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//}
