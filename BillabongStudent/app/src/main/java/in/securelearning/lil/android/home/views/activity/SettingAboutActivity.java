package in.securelearning.lil.android.home.views.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSettingAboutBinding;

/**
 * Created by lenovo on 3/25/2017.
 */

public class SettingAboutActivity extends AppCompatActivity implements View.OnClickListener {
    private LayoutSettingAboutBinding sBinding;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sBinding = DataBindingUtil.setContentView(this,R.layout.layout_setting_about);
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingAboutActivity.this, R.color.colorPrimary));
        sBinding.imagebuttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imagebutton_back:
                onBackPressed();
                break;
        }
    }

}
