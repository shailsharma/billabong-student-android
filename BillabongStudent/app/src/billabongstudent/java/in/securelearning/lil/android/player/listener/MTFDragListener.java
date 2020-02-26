package in.securelearning.lil.android.player.listener;

import android.view.DragEvent;
import android.view.View;

public class MTFDragListener implements View.OnDragListener {

    private int mScrollTop, mScrollBottom;
    private MTFDragListenerNotifier mMTFDragListenerNotifier;

    public MTFDragListener(int scrollTop, int scrollBottom, MTFDragListenerNotifier mtfDragListenerNotifier) {
        this.mScrollTop = scrollTop;
        this.mScrollBottom = scrollBottom;
        this.mMTFDragListenerNotifier = mtfDragListenerNotifier;
    }

    @Override
    public boolean onDrag(View dropView, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            int y = Math.round(event.getY());
            int threshold = 600;
            if (Math.abs(y - mScrollTop) < threshold) {
                mMTFDragListenerNotifier.OnDraggingStart(0, -30);
            } else if (Math.abs((y + mScrollTop) - mScrollBottom) < threshold) {
                mMTFDragListenerNotifier.OnDraggingStart(0, 30);
            }
        }
        return true;
    }
}
