package in.securelearning.lil.android.syncadapter.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProgressBinding;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.DateUtils;

public class StudentCommonUtils {

    private static StudentCommonUtils mCommonUtil;


    public static StudentCommonUtils getInstance() {
        if (mCommonUtil == null) {
            mCommonUtil = new StudentCommonUtils();

        }
        return mCommonUtil;
    }

    public String firstLetterCapital(String line) {
        if (line != null && line !=StudentConstantUtil.BLANK) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        }
        return line;
    }

    public String getDate(String date) {
        if (date != null) {
            String date1 = null;
            date1 = new SimpleDateFormat("dd MMMM, yy").format(DateUtils.convertrIsoDate(date));
            return date1;
        } else return StudentConstantUtil.BLANK;
    }

    /*Common function for creating pie chart
     * Entry Arraylist ,label,tring arraylist
     *
     * */
    public PieChart drawHomeWorkPieChart(Activity activity, PieChart chart, List<PieEntry> entryArrayList, ArrayList<String> xAxisArrayList, int[] colorArray, String label) {
        PieDataSet dataSet = new PieDataSet(entryArrayList, "");
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //dataSet.setColors(new int[]{R.color.colorSubmittedHomework, R.color.colorPendingHomework}, activity);
        dataSet.setColors(colorArray, activity);
        dataSet.setSliceSpace(2f);
        PieData data = new PieData();
        data.setValueFormatter(new PercentFormatter());
        chart.setData(data);
        chart.setDrawHoleEnabled(false);
        chart.setHoleRadius(0f);

        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        chart.invalidate();
        chart.setClickable(true);
        chart.setTouchEnabled(true);
        chart.setRotationEnabled(false);
        chart.setDrawSliceText(false);

        chart.getLegend().setWordWrapEnabled(true);
        chart.animateXY(1400, 1400);
        return chart;
    }

    public void setUserThumbnail(Context context, String name, Thumbnail thumbnail, AppCompatImageView imageView) {
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
            Picasso.with(context).load(thumbnail.getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(context).load(thumbnail.getUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(context).load(thumbnail.getThumb()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
            Picasso.with(context).load(thumbnail.getThumbXL()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else {
            String firstWord = name.substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            imageView.setImageDrawable(textDrawable);
        }
    }


    /*Set activity status bar style immersive*/
    public void setImmersiveStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /*An alert dialog, which contains progress text*/
    public Dialog loadingDialog(Context context, String message) {
        LayoutProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_progress, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());
        binding.textViewLoading.setText(message);

        dialog.show();
        return dialog;
    }

    public String getClassWithGrade(String grade, String section) {
        final StringBuilder myClass = new StringBuilder();
        if (!TextUtils.isEmpty(grade)) {
            myClass.append(grade);
            if (!TextUtils.isEmpty(section)) {
                myClass.append("-");
                myClass.append(section);
            }

        }
        return myClass.toString();
    }

    public float setTextSize(String grade, Context context) {
        float mDisplayDensity = context.getResources().getDisplayMetrics().density;
        if (!TextUtils.isEmpty(grade)) {
            if (grade.length() > 4) {
                return (context.getResources().getDimension(R.dimen.textSize_18sp) / mDisplayDensity);

            } else {
                return (context.getResources().getDimension(R.dimen.textSize_48sp) / mDisplayDensity);

            }

        }
        return (context.getResources().getDimension(R.dimen.textSize_18sp) / mDisplayDensity);
    }
    public String showTextTwoDecimal(float value)
    {
        if(value!=0f)
        {

            return String.format ("%.2f", value);
        }
        else
            return StudentConstantUtil.ZERO;

    }
    public String showTextTwoDecimalDouble(double value)
    {
        DecimalFormat df = new DecimalFormat("#.##");

            return df.format(value);
        }


    public String convertSecondToMinute(long actualSeconds) {
        String strMinutes = "";

        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        /*This is to convert into a minute, if seconds value is >= 30 seconds else seconds
        value will become zero as discussed*/
        if (seconds >= 30) {
            minutes = minutes + 1;
        }

        strMinutes = String.valueOf(minutes);

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }


        return strMinutes;
    }

    public void setGroupThumbnail(Context context, String name, Thumbnail thumbnail, AppCompatImageView imageView) {
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
            Picasso.with(context).load(thumbnail.getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(context).load(thumbnail.getUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(context).load(thumbnail.getThumb()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
            Picasso.with(context).load(thumbnail.getThumbXL()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
        } else {
            String firstWord = name.substring(0,2).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            imageView.setImageDrawable(textDrawable);
        }
    }

    /*convert seconds to natural time format*/
    public String convertSecondToHourMinuteSecond(long actualSeconds) {
        String strHours = "";
        String strMinutes = "";

        long hours = actualSeconds / 3600;
        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        /*This is to convert into a minute, if seconds value is >= 30 seconds else seconds
        value will become zero as discussed*/
        if (seconds >= 30) {
            minutes = minutes + 1;
        }

        strHours = String.valueOf(hours);
        strMinutes = String.valueOf(minutes);
        // strSeconds = String.valueOf(newSeconds);


        if (hours >= 0 && hours <= 9) {
            strHours = "0" + strHours;
        }

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }
//        if (newSeconds >= 0 && newSeconds <= 9) {
//            strSeconds = "0" + strSeconds;
//
//        }

        return strHours + ":" + strMinutes; // + ":" + strSeconds;
    }

}
