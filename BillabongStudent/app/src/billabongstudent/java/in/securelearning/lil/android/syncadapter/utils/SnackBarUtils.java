package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import in.securelearning.lil.android.app.R;

/**
 * Created by Chaitendra on 23-Aug-17.
 */

public class SnackBarUtils {

    /*  0 - False  - Unsuccessful - Red - #FF0000
     *  1 - True - Successful - Green - #61BD4F
     *  2 - If want primary color as default - colorPrimary*/
    public static final int UNSUCCESSFUL = 0;
    public static final int SUCCESSFUL = 1;
    public static final int PRIMARY_DEFAULT = 2;


    public static SnackBarUtils mSnackbarUtil;

    public static SnackBarUtils getInstance() {
        if (mSnackbarUtil == null) {
            mSnackbarUtil = new SnackBarUtils();

        }
        return mSnackbarUtil;
    }

    public static void showColoredSnackBar(Context context, View view, String message, int color) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(color);
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }


    /**
     * @param context
     * @param view    Provide root view to show snack bar on OR The view to find a parent from.
     * @param message Provide message to show in snack bar
     * @param type    Type of message. Types are {#UNSUCCESSFUL}, {#SUCCESSFUL} or {#PRIMARY_DEFAULT}.
     *                Default type is {#PRIMARY_DEFAULT}
     */
    public static void showSnackBar(Context context, View view, String message, int type) {


        int snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        if (type == UNSUCCESSFUL) {
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorRed);
        } else if (type == SUCCESSFUL) {
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorGreen);
        } else if (type == PRIMARY_DEFAULT) {
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        }

        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(snackBarBackgroundColor);
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showSuccessSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenDark));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showAlertSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showNoInternetSnackBar(Context context, View view) {
        Snackbar snackbar = null;
        //// TODO: 07-Nov-17  change internet message when branch change
        snackbar = Snackbar.make(view, context.getString(R.string.connect_internet), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }
    // parameter are 1. image,
    // 2. msg -dynamic so will take arraylist of string
    // 3. background color
    // 4. image position right or left
    //5. View in which we have to attach the snackbar


}
