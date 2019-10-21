package in.securelearning.lil.android.app.Widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Prabodh Dhabaria on 02-05-2018.
 */
public class CheckBoxCustom extends AppCompatCheckBox implements Target {
    public CheckBoxCustom(Context context) {
        super(context);
    }

    public CheckBoxCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
