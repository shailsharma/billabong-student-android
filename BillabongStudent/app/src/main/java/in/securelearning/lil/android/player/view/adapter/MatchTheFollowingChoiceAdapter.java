//package in.securelearning.lil.android.player.view.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.ClipData;
//import android.content.Context;
//import android.databinding.DataBindingUtil;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.util.SparseBooleanArray;
//import android.view.DragEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.ArrayList;
//
//import in.securelearning.lil.android.app.R;
//import in.securelearning.lil.android.app.databinding.LayoutMatchTheFollowingItemBinding;
//import in.securelearning.lil.android.player.dataobject.MatchingContent;
//import in.securelearning.lil.android.player.view.activity.PracticePlayerActivity;
//
//public class MatchTheFollowingChoiceAdapter extends RecyclerView.Adapter<MatchTheFollowingChoiceAdapter.ViewHolder> {
//    private ArrayList<MatchingContent> mQuestionChoicesA;
//    private ArrayList<MatchingContent> mQuestionChoicesB;
//    private boolean mIsAttempt;
//    private Context mContext;
//    private SparseBooleanArray mItemStateArrayA = new SparseBooleanArray();
//    private SparseBooleanArray mItemStateArrayB = new SparseBooleanArray();
//
//    public MatchTheFollowingChoiceAdapter(Context context, ArrayList<MatchingContent> questionChoicesA, ArrayList<MatchingContent> questionChoicesB, boolean isAttempt) {
//        mQuestionChoicesA = questionChoicesA;
//        mQuestionChoicesB = questionChoicesB;
//        mIsAttempt = isAttempt;
//        mContext = context;
//    }
//
//    @Override
//    public MatchTheFollowingChoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutMatchTheFollowingItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_match_the_following_item, parent, false);
//        return new MatchTheFollowingChoiceAdapter.ViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(MatchTheFollowingChoiceAdapter.ViewHolder holder, int position) {
//        setDefaults(holder.mBinding);
//        int index = position / 2;
//
//        if (position % 2 == 0) {
//            /*Left side items*/
//            MatchingContent choice = mQuestionChoicesA.get(index);
//            setChoiceView(choice, index, holder.mBinding);
//            if (mItemStateArrayA.get(index)) {
//                //holder.mBinding.layoutChoice.setVisibility(View.INVISIBLE);
//                holder.mBinding.layoutChoice.setEnabled(false);
//                holder.mBinding.textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite66));
//                holder.mBinding.layoutChoice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_unselected_light));
//            }
//        } else {
//            /*Right side items*/
//            MatchingContent choiceB = mQuestionChoicesB.get(index);
//            setChoiceBView(choiceB, index, holder.mBinding);
//            if (mItemStateArrayB.get(index)) {
//                holder.mBinding.layoutChoiceB.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_selected));
//            }
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mQuestionChoicesA.size() + mQuestionChoicesB.size();
//
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setChoiceView(MatchingContent choice, int index, LayoutMatchTheFollowingItemBinding binding) {
//        int choiceId = choice.getId();
//        String text = choice.getEntity();
//        binding.layoutChoice.setTag(choiceId);
//        binding.layoutChoice.setVisibility(View.VISIBLE);
//
//        if (!TextUtils.isEmpty(text)) {
//            binding.textView.setVisibility(View.VISIBLE);
//            binding.textView.setText(text);
//            binding.layoutChoice.setId(index);
//        }
//
//        binding.layoutChoice.setOnTouchListener(new ChoiceTouchListener());
//
//    }
//
//    private void setChoiceBView(MatchingContent choiceB, int index, LayoutMatchTheFollowingItemBinding binding) {
//        int choiceIdB = choiceB.getId();
//        String textB = choiceB.getPlaceHolder();
//        binding.layoutChoiceB.setTag(choiceIdB);
//        binding.layoutChoiceB.setVisibility(View.VISIBLE);
//
//        if (!TextUtils.isEmpty(textB)) {
//            binding.textViewB.setVisibility(View.VISIBLE);
//            binding.textViewB.setText(textB);
//            binding.layoutChoiceB.setId(index);
//        }
//
//        PracticePlayerActivity.ChoiceBDragListener choiceBDragListener = new PracticePlayerActivity().ChoiceBDragListener;
//        binding.layoutChoiceB.setOnDragListener(choiceBDragListener);
//
//    }
//
//    private void setReviewMode(MatchingContent choice, LayoutMatchTheFollowingItemBinding binding) {
//        binding.layoutChoice.setBackground(null);
//        binding.layoutChoice.setVisibility(View.VISIBLE);
//        binding.layoutChoiceB.setVisibility(View.VISIBLE);
//        ViewGroup.LayoutParams layoutParams = binding.layoutChoiceB.getLayoutParams();
//        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        binding.layoutChoiceB.setLayoutParams(layoutParams);
////        AnimationUtils.fadeIn(mContext, binding.layoutMatchTheFollowingItem);
//        binding.layoutMatchTheFollowingItem.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_selected));
//        String text = choice.getEntity();
//
//        if (!TextUtils.isEmpty(text)) {
//            binding.textView.setVisibility(View.VISIBLE);
//            binding.textView.setText(text);
//        }
//
////        if (choice.getChoiceResource() != null && !TextUtils.isEmpty(choice.getChoiceResource().getUrlMain())) {
////            binding.layoutImageView.setVisibility(View.VISIBLE);
////            Picasso.with(mContext).load(choice.getChoiceResource().getUrlMain()).resize(240, 180).into(binding.imageView);
////        }
//
//        String textB = choice.getPlaceHolder();
//
//        if (!TextUtils.isEmpty(textB)) {
//            binding.textViewB.setVisibility(View.VISIBLE);
//            binding.textViewB.setText(textB);
//        }
//
////        if (choice.getChoiceResourceB() != null && !TextUtils.isEmpty(choice.getChoiceResourceB().getUrlMain())) {
////            binding.layoutImageViewB.setVisibility(View.VISIBLE);
////            Picasso.with(mContext).load(choice.getChoiceResourceB().getUrlMain()).resize(240, 180).into(binding.imageViewB);
////        }
//
//    }
//
//    private void setDefaults(LayoutMatchTheFollowingItemBinding binding) {
//        binding.textView.setVisibility(View.GONE);
//        binding.textViewB.setVisibility(View.GONE);
////        binding.layoutImageView.setVisibility(View.GONE);
////        binding.layoutImageViewB.setVisibility(View.GONE);
//        binding.layoutChoice.setVisibility(View.GONE);
//        binding.layoutChoiceB.setVisibility(View.GONE);
//        binding.layoutChoice.setOnTouchListener(null);
//        binding.layoutChoice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_unselected));
//        binding.layoutChoiceB.setOnDragListener(null);
//        binding.layoutChoiceB.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_drop_area));
//    }
//
//    public void updateAdapter(int choiceIndexA, int choiceIndexB) {
//        mItemStateArrayA.put(choiceIndexA, true);
//        mItemStateArrayB.put(choiceIndexB, true);
//        notifyDataSetChanged();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        LayoutMatchTheFollowingItemBinding mBinding;
//
//        public ViewHolder(LayoutMatchTheFollowingItemBinding binding) {
//            super(binding.getRoot());
//            mBinding = binding;
//        }
//    }
//
//    public final class ChoiceTouchListener implements View.OnTouchListener {
//        @SuppressLint("NewApi")
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//
//                ClipData data = ClipData.newPlainText("", "");
//                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//                view.startDrag(data, shadowBuilder, view, 0);
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }
//
//}