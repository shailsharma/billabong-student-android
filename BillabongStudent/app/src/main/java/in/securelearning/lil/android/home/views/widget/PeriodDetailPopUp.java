package in.securelearning.lil.android.home.views.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.AppUser;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.RoundedImageView;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;

/**
 * Created by Chaitendra on 4/11/2017.
 */

public class PeriodDetailPopUp {


    public static String getTimeFromMilliSeconds(long milli) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String dateString = formatter.format(new Date(milli));
        return dateString;


    }

    public static HashMap<String, Category> getSubjectMap(final Context context) {
        final HashMap<String, Category> subjectMap = new HashMap<>();
        ArrayList<Category> categories = PrefManager.getCategoryList(context);
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            subjectMap.put(category.getId(), category);
        }

        return subjectMap;
    }

    public static void showPeriodDetail(final Context context, View v, final PeriodNew period, AppUser.USERTYPE strUserType, String periodDate, Integer periodColor) {
        int[] loc_int = new int[2];
        v.getLocationOnScreen(loc_int);
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_calendar_period_detail, null);
        RelativeLayout mToolbarRelativeLayout = (RelativeLayout) layout.findViewById(R.id.layout_header);
        ImageButton mBackButton = (ImageButton) layout.findViewById(R.id.button_back);
        TextView mPeriodNumberTextView = (TextView) layout.findViewById(R.id.textView_period_number);
        TextView mPeriodNameTextView = (TextView) layout.findViewById(R.id.textView_period_name);
        TextView mPeriodTopicTextView = (TextView) layout.findViewById(R.id.textView_period_topic);
        TextView mPeriodLocationTextView = (TextView) layout.findViewById(R.id.textView_period_location);
        TextView mPeriodClassTextView = (TextView) layout.findViewById(R.id.textView_class);
        LinearLayout mClassLayout = (LinearLayout) layout.findViewById(R.id.layout_class);
        LinearLayout mTeacherLayout = (LinearLayout) layout.findViewById(R.id.layout_teacher);
        TextView mPeriodTeacherNameTextView = (TextView) layout.findViewById(R.id.textView_period_teacher_name);
        TextView mPeriodDurationTextView = (TextView) layout.findViewById(R.id.textView_period_duration);
        TextView mPeriodDateTextView = (TextView) layout.findViewById(R.id.textView_period_date);
        ImageView mPeriodIconImageView = (ImageView) layout.findViewById(R.id.imageView_period_icon);
        RoundedImageView mPeriodTeacherPicImageView = (RoundedImageView) layout.findViewById(R.id.imageView_period_teacher_pic);
        ImageButton mReferenceMaterialButton = (ImageButton) layout.findViewById(R.id.btn_r_material);
        View mReferenceMaterialView = (View) layout.findViewById(R.id.viewReferenceMaterial);
        mReferenceMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = ResourceListActivity.getIntentForTopicBrowse(context, period.getSubject().getId(), period.getSubjectIds(), period.getTopic().getId(), period.getGrade().getId(), "Reference material for " + period.getTopic().getName());
//                context.startActivity(i);

            }
        });

        mToolbarRelativeLayout.setBackgroundColor(periodColor);
        mReferenceMaterialView.setBackgroundColor(periodColor);
        //mReferenceMaterialButton.setTextColor(periodColor);
        mPeriodNumberTextView.setText(String.valueOf(period.getPeriodNo()));
        mPeriodNameTextView.setText(period.getSubject().getName());
        mPeriodDateTextView.setText(periodDate);
        //  mPeriodIconImageView.setImageResource(periodIcon);
        mPeriodTopicTextView.setText(period.getTopic().getName());
        setPeriodTime(period, mPeriodDurationTextView);

        if (strUserType.equals(AppUser.USERTYPE.TEACHER)) {
            mTeacherLayout.setVisibility(View.GONE);
            mClassLayout.setVisibility(View.VISIBLE);
            mPeriodClassTextView.setText(period.getGrade().getName() + " - " + period.getSection().getName());
        } else {
            mTeacherLayout.setVisibility(View.VISIBLE);
            mClassLayout.setVisibility(View.GONE);
            if (period.getTeacher() != null) {
                mPeriodTeacherNameTextView.setText(period.getTeacher().getName());
                if (period.getTeacher().getThumbnail() != null) {
                    if (period.getTeacher().getThumbnail().getUrl() != null && !period.getTeacher().getThumbnail().getUrl().isEmpty()) {
                        Picasso.with(context).load(period.getTeacher().getThumbnail().getUrl()).placeholder(R.drawable.icon_profile_large).resize(200, 200).centerCrop().into(mPeriodTeacherPicImageView);
                    } else if (period.getTeacher().getThumbnail().getThumb() != null && !period.getTeacher().getThumbnail().getThumb().isEmpty()) {
                        Picasso.with(context).load(period.getTeacher().getThumbnail().getThumb()).placeholder(R.drawable.icon_profile_large).resize(200, 200).centerCrop().into(mPeriodTeacherPicImageView);
                    } else {
                        String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, periodColor);
                        mPeriodTeacherPicImageView.setImageDrawable(textDrawable);
                    }
                } else {
                    String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                    TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, periodColor);
                    mPeriodTeacherPicImageView.setImageDrawable(textDrawable);
                }
            }
        }

        mPeriodTeacherPicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(context)) {
                    Intent mIntent = UserProfileActivity.getStartIntent(period.getTeacher().getId(), context);
                    context.startActivity(mIntent);
                } else {
                    ToastUtils.showToastAlert(context, context.getString(R.string.connect_internet));
                }
            }
        });

        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int popupWidth = ViewGroup.LayoutParams.MATCH_PARENT;

        final PopupWindow popupPeriod = new PopupWindow(context);
        popupPeriod.setContentView(layout);
        popupPeriod.setWidth(popupWidth);
        popupPeriod.setHeight(popupHeight);
        popupPeriod.setFocusable(true);
        popupPeriod.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupPeriod.setOutsideTouchable(true);
        popupPeriod.setAnimationStyle(android.R.style.Animation_Dialog);
        popupPeriod.showAtLocation(v, Gravity.NO_GRAVITY, Gravity.CENTER_HORIZONTAL, location.bottom);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupPeriod.dismiss();
            }
        });
    }

    private static void setPeriodTime(PeriodNew period, TextView periodDurationTextView) {
        if (!TextUtils.isEmpty(period.getStartTime()) && !TextUtils.isEmpty(period.getEndTime())) {
            String periodStartTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getStartTime()).getTime());
            String periodEndTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getEndTime()).getTime());
            periodDurationTextView.setText(periodStartTime + " - " + periodEndTime);
        } else {
            periodDurationTextView.setText("");
        }
    }

}
