package in.securelearning.lil.android.thirdparty.views.adapter;

import android.databinding.DataBindingUtil;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSimpleTextBinding;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkDropdownQuestionChoice;

public class DropdownChoiceAdapter extends RecyclerView.Adapter<DropdownChoiceAdapter.ViewHolder> {
    private ArrayList<Object> mList;
    private BottomSheetDialog mBottomSheetDialog;
    private TextInputEditText mAppCompatTextView;

    public DropdownChoiceAdapter(ArrayList<Object> list, BottomSheetDialog bottomSheetDialog, TextInputEditText appCompatTextView) {
        this.mList = list;
        this.mBottomSheetDialog = bottomSheetDialog;
        this.mAppCompatTextView = appCompatTextView;
    }

    @Override
    public DropdownChoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutSimpleTextBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_simple_text, parent, false);
        return new DropdownChoiceAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DropdownChoiceAdapter.ViewHolder holder, final int position) {
        final Object object = mList.get(position);
        final MindSparkDropdownQuestionChoice choice = (MindSparkDropdownQuestionChoice) object;
        holder.mBinding.textView.setText(choice.getValue());
        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAppCompatTextView.setText(((MindSparkDropdownQuestionChoice) object).getValue());
                int correct = choice.getScore();
                if (correct == 1) {
                    mAppCompatTextView.setTag(R.id.isCorrect, true);
                } else {
                    mAppCompatTextView.setTag(R.id.isCorrect, false);
                }
                mAppCompatTextView.setTag(R.id.correctAnswer, String.valueOf(position));
                mBottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutSimpleTextBinding mBinding;

        public ViewHolder(LayoutSimpleTextBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
