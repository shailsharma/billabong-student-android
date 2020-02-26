package in.securelearning.lil.android.player.listener;

import android.view.View;

import in.securelearning.lil.android.base.dataobjects.Attempt;

public interface MTFDropListenerNotifier {
    void OnDropped(Attempt attempt, boolean isCorrect, View dragView, View targetView);

}