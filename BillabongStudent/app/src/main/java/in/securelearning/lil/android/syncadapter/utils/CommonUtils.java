package in.securelearning.lil.android.syncadapter.utils;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProgressBinding;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.DateUtils;

public class CommonUtils {

    private static CommonUtils mCommonUtil;

    public static CommonUtils getInstance() {
        if (mCommonUtil == null) {
            mCommonUtil = new CommonUtils();

        }
        return mCommonUtil;
    }

    public String firstLetterCapital(String line) {
        if (line != null && !line.equals(ConstantUtil.BLANK)) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        }
        return line;
    }

    public String getDate(String date) {
        if (date != null) {
            String date1 = null;
            date1 = new SimpleDateFormat("dd MMMM, yy").format(DateUtils.convertrIsoDate(date));
            return date1;
        } else return ConstantUtil.BLANK;
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

    public String showTextTwoDecimal(float value) {
        if (value != 0f) {

            return String.format("%.2f", value);
        } else
            return ConstantUtil.ZERO;

    }

    public String showTextTwoDecimalDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.##");

        return df.format(value);
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
            String firstWord = name.substring(0, 2).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            imageView.setImageDrawable(textDrawable);
        }
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

    public GradientDrawable getGradientDrawableFromSingleColor(int color) {
        int lightColor = manipulateColor(color, 0.7f);
        int[] colors = {lightColor, color};

        return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);

    }


    public int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }


    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public String getNextDayDate() {


        String nextDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = format.parse(getCurrentTime());
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = format.format(today.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }


    public boolean getDateDiff(String eventDate) {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date dEvent = null;
        Date dSystem = null;
        try {
            dEvent = format.parse(eventDate);
            dSystem = format.parse(getCurrentTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dSystem != null && dEvent != null) {
            long diff = dSystem.getTime() - dEvent.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            if (diffHours >= 24) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String showSecondAndMinutesAndHours(long actualSeconds) {
        String strMinutes, strSeconds, strHours;


        long hours = actualSeconds / 3600;
        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        strHours = String.valueOf(hours);
        strMinutes = String.valueOf(minutes);
        strSeconds = String.valueOf(seconds);


        if (hours >= 0 && hours <= 9) {
            strHours = "0" + strHours;
        }

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }
        if (seconds >= 0 && seconds <= 9) {
            strSeconds = "0" + strSeconds;

        }

        return strHours + ":" + strMinutes + ":" + strSeconds;
    }
}


