package in.securelearning.lil.android.home.views.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSettingHelpBinding;

public class SettingHelpActivity extends AppCompatActivity implements View.OnClickListener {
    private LayoutSettingHelpBinding sBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sBinding = DataBindingUtil.setContentView(this, R.layout.layout_setting_help);
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingHelpActivity.this, R.color.colorPrimary));
        sBinding.imagebuttonBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imagebutton_back:
                onBackPressed();
        }
    }
}
