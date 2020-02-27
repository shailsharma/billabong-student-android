package in.securelearning.lil.android.player.listener;

import android.view.DragEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.utils.AnimationUtils;

public class MTFDropListener implements View.OnDragListener {

    private MTFDropListenerNotifier mMTFDropListenerNotifier;

    public MTFDropListener(MTFDropListenerNotifier mtfDropListenerNotifier) {
        this.mMTFDropListenerNotifier = mtfDropListenerNotifier;
    }

    @Override
    public boolean onDrag(View dropTargetView, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                break;

            case DragEvent.ACTION_DROP:
                View droppedView = (View) event.getLocalState();
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(new ArrayList<>(Collections.singleton(String.valueOf(dropTargetView.getTag()))));

                if (dropTargetView.getTag() == droppedView.getTag()) {

                    mMTFDropListenerNotifier.OnDropped(attempt, true, droppedView, dropTargetView);

                } else if (dropTargetView.getTag(R.id.mtfPlaceholderLocked) != null
                        && (Boolean) dropTargetView.getTag(R.id.mtfPlaceholderLocked)) {

                    AnimationUtils.shake(droppedView.getContext(), droppedView);
                    AnimationUtils.shake(dropTargetView.getContext(), dropTargetView);

                } else {

                    mMTFDropListenerNotifier.OnDropped(attempt, false, droppedView, dropTargetView);
                    AnimationUtils.shake(droppedView.getContext(), droppedView);
                    AnimationUtils.shake(dropTargetView.getContext(), dropTargetView);
                }

                break;

            default:

                break;
        }
        return true;
    }
}
