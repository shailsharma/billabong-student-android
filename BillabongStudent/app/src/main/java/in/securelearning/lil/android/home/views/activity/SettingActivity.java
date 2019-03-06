package in.securelearning.lil.android.home.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;

/**
 * Created by Cp on 11/15/2016.
 */
public class SettingActivity extends AppCompatActivity {

    String strfont = "", strTheme = "";
    String strSampleFont = "Your App font style";
    private String MY_PREFS_NAME = "LIL_pref";
    private RadioButton mRadioButton, mThemeRadioButton;
    private TextView mHeaderTextView;
    private Button mOkButton, mThemeOkButton;
    private RadioGroup mRadioGroup, mThemeRadioGroup;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        initializeViews();
        initializeUiandClickListeners();
        initializeFontSection();
        initializeThemeSection();

    }


    private void initializeViews() {

        mHeaderTextView = (TextView) findViewById(R.id.textview_header);
        mOkButton = (Button) findViewById(R.id.button_ok);
        mThemeOkButton = (Button) findViewById(R.id.button_ok_theme);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mThemeRadioGroup = (RadioGroup) findViewById(R.id.radio_group_theme);
    }

    private void initializeUiandClickListeners() {
    }

    private void initializeFontSection() {

        mHeaderTextView.setText("Select Font");
        final List<String> fontList = new ArrayList<>();
//        fontList.add(getString(R.string.roboto));
//        fontList.add(getString(R.string.seasrn));
//        fontList.add(getString(R.string.walkway));
//        fontList.add(getString(R.string.berantas));
//        fontList.add(getString(R.string.caviar_dreams));
//        fontList.add(getString(R.string.krona_one));
//        fontList.add(getString(R.string.modern_antiqua));
//        fontList.add(getString(R.string.neohellenic));
//        fontList.add(getString(R.string.obti_sans));


        for (int i = 0; i < fontList.size(); i++) {
            mRadioButton = new RadioButton(SettingActivity.this);
            Typeface tf = Typeface.createFromAsset(getAssets(), fontList.get(i));
            mRadioButton.setText(strSampleFont);
            mRadioButton.setTypeface(tf);
            mRadioGroup.addView(mRadioButton);
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton mRadioButton = (RadioButton) group.getChildAt(x);
                    if (mRadioButton.getId() == checkedId) {
                        strfont = fontList.get(x);
                    }
                }
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strfont.equals("") || strfont.isEmpty()) {
                    Toast.makeText(SettingActivity.this, "Please select a font style", Toast.LENGTH_LONG).show();
                } else {
                    appRestartAlertDialog();
                }
            }
        });

    }

    private void initializeThemeSection() {
        final List<String> themeList = new ArrayList<>();
        themeList.add("Course Blue");
        themeList.add("Course Grey");

        for (int i = 0; i < themeList.size(); i++) {
            mThemeRadioButton = new RadioButton(SettingActivity.this);
            mThemeRadioButton.setText(themeList.get(i));
            mThemeRadioGroup.addView(mThemeRadioButton);
        }

        mThemeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton mRadioButton = (RadioButton) group.getChildAt(x);
                    if (mRadioButton.getId() == checkedId) {
                        strTheme = themeList.get(x);
                    }
                }
            }
        });

        mThemeOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strTheme.equals("") || strTheme.isEmpty()) {
                    Toast.makeText(SettingActivity.this, "Please select a theme", Toast.LENGTH_LONG).show();
                } else {
                    appRestartAlertDialogforTheme();
                }
            }
        });
    }

    private void appRestartAlertDialogforTheme() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
        alertDialog.setMessage("App need restart to change theme");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("theme", strTheme);
                editor.commit();

                // restart app to change font style
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();


    }

    private void appRestartAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
        alertDialog.setMessage("App need restart to change font style");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("font", strfont);
                editor.commit();

                // restart app to change font style
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }


}
