package in.securelearning.lil.android.player.listener;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public final class MTFChoiceTouchListener implements View.OnTouchListener {
    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            ClipData data = ClipData.newPlainText(ConstantUtil.BLANK, ConstantUtil.BLANK);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, ConstantUtil.INT_ZERO);
            return true;
        } else {
            return false;
        }
    }
}
