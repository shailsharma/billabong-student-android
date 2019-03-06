package in.securelearning.lil.android.home.views.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Cp on 11/28/2016.
 */

public class RelativeLayoutWidth16to9 extends RelativeLayout {

    public RelativeLayoutWidth16to9(Context context) {
        super(context);
    }

    public RelativeLayoutWidth16to9(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutWidth16to9(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RelativeLayoutWidth16to9(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = (widthMeasureSpec * 9) / 16;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
