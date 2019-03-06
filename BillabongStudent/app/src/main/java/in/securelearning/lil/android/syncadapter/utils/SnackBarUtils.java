package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import in.securelearning.lil.android.app.R;

/**
 * Created by Chaitendra on 23-Aug-17.
 */

public class SnackBarUtils {

    public static void showColoredSnackBar(Context context, View view, String message, int color) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(color);
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showSuccessSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenDark));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showAlertSnackBar(Context context, View view, String message) {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

    public static void showNoInternetSnackBar(Context context, View view) {
        Snackbar snackbar = null;
        //// TODO: 07-Nov-17  change internet message when branch change
        snackbar = Snackbar.make(view, context.getString(R.string.connect_internet), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        snackbar.show();
    }

}
