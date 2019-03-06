package in.securelearning.lil.android.home.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.utils.DateUtils;

/**
 * Created by Chaitendra on 3/21/2017.
 */
public class CalendarPeriodDetailActivity extends AppCompatActivity {

    private PeriodNew mPeriodNew;
    private ImageButton mBackButton;
    private TextView mPeriodNumberTextView, mPeriodNameTextView, mPeriodTopicTextView, mPeriodTeacherNameTextView,
            mPeriodDateTextView, mPeriodDurationTextView, mPeriodLocationTextView;
    private ImageView mPeriodIconImageView, mPeriodTeacherPicImageView;
    private String strPeriodDate;
    private int intPeriodIcon;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar_period_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPeriod));
        mPeriodNew = (PeriodNew) getIntent().getSerializableExtra("selectedPeriod");
        strPeriodDate = getIntent().getExtras().getString("periodDate");
        intPeriodIcon = getIntent().getExtras().getInt("periodIcon");
        initializeViews();
        initializeUiAndClickListeners();
    }

    private void initializeViews() {
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mPeriodNumberTextView = (TextView) findViewById(R.id.textView_period_number);
        mPeriodNameTextView = (TextView) findViewById(R.id.textView_period_name);
        mPeriodTopicTextView = (TextView) findViewById(R.id.textView_period_topic);
        mPeriodLocationTextView = (TextView) findViewById(R.id.textView_period_location);
        mPeriodTeacherNameTextView = (TextView) findViewById(R.id.textView_period_teacher_name);
        mPeriodDurationTextView = (TextView) findViewById(R.id.textView_period_duration);
        mPeriodDateTextView = (TextView) findViewById(R.id.textView_period_date);
        mPeriodIconImageView = (ImageView) findViewById(R.id.imageView_period_icon);
        mPeriodTeacherPicImageView = (ImageView) findViewById(R.id.imageView_period_teacher_pic);
    }

    private void initializeUiAndClickListeners() {
        mPeriodNumberTextView.setText(String.valueOf(mPeriodNew.getPeriodNo()));
        mPeriodNameTextView.setText(mPeriodNew.getSubject().getName());
        mPeriodDateTextView.setText(strPeriodDate);
        mPeriodIconImageView.setImageResource(intPeriodIcon);
        mPeriodTopicTextView.setText(mPeriodNew.getTopic().getName());
        mPeriodTeacherNameTextView.setText(mPeriodNew.getTeacher().getName());

        mPeriodDurationTextView.setText(getTimeFromMilliSeconds(DateUtils.convertrIsoDate(mPeriodNew.getStartTime()).getTime()) + " - " + getTimeFromMilliSeconds(DateUtils.convertrIsoDate(mPeriodNew.getEndTime()).getTime()));

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public String getTimeFromMilliSeconds(long milli) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String dateString = formatter.format(new Date(milli));
        return dateString;

    }
}
