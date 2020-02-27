package cardstackview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

import in.securelearning.lil.android.app.R;

public class CardStackView extends FrameLayout {

    public interface CardEventListener {
        void onCardDragging(float percentX, float percentY);

        void onCardSwiped(SwipeDirection direction);

        void onCardReversed();

        void onCardMovedToOrigin();

        void onCardClicked(int index);
    }

    private CardStackOption option = new CardStackOption();
    private CardStackState state = new CardStackState();

    private CardStackAdapter adapter = null;
    private LinkedList<CardContainerView> containers = new LinkedList<>();
    private CardEventListener cardEventListener = null;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            boolean shouldReset = false;
            if (state.isPaginationReserved) {
                state.isPaginationReserved = false;
            } else {
                boolean isSameCount = state.lastCount == adapter.getCount();
                shouldReset = !isSameCount;
            }
            initialize(shouldReset);
            state.lastCount = adapter.getCount();
        }
    };
    private CardContainerView.ContainerEventListener containerEventListener = new CardContainerView.ContainerEventListener() {
        @Override
        public void onContainerDragging(float percentX, float percentY) {
            update(percentX, percentY);
        }

        @Override
        public void onContainerForwardSwiped(Point point, SwipeDirection direction) {
            swipe(point, direction);
        }

        @Override
        public void onContainerReverseSwiped(Point point, SwipeDirection direction) {
            reverse(point, direction);
        }

        @Override
        public void onContainerMovedToOrigin() {
            initializeCardStackPosition();
            if (cardEventListener != null) {
                cardEventListener.onCardMovedToOrigin();
            }
        }

        @Override
        public void onContainerClicked() {
            if (cardEventListener != null) {
                cardEventListener.onCardClicked(state.topIndex);
            }
        }
    };

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CardStackView);
        setVisibleCount(array.getInt(R.styleable.CardStackView_visibleCount, option.visibleCount));
        setSwipeThreshold(array.getFloat(R.styleable.CardStackView_swipeThreshold, option.swipeThreshold));
        setTranslationDiff(array.getFloat(R.styleable.CardStackView_translationDiff, option.translationDiff));
        setScaleDiff(array.getFloat(R.styleable.CardStackView_scaleDiff, option.scaleDiff));
        setStackFrom(StackFrom.values()[array.getInt(R.styleable.CardStackView_stackFrom, option.stackFrom.ordinal())]);
        setElevationEnabled(array.getBoolean(R.styleable.CardStackView_elevationEnabled, option.isElevationEnabled));
        setSwipeEnabled(array.getBoolean(R.styleable.CardStackView_swipeEnabled, option.isSwipeEnabled));
        setSwipeForwardDirection(SwipeDirection.from(array.getInt(R.styleable.CardStackView_swipeForwardDirection, 2)));
        setSwipeReverseDirection(SwipeDirection.from(array.getInt(R.styleable.CardStackView_swipeReverseDirection, 3)));
        setLeftOverlay(array.getResourceId(R.styleable.CardStackView_leftOverlay, 0));
        setRightOverlay(array.getResourceId(R.styleable.CardStackView_rightOverlay, 0));
        setBottomOverlay(array.getResourceId(R.styleable.CardStackView_bottomOverlay, 0));
        setTopOverlay(array.getResourceId(R.styleable.CardStackView_topOverlay, 0));
        array.recycle();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (state.isInitialized && visibility == View.VISIBLE) {
            initializeCardStackPosition();
        }
    }

    private void initialize(boolean shouldReset) {
        resetIfNeeded(shouldReset);
        initializeViews();
        initializeCardStackPosition();
        initializeViewContents();
    }

    private void resetIfNeeded(boolean shouldReset) {
        if (shouldReset) {
            state.reset();
        }
    }

    private void initializeViews() {
        removeAllViews();
        containers.clear();

        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView view = (CardContainerView) LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_card_stack_container, this, false);
            view.setDraggable(false);
            view.setReverseAllowed(false);
            view.setCardStackOption(option);
            view.setOverlay(option.leftOverlay, option.rightOverlay, option.bottomOverlay, option.topOverlay);
            containers.add(0, view);
            addView(view);
        }

        containers.getFirst().setContainerEventListener(containerEventListener);

        state.isInitialized = true;
    }

    private void initializeCardStackPosition() {
        clear();
        update(0f, 0f);
    }

    private void initializeViewContents() {
        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView container = containers.get(i);
            int adapterIndex = state.topIndex + i;

            if (adapterIndex < adapter.getCount()) {
                ViewGroup parent = container.getContentContainer();
                View child = adapter.getView(adapterIndex, parent.getChildAt(0), parent);
                if (parent.getChildCount() == 0) {
                    parent.addView(child);
                }
                container.setNoDragMessage(adapter.getNoDragMessage(adapterIndex));
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }
        }
        if (!adapter.isEmpty()) {
            getTopView().setDraggable(adapter.isDraggable(0));
            getTopView().setReverseAllowed(adapter.isReverseAllowed(0));
        }
    }

    private void loadNextView() {
        int lastIndex = state.topIndex + option.visibleCount - 1;
        boolean hasNextCard = lastIndex < adapter.getCount();
        if (hasNextCard) {
            CardContainerView container = getBottomView();
            container.setDraggable(false);
            ViewGroup parent = container.getContentContainer();
            View child = adapter.getView(lastIndex, parent.getChildAt(0), parent);
            if (parent.getChildCount() == 0) {
                parent.addView(child);
            }
        } else {
            CardContainerView container = getBottomView();
            container.setDraggable(false);
            container.setVisibility(View.GONE);
        }

        boolean hasCard = state.topIndex < adapter.getCount();
        if (hasCard && adapter.isDraggable(state.topIndex)) {
            getTopView().setDraggable(true);
        } else {
            getTopView().setDraggable(false);
        }
        if (hasCard && adapter.isReverseAllowed(state.topIndex)) {
            getTopView().setReverseAllowed(true);
        } else {
            getTopView().setReverseAllowed(false);
        }
    }

    private void clear() {
        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView view = containers.get(i);
            view.reset();
            ViewCompat.setTranslationX(view, 0f);
            ViewCompat.setTranslationY(view, 0f);
            ViewCompat.setScaleX(view, 1f);
            ViewCompat.setScaleY(view, 1f);
            ViewCompat.setRotation(view, 0f);
        }
    }

    private void update(float percentX, float percentY) {
        if (cardEventListener != null) {
            cardEventListener.onCardDragging(percentX, percentY);
        }

        if (!option.isElevationEnabled) {
            return;
        }

        for (int i = 1; i < option.visibleCount; i++) {
            CardContainerView view = containers.get(i);

            float currentScale = 1f - (i * option.scaleDiff);
            float nextScale = 1f - ((i - 1) * option.scaleDiff);
            float percent = currentScale + (nextScale - currentScale) * Math.abs(percentX);
            ViewCompat.setScaleX(view, percent);
            ViewCompat.setScaleY(view, percent);

            float currentTranslationY = i * CardStackUtil.toPx(getContext(), option.translationDiff);
            if (option.stackFrom == StackFrom.Top) {
                currentTranslationY *= -1;
            }

            float nextTranslationY = (i - 1) * CardStackUtil.toPx(getContext(), option.translationDiff);
            if (option.stackFrom == StackFrom.Top) {
                nextTranslationY *= -1;
            }

            float translationY = currentTranslationY - Math.abs(percentX) * (currentTranslationY - nextTranslationY);
            ViewCompat.setTranslationY(view, translationY);
        }
    }

    public void performReverse(Point point, View prevView, final Animator.AnimatorListener listener) {
        reorderForReverse(prevView);
        CardContainerView topView = getTopView();
        ViewCompat.setTranslationX(topView, point.x);
        ViewCompat.setTranslationY(topView, -point.y);
        topView.animate()
                .translationX(topView.getViewOriginX())
                .translationY(topView.getViewOriginY())
                .setListener(listener)
                .setDuration(400L)
                .start();
    }

    public void performSwipe(Point point, final Animator.AnimatorListener listener) {
        getTopView().animate()
                .translationX(point.x)
                .translationY(-point.y)
                .setDuration(400L)
                .setListener(listener)
                .start();
    }

    public void performSwipe(SwipeDirection direction, AnimatorSet set, final Animator.AnimatorListener listener) {
        if (direction == SwipeDirection.Left) {
            getTopView().showLeftOverlay();
            getTopView().setOverlayAlpha(1f);
        } else if (direction == SwipeDirection.Right) {
            getTopView().showRightOverlay();
            getTopView().setOverlayAlpha(1f);
        } else if (direction == SwipeDirection.Bottom) {
            getTopView().showBottomOverlay();
            getTopView().setOverlayAlpha(1f);
        } else if (direction == SwipeDirection.Top) {
            getTopView().showTopOverlay();
            getTopView().setOverlayAlpha(1f);
        }
        set.addListener(listener);
        set.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                CardContainerView view = getTopView();
                update(view.getPercentX(), view.getPercentY());
                return input;
            }
        });
        set.start();
    }

    private void moveToBottom(CardContainerView container) {
        CardStackView parent = (CardStackView) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container, 0);
        }
    }

    private void moveToTop(CardContainerView container, View child) {
        CardStackView parent = (CardStackView) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container);

            container.getContentContainer().removeAllViews();
            container.getContentContainer().addView(child);
            container.setVisibility(View.VISIBLE);
        }
    }

    private void reorderForSwipe() {
        moveToBottom(getTopView());
        containers.addLast(containers.removeFirst());
    }

    private void reorderForReverse(View prevView) {
        CardContainerView bottomView = getBottomView();
        moveToTop(bottomView, prevView);
        containers.addFirst(containers.removeLast());
    }

    private void executePreSwipeTask() {
        containers.getFirst().setContainerEventListener(null);
        containers.getFirst().setDraggable(false);
        if (containers.size() > 1) {
            containers.get(1).setContainerEventListener(containerEventListener);
            if (state.topIndex < state.lastCount) {
                containers.get(1).setDraggable(adapter.isDraggable(state.topIndex + 1));
            } else {
                containers.get(1).setDraggable(true);
            }
        }
    }

    private void executePostSwipeTask(Point point, final SwipeDirection direction) {
        reorderForSwipe();

        state.lastPoint = point;

        initializeCardStackPosition();

        state.topIndex++;

        if (cardEventListener != null) {
            cardEventListener.onCardSwiped(direction);

        }

        loadNextView();

        containers.getLast().setContainerEventListener(null);
        containers.getFirst().setContainerEventListener(containerEventListener);
    }

    private void executePostReverseTask() {
        state.lastPoint = null;

        initializeCardStackPosition();

        state.topIndex--;

        if (cardEventListener != null) {
            cardEventListener.onCardReversed();
        }

        containers.getLast().setContainerEventListener(null);
        containers.getFirst().setContainerEventListener(containerEventListener);

        getTopView().setDraggable(adapter.isDraggable(state.topIndex));
        getTopView().setReverseAllowed(adapter.isReverseAllowed(state.topIndex));
    }

    private void executePostReverseTask(final Point point, final SwipeDirection direction) {
        initializeCardStackPosition();

        state.topIndex--;

        if (cardEventListener != null) {
            cardEventListener.onCardReversed();
        }

        containers.getLast().setContainerEventListener(null);
        containers.getFirst().setContainerEventListener(containerEventListener);

        getTopView().setDraggable(adapter.isDraggable(state.topIndex));
        getTopView().setReverseAllowed(adapter.isReverseAllowed(state.topIndex));
    }

    public void setCardEventListener(CardEventListener listener) {
        this.cardEventListener = listener;
    }

    public void setAdapter(CardStackAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        this.state.lastCount = adapter.getCount();
        initialize(true);
    }

    public void setVisibleCount(int visibleCount) {
        option.visibleCount = visibleCount;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeThreshold(float swipeThreshold) {
        option.swipeThreshold = swipeThreshold;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setTranslationDiff(float translationDiff) {
        option.translationDiff = translationDiff;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setScaleDiff(float scaleDiff) {
        option.scaleDiff = scaleDiff;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setStackFrom(StackFrom stackFrom) {
        option.stackFrom = stackFrom;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setElevationEnabled(boolean isElevationEnabled) {
        option.isElevationEnabled = isElevationEnabled;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        option.isSwipeEnabled = isSwipeEnabled;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeForwardDirection(List<SwipeDirection> swipeForwardDirection) {
        option.swipeForwardDirection = swipeForwardDirection;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeReverseDirection(List<SwipeDirection> swipeReverseDirection) {
        option.swipeReverseDirection = swipeReverseDirection;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setLeftOverlay(int leftOverlay) {
        option.leftOverlay = leftOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setRightOverlay(int rightOverlay) {
        option.rightOverlay = rightOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setBottomOverlay(int bottomOverlay) {
        option.bottomOverlay = bottomOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setTopOverlay(int topOverlay) {
        option.topOverlay = topOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setPaginationReserved() {
        state.isPaginationReserved = true;
    }

    public void swipe(final Point point, final SwipeDirection direction) {
        executePreSwipeTask();
        performSwipe(point, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                executePostSwipeTask(point, direction);
            }
        });
    }

    public void swipe(final SwipeDirection direction, AnimatorSet set) {
        executePreSwipeTask();
        performSwipe(direction, set, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                executePostSwipeTask(new Point(0, -2000), direction);
            }
        });
    }

    public void reverse(final Point point, final SwipeDirection direction) {
        if (state.topIndex > 0) {
            performSwipe(point, null);
            ViewGroup parent = containers.getLast();
            View prevView = adapter.getView(state.topIndex - 1, null, parent);
            performReverse(new Point(2000, 0), prevView, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    executePostReverseTask(new Point(2000, 0), direction);
                }
            });
        }
    }

    public void reverse() {
        if (state.lastPoint != null) {
            ViewGroup parent = containers.getLast();
            View prevView = adapter.getView(state.topIndex - 1, null, parent);
            performReverse(state.lastPoint, prevView, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    executePostReverseTask();
                }
            });
        }
    }

    public CardContainerView getTopView() {
        return containers.getFirst();
    }

    public CardContainerView getBottomView() {
        return containers.getLast();
    }

    public int getTopIndex() {
        return state.topIndex;
    }

}
