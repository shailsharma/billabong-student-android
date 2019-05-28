package in.securelearning.lil.android.syncadapter.utils;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDynamicSnackbarMsgImageBinding;

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
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
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
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorRedAlert);
        } else if (type == SUCCESSFUL) {
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorGreenSuccessful);
        } else if (type == PRIMARY_DEFAULT) {
            snackBarBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        }

        Snackbar snackbar = null;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(snackBarBackgroundColor);
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
    // parameter are 1. image,
    // 2. msg -dynamic so will take arraylist of string
    // 3. background color
    // 4. image position right or left
    //5. View in which we have to attach the snackbar


    public void getSnackBarWithImage(Activity activity, View view, int imageId, String imgPosition, ArrayList<String> msgList, int colorId, int textColor, String msg) {

        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view

        View snackView = activity.getLayoutInflater().
                inflate(R.layout.layout_dynamic_snackbar_msg_image, null);
//        String msg = String.format(
//                " Welcome %1$s Your New Assignment count %2$s!\n  Pending Assignment count %3$s!\n OverDue Assignment count %4$s!" , msgList.get(0), msgList.get(1),"0","12");
//        if(msgList!=null && !msgList.isEmpty())
//        {
//            if(msgList.size()==1) {
//                finalMsg= String.format(finalMsg, msgList.get(0));
//            }
//
//
//        }

        TextView snackbarText = snackView.findViewById(R.id.snackbar_text);
        LinearLayout llMain = snackView.findViewById(R.id.llMain);
        ImageView imageRight = snackView.findViewById(R.id.imageRight);
        ImageView imageLeft = snackView.findViewById(R.id.imageLeft);
        // snackbarText.setTextColor(textColor);
        snackbarText.setTextColor(ContextCompat.getColor(activity, textColor));
        snackbarText.setText(Html.fromHtml(msg));

        // image will be in right of text view
        if (imgPosition.equalsIgnoreCase("right")) {
            imageLeft.setVisibility(View.GONE);
            imageRight.setVisibility(View.VISIBLE);
            imageRight.setImageResource(imageId);

        } else if (imgPosition.equalsIgnoreCase("left")) {

            imageLeft.setVisibility(View.VISIBLE);
            imageRight.setVisibility(View.GONE);
            imageLeft.setImageResource(imageId);
        }

        layout.setPadding(0, 0, 0, 0);
        // Add the view to the Snackbar's layout
        // llMain.getLayoutParams().height=220;
        layout.addView(snackView, 0);
        // Show the Snackbar
        snackbar.setDuration(8000).show();
    }
}
