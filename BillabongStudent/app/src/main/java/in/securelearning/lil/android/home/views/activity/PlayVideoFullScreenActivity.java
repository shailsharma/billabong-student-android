package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityPlayResourceBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.SearchModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

public class PlayVideoFullScreenActivity extends AppCompatActivity {
    @Inject
    SearchModel mSearchModel;
    public static final String RESOURCE = "resource";
    public static final String APP_DATA = "app_data";
    public static final String NETWORK_TYPE = "network_type";
    public static final String NETWORK_TYPE_LOCAL = "network_type_local";
    public static final String NETWORK_TYPE_FTP = "network_type_ftp";
    public static final String NETWORK_TYPE_ONLINE = "network_type_online";

    private ActivityPlayResourceBinding mBinding;

    private int TIMEOUT = 5;
    private String mNetworkType = "";
    private Resource mResource = new Resource();

    public static Intent getStartActivityIntent(Context context, String network_type, Resource resource) {
        Intent intent = new Intent(context, PlayVideoFullScreenActivity.class);
        Bundle appData = new Bundle();
        appData.putString(NETWORK_TYPE, network_type);
        appData.putSerializable(RESOURCE, resource);
        intent.putExtra(APP_DATA, appData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_resource);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding.imageView.setVisibility(INVISIBLE);
        mBinding.videoView.setVisibility(INVISIBLE);
        mBinding.playProgressBar.bringToFront();
        handleIntent(getIntent());

    }

    private void handleIntent(Intent intent) {
        Bundle appData = getIntent().getBundleExtra(APP_DATA);
        if (appData != null) {
            mNetworkType = appData.getString(NETWORK_TYPE);
            mResource = (Resource) appData.getSerializable(RESOURCE);
        }
        if (mNetworkType.equals(NETWORK_TYPE_FTP)) {
            Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> subscriber) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "tempLil", mResource.getName());
                    if (file.exists() && file.isFile() && file.length() == mResource.getSize()) {
                        subscriber.onNext("success");
                    } else {
                        if (mSearchModel.copyFromFtp(mResource, Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "tempLil", mResource.getName())) {
                            subscriber.onNext("success");
                        } else {
                            subscriber.onNext("fail");
                        }
                    }
                }
            });
            final Disposable Disposable = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String string) {

                            if (string.equals("success")) {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "tempLil", mResource.getName());
                                mResource.setUrlMain(file.getAbsolutePath());
                                showResource(mResource);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        } else {
            showResource(mResource);
        }

    }

    private void showResource(Resource resource) {
        if (resource.getType().toLowerCase().contains("image")) {
            mBinding.imageView.setVisibility(View.VISIBLE);
            mBinding.videoView.setVisibility(GONE);

            File file = new File(resource.getUrlMain());
            if (file.exists() && file.isFile()) {
                Picasso.with(this).load(file).into(mBinding.imageView);
            } else {
                Picasso.with(this).load(resource.getUrlMain()).into(mBinding.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mBinding.playProgressBar.setVisibility(GONE);
                        mBinding.imageView.bringToFront();
                    }

                    @Override
                    public void onError() {
                        mBinding.playProgressBar.setVisibility(GONE);
                        mBinding.imageView.bringToFront();
                        ToastUtils.showToastAlert(getBaseContext(), "Could not Load");
                        finish();
                    }
                });
            }

        } else if (resource.getType().toLowerCase().contains("video")) {
            mBinding.imageView.setVisibility(GONE);
            mBinding.videoView.setVisibility(View.VISIBLE);
            mBinding.playProgressBar.setVisibility(GONE);
            mBinding.videoView.bringToFront();
            Bitmap bitmap = getScaledBitmapFromPath(getBaseContext().getResources(), resource.getUrlMain());
            bitmap = Bitmap.createScaledBitmap(bitmap, 600, 340, false);
            mBinding.videoView.thumbImageView.setImageBitmap(bitmap);
            mBinding.videoView.setUp(resource.getUrlMain(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, resource.getName());
            mBinding.videoView.startButton.performClick();

        } else {
            ToastUtils.showToastAlert(this, "Resource not supported");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
