package in.securelearning.lil.android.syncadapter.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class FlyObjectAnimationUtil {

    private Animator.AnimatorListener mAnimationListener;

    public FlyObjectAnimationUtil() {
    }

    public FlyObjectAnimationUtil setAnimationListener(Animator.AnimatorListener listener) {
        mAnimationListener = listener;
        return this;
    }

    public void startAnimation(final View targetView, final View destinationView) {

        flyTargetView(targetView, destinationView).start();

    }

    private AnimatorSet flyTargetView(final View targetView, final View destinationView) {
        final float originX = targetView.getWidth();
        final float originY = targetView.getHeight();
        final float destX = destinationView.getWidth();
        final float destY = destinationView.getHeight();
        long mCircleDuration = 1000;
        final long mMoveDuration = 1000;
        final long mDisappearDuration = 500;


        final float endRadius = Math.max(destX, destY) / 2;
        final float startRadius = Math.max(originX, originY);

        @SuppressLint("ObjectAnimatorBinding") Animator mRevealAnimator = ObjectAnimator.ofFloat(targetView, "drawableRadius", startRadius, endRadius * 1.05f, endRadius * 0.9f, endRadius);
        mRevealAnimator.setInterpolator(new AccelerateInterpolator());

        final float scaleFactor = 0.6f;
        Animator scaleAnimatorY = ObjectAnimator.ofFloat(targetView, View.SCALE_Y, 1, 1, scaleFactor, scaleFactor);
        Animator scaleAnimatorX = ObjectAnimator.ofFloat(targetView, View.SCALE_X, 1, 1, scaleFactor, scaleFactor);

        AnimatorSet animatorCircleSet = new AnimatorSet();
        animatorCircleSet.setDuration(mCircleDuration);
        animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY, mRevealAnimator);

        animatorCircleSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimationListener != null)
                    mAnimationListener.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int[] src = new int[2];
                int[] dest = new int[2];
                targetView.getLocationOnScreen(src);
                destinationView.getLocationOnScreen(dest);

                float y = targetView.getY();
                float x = targetView.getX();
                Animator translatorX = ObjectAnimator.ofFloat(targetView, View.X, x, x + dest[0] - (src[0] + (originX * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destX - scaleFactor * endRadius));
                translatorX.setInterpolator(new TimeInterpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return (float) (-Math.pow(input - 1, 2) + 1f);
                    }
                });

                Animator translatorY = ObjectAnimator.ofFloat(targetView, View.Y, y, y + dest[1] - (src[1] + (originY * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destY - scaleFactor * endRadius));
                translatorY.setInterpolator(new LinearInterpolator());

                AnimatorSet animatorMoveSet = new AnimatorSet();
                animatorMoveSet.playTogether(translatorX, translatorY);
                animatorMoveSet.setDuration(mMoveDuration);

                AnimatorSet animatorDisappearSet = new AnimatorSet();
                Animator disappearAnimatorY = ObjectAnimator.ofFloat(targetView, View.SCALE_Y, scaleFactor, 0);
                Animator disappearAnimatorX = ObjectAnimator.ofFloat(targetView, View.SCALE_X, scaleFactor, 0);
                animatorDisappearSet.setDuration(mDisappearDuration);
                animatorDisappearSet.playTogether(disappearAnimatorX, disappearAnimatorY);


                AnimatorSet total = new AnimatorSet();
                total.playSequentially(animatorMoveSet, animatorDisappearSet);
                total.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mAnimationListener != null)
                            mAnimationListener.onAnimationEnd(animation);

                        targetView.animate().translationX(0).translationY(0);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                total.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animatorCircleSet;
    }


}
