package in.securelearning.lil.android.player.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutMatchTheFollowingItemBinding;
import in.securelearning.lil.android.player.dataobject.MatchingContent;
import in.securelearning.lil.android.player.listener.MTFChoiceTouchListener;
import in.securelearning.lil.android.player.listener.MTFDropListener;
import in.securelearning.lil.android.player.listener.MTFDropListenerNotifier;

public class MatchTheFollowingAdapter extends RecyclerView.Adapter<MatchTheFollowingAdapter.ViewHolder> {

    private ArrayList<MatchingContent> mChoicesEntity;
    private ArrayList<MatchingContent> mChoicesPlaceholder;
    private boolean mIsAttempt;
    private Context mContext;
    private SparseBooleanArray mItemStateArrayEntity = new SparseBooleanArray();
    private SparseBooleanArray mItemStateArrayPlaceholder = new SparseBooleanArray();
    private MTFDropListenerNotifier mMTFDropListenerNotifier;

    public MatchTheFollowingAdapter(Context context, ArrayList<MatchingContent> choicesEntity, ArrayList<MatchingContent> choicesPlaceholder, boolean isAttempt, MTFDropListenerNotifier mtfDropListenerNotifier) {
        this.mChoicesEntity = choicesEntity;
        this.mChoicesPlaceholder = choicesPlaceholder;
        this.mIsAttempt = isAttempt;
        this.mContext = context;
        this.mMTFDropListenerNotifier = mtfDropListenerNotifier;
    }

    @NonNull
    @Override
    public MatchTheFollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMatchTheFollowingItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_match_the_following_item, parent, false);
        return new MatchTheFollowingAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchTheFollowingAdapter.ViewHolder holder, int position) {
        setDefaults(holder.mBinding);
        int index = position / 2;

        if (position % 2 == 0) {
            /*Left side items*/
            MatchingContent choice = mChoicesEntity.get(index);
            setChoiceEntityView(choice, index, mIsAttempt, holder.mBinding);


        } else {
            /*Right side items*/
            MatchingContent choiceB = mChoicesPlaceholder.get(index);
            setChoicePlaceholderView(choiceB, index, mIsAttempt, holder.mBinding);

        }


    }

    @Override
    public int getItemCount() {
        return mChoicesEntity.size() + mChoicesPlaceholder.size();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setChoiceEntityView(MatchingContent choiceEntity, int index, boolean isAttempt, LayoutMatchTheFollowingItemBinding binding) {
        int choiceId = choiceEntity.getId();
        String text = choiceEntity.getEntity();
        binding.layoutChoiceEntity.setTag(choiceId);
        binding.layoutChoiceEntity.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(text)) {
            binding.textViewEntity.setVisibility(View.VISIBLE);
            binding.textViewEntity.setText(text);
            binding.layoutChoiceEntity.setId(index);
        }

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(choiceEntity.getColor());
        gradientDrawable.setCornerRadius(12);

        binding.layoutChoiceEntity.setBackground(gradientDrawable);
        binding.layoutChoiceEntity.setOnTouchListener(new MTFChoiceTouchListener());

        if (mItemStateArrayEntity.get(index) || !isAttempt) {
            binding.layoutChoiceEntity.setEnabled(false);
            binding.textViewEntity.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite66));
            GradientDrawable gradientDrawableDone = new GradientDrawable();
            gradientDrawableDone.setColor(choiceEntity.getColor());
            gradientDrawableDone.setCornerRadius(12);
            binding.layoutChoiceEntity.setBackground(gradientDrawableDone);
        }

    }

    private void setChoicePlaceholderView(MatchingContent choicePlaceholder, int index, boolean isAttempt, LayoutMatchTheFollowingItemBinding binding) {
        int choiceIdPlaceholder = choicePlaceholder.getId();
        String textPlaceholder = choicePlaceholder.getPlaceHolder();
        binding.layoutChoicePlaceholder.setTag(choiceIdPlaceholder);
        binding.layoutChoicePlaceholder.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(textPlaceholder)) {
            binding.textViewPlaceholder.setVisibility(View.VISIBLE);
            binding.textViewPlaceholder.setText(textPlaceholder);
            binding.layoutChoicePlaceholder.setId(index);
        }


        if (mItemStateArrayPlaceholder.get(index) || !isAttempt) {

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(choicePlaceholder.getColor());
            gradientDrawable.setCornerRadius(12);
            gradientDrawable.setStroke(4, Color.WHITE);

            binding.layoutChoicePlaceholder.setBackground(gradientDrawable);
            binding.layoutChoicePlaceholder.setTag(R.id.mtfPlaceholderLocked, true);
        }


        binding.layoutChoicePlaceholder.setOnDragListener(new MTFDropListener(mMTFDropListenerNotifier));

    }

    private void setReviewMode(MatchingContent choice, LayoutMatchTheFollowingItemBinding binding) {
        binding.layoutChoiceEntity.setBackground(null);
        binding.layoutChoiceEntity.setVisibility(View.VISIBLE);
        binding.layoutChoicePlaceholder.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = binding.layoutChoicePlaceholder.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.layoutChoicePlaceholder.setLayoutParams(layoutParams);
//        AnimationUtils.fadeIn(mContext, binding.layoutMatchTheFollowingItem);
        binding.layoutMatchTheFollowingItem.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_selected));
        String textEntity = choice.getEntity();

        if (!TextUtils.isEmpty(textEntity)) {
            binding.textViewEntity.setVisibility(View.VISIBLE);
            binding.textViewEntity.setText(textEntity);
        }

//        if (choice.getChoiceResource() != null && !TextUtils.isEmpty(choice.getChoiceResource().getUrlMain())) {
//            binding.layoutImageView.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(choice.getChoiceResource().getUrlMain()).resize(240, 180).into(binding.imageView);
//        }

        String textPlaceHolder = choice.getPlaceHolder();

        if (!TextUtils.isEmpty(textPlaceHolder)) {
            binding.textViewPlaceholder.setVisibility(View.VISIBLE);
            binding.textViewPlaceholder.setText(textPlaceHolder);
        }

//        if (choice.getChoiceResourceB() != null && !TextUtils.isEmpty(choice.getChoiceResourceB().getUrlMain())) {
//            binding.layoutImageViewB.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(choice.getChoiceResourceB().getUrlMain()).resize(240, 180).into(binding.imageViewB);
//        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setDefaults(LayoutMatchTheFollowingItemBinding binding) {
        binding.textViewEntity.setVisibility(View.GONE);
        binding.textViewPlaceholder.setVisibility(View.GONE);
        binding.layoutChoiceEntity.setVisibility(View.GONE);
        binding.layoutChoicePlaceholder.setVisibility(View.GONE);
        binding.layoutChoiceEntity.setOnTouchListener(null);
        binding.layoutChoiceEntity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_unselected));
        binding.layoutChoicePlaceholder.setOnDragListener(null);
        binding.layoutChoicePlaceholder.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_drop_area));
    }

    public void updateAdapter(int choiceIndexEntity, int choiceIndexPlaceholder) {
        mItemStateArrayEntity.put(choiceIndexEntity, true);
        mItemStateArrayPlaceholder.put(choiceIndexPlaceholder, true);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutMatchTheFollowingItemBinding mBinding;

        public ViewHolder(LayoutMatchTheFollowingItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }


}