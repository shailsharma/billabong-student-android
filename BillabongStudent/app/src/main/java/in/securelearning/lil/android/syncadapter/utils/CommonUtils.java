package in.securelearning.lil.android.syncadapter.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

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


    /*alert dialog to show any error, message*/
    /*Context should be activity context only.*/
    public void showAlertDialog(@NonNull Context context, String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

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
            return ConstantUtil.STRING_ZERO;

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
//        if (seconds >= 30) {
//            minutes = minutes + 1;
//        }

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
        int centerColor = lighter(color, 0.2f);
        int endColor = lighter(color, 0.3f);
        int[] colors = {color, centerColor, endColor};

        return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);

    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color  The color to lighten
     * @param factor The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *               color white.
     * @return lighter version of the specified color.
     */
    private int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
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

    public int generateRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public String getNextDayDate(int days, int hours) {


        String nextDate = "";
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = format.parse(getCurrentTime());
            calendar.setTime(date);
            if (days != 0)
                calendar.add(Calendar.DAY_OF_YEAR, days);
            else if (hours != 0)
                calendar.add(Calendar.HOUR_OF_DAY, 5);
            nextDate = format.format(calendar.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }

    public String getNextDayISODate(int days, int hours) {


        String nextDate = "";
        String result1 = "";
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = format.parse(getCurrentTime());
            calendar.setTime(date);
            if (days != 0)
                calendar.add(Calendar.DAY_OF_YEAR, days);
            else if (hours != 0)
                calendar.add(Calendar.HOUR_OF_DAY, hours);
            nextDate = format.format(calendar.getTime());
            result1 = DateUtils.getISO8601DateStringFromDate(calendar.getTime());
            return result1;
        } catch (Exception e) {
            return nextDate;
        }

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

    public boolean checkDateRange(String eventStartDate, String eventEndDate) {

        Date dStartEvent = null;
        Date dEndEvent = null;
        Date dSystem = null;
        try {

            dStartEvent = DateUtils.convertrIsoDate(eventStartDate);
//            Log.e("dStartEvent", String.valueOf(dStartEvent));
            dEndEvent = DateUtils.convertrIsoDate(eventEndDate);
//            Log.e("dEndEvent", String.valueOf(dEndEvent));
            dSystem = new Date();
//            Log.e("dSystem", String.valueOf(dSystem));
            if (dSystem.getTime() > dStartEvent.getTime() && dSystem.getTime() < dEndEvent.getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean compareTwoDatesForSurvey(String eventDoneDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dEventDone = null;
        Date dSystem = null;
        try {
            dEventDone = format.parse(eventDoneDate);
            dSystem = format.parse(getCurrentTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Log.e("dSystem", String.valueOf(dSystem));
        if (dSystem.getDate() != dEventDone.getDate() && dSystem.getDate() > dEventDone.getDate() && CommonUtils.getInstance().getHoursOfDay() >= 15) {
            return true;
        }

        return false;
    }

    public boolean checkEventOccurrence(int eventFrequency, String frequencyUnit, String eventDate) {
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
            if (frequencyUnit.equalsIgnoreCase("days")) {
                if (diffHours >= 24 * eventFrequency) {
                    return true;
                } else {
                    return false;
                }
            } else if (frequencyUnit.equalsIgnoreCase("min")) {
                if (diffMinutes >= 60 * eventFrequency) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    public boolean checkEventOccurrenceForBonus(int eventFrequency, String frequencyUnit, String eventStartDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date dStartEvent = null;
        Date dSystem = null;
        try {
            dStartEvent = DateUtils.convertrIsoDate(eventStartDate);
//            Log.e("dStartEvent", String.valueOf(dStartEvent));

            dSystem = new Date();
//            Log.e("dSystem", String.valueOf(dSystem));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dSystem != null && dStartEvent != null) {
            long diff = dSystem.getTime() - dStartEvent.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            if (frequencyUnit.equalsIgnoreCase("days")) {
                if (diffHours >= 24 * eventFrequency) {
                    return true;
                } else {
                    return false;
                }
            } else if (frequencyUnit.equalsIgnoreCase("min")) {
                if (diffMinutes >= 60 * eventFrequency) {
                    return true;
                } else {
                    return false;
                }
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

    public String showSecondAndMinutesFromLong(long timeInMilliSeconds) {
        String strMinutes, strSeconds, strHours;

        long actualSeconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilliSeconds);

        long hours = actualSeconds / 3600;
        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        strHours = String.valueOf(hours);
        strMinutes = String.valueOf(minutes);
        strSeconds = String.valueOf(seconds);


        /*if (hours >= 0 && hours <= 9) {
            strHours = "0" + strHours;
        }*/

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }
        if (seconds >= 0 && seconds <= 9) {
            strSeconds = "0" + strSeconds;

        }

        return strMinutes + ":" + strSeconds;
    }

    public int getHoursOfDay() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);

    }

    public int getCurrentWeek() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.WEEK_OF_YEAR);
    }

    public String getChartWeekLabel(int weekNo) {
        int diff = getCurrentWeek() - weekNo;
        if (diff == 0) {
            return "This\n" +
                    "Week";
        } else if (diff == 1) {
            return "Last\n" +
                    "Week";
        } else if (diff > 1)
            return diff + " Weeks\n" +
                    "Ago";
        return null;
    }

    /**
     * To set status bar icons dark
     *
     * @param activityContext It must be Activity context
     */
    public void setStatusBarIconsDark(@NonNull Activity activityContext) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activityContext.getWindow().setStatusBarColor(Color.TRANSPARENT);
            View decor = activityContext.getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        } else {
            activityContext.getWindow().setStatusBarColor(ContextCompat.getColor(activityContext, R.color.colorGrey55));
        }

    }

    /**
     * To set status bar icons light
     *
     * @param activityContext It must be Activity context
     */
    public void setStatusBarIconsLight(@NonNull Activity activityContext) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activityContext.getWindow().setStatusBarColor(Color.TRANSPARENT);
            View decor = activityContext.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

        } else {
            activityContext.getWindow().setStatusBarColor(ContextCompat.getColor(activityContext, R.color.colorGrey55));
        }

    }

    /* Set activity status bar style immersive without coordinator layout and without fitSystemWindow=true
     * and custom status bar color*/
    public void setImmersiveUiWithoutFitSystemWindow(Window window, int statusBarColor) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        window.setStatusBarColor(statusBarColor);
    }

}


