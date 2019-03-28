package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutFullImageBinding;

public class PlayFullScreenImageActivity extends AppCompatActivity {

    LayoutFullImageBinding mBinding;
    public static final String URL = "url";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_full_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        handleIntent();
        setTitle("");
        mBinding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String url = getIntent().getStringExtra(URL);
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(getBaseContext()).load(url).placeholder(R.drawable.image_placeholder).into(mBinding.imageViewUserImage);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public static Intent getStartIntent(Context context, String url) {
        Intent intent = new Intent(context, PlayFullScreenImageActivity.class);
        intent.putExtra(URL, url);
        return intent;
    }

}
