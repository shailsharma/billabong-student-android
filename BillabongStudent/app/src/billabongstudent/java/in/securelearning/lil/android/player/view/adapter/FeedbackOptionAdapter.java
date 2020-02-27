package in.securelearning.lil.android.player.view.adapter;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutQuestionFeedbackOptionItemBinding;
import in.securelearning.lil.android.player.listener.FeedbackOptionSelectListener;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;

public class FeedbackOptionAdapter extends RecyclerView.Adapter<FeedbackOptionAdapter.ViewHolder> {

    private ArrayList<IdNameObject> mList;
    private int mSelectedPosition = -1;
    private FeedbackOptionSelectListener mFeedbackOptionSelectListener;

    public FeedbackOptionAdapter(FeedbackOptionSelectListener listener, ArrayList<IdNameObject> questionFeedbackOptions) {
        this.mFeedbackOptionSelectListener = listener;
        this.mList = questionFeedbackOptions;
    }

    @NonNull
    @Override
    public FeedbackOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutQuestionFeedbackOptionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_question_feedback_option_item, parent, false);
        return new FeedbackOptionAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedbackOptionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final IdNameObject idNameObject = mList.get(position);
        holder.mBinding.textView.setText(idNameObject.getName());

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mBinding.imageView.setImageResource(R.drawable.checkbox_circle_s);
                mFeedbackOptionSelectListener.OnFeedbackOptionSelected(idNameObject.getId());
                notifyItemChanged(mSelectedPosition);
                mSelectedPosition = position;
            }
        });

        if (mSelectedPosition == position) {
            holder.mBinding.imageView.setImageResource(R.drawable.checkbox_circle_s);
        } else {
            holder.mBinding.imageView.setImageResource(R.drawable.checkbox_circle_u);
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutQuestionFeedbackOptionItemBinding mBinding;

        public ViewHolder(LayoutQuestionFeedbackOptionItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}
