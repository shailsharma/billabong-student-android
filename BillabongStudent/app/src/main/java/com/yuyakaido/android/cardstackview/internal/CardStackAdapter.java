package com.yuyakaido.android.cardstackview.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Prabodh Dhabaria on 26-03-2018.
 */

public abstract class CardStackAdapter<T> extends ArrayAdapter<T> {

    public CardStackAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public CardStackAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CardStackAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    public CardStackAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CardStackAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    public CardStackAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public abstract boolean isDraggable(int position);

    public abstract boolean isReverseAllowed(int position);

    public abstract String getNoDragMessage(int position);

}
