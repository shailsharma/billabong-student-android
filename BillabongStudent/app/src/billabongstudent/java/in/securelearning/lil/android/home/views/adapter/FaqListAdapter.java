package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutFaqListItemBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.FAQuestionAnswer;

public class FaqListAdapter extends RecyclerView.Adapter<FaqListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<FAQuestionAnswer> mList;
    private int mExpandedPosition = -1;

    FaqListAdapter(Context context, ArrayList<FAQuestionAnswer> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public FaqListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutFaqListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_faq_list_item, parent, false);
        return new FaqListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final FaqListAdapter.ViewHolder holder, final int position) {
        final FAQuestionAnswer helpAndFaqModule = mList.get(position);

        setQuestion(helpAndFaqModule.getQuestion(), holder.mBinding.textViewQuestion);
        setAnswer(helpAndFaqModule.getAnswer(), holder.mBinding.textViewAnswer);


        if (mExpandedPosition == position) {
            holder.mBinding.textViewAnswer.setVisibility(View.VISIBLE);
            holder.mBinding.textViewQuestion.setTypeface(holder.mBinding.textViewQuestion.getTypeface(), Typeface.BOLD);
        } else {
            holder.mBinding.imageViewHeaderIndicator.setImageResource(R.drawable.chevron_down_dark);
            holder.mBinding.textViewQuestion.setTypeface(ResourcesCompat.getFont(mContext, R.font.poppins_regular), Typeface.NORMAL);
            holder.mBinding.textViewAnswer.setVisibility(View.GONE);
        }

        holder.mBinding.layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        holder.mBinding.imageViewHeaderIndicator.setImageResource(R.drawable.chevron_down_dark);

                        mExpandedPosition = position;
                        notifyDataSetChanged();

                    }
                }, 300);
            }
        });


    }

    private void setQuestion(String questionString, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(questionString)) {
            textView.setText(questionString);
        }
    }

    private void setAnswer(String answerString, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(answerString)) {
            textView.setText(answerString);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addValues(ArrayList<FAQuestionAnswer> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutFaqListItemBinding mBinding;

        public ViewHolder(LayoutFaqListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}