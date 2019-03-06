package in.securelearning.lil.android.home.views.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSettingAccountBinding;

/**
 * Created by lenovo on 3/25/2017.
 */

public class SettingAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private LayoutSettingAccountBinding lBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lBinding = DataBindingUtil.setContentView(this, R.layout.layout_setting_account);
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingAccountActivity.this, R.color.colorPrimary));
        lBinding.imagebuttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imagebutton_back:
                onBackPressed();
                break;
        }
    }
}
