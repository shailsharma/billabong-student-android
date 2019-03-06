package in.securelearning.lil.android.home.views.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSettingStorageBinding;

/**
 * Created by Tikam on 3/24/2017.
 */

public class SettingStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private LayoutSettingStorageBinding sBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting_storage);
        sBinding = DataBindingUtil.setContentView(this, R.layout.layout_setting_storage);
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingStorageActivity.this, R.color.colorPrimary));
        sBinding.imagebuttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imagebutton_back:
                onBackPressed();
                break;
        }
    }
}