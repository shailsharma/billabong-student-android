package in.securelearning.lil.android.player.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewSimpleItemBinding;

public class DropdownChoiceItemAdapter extends RecyclerView.Adapter<DropdownChoiceItemAdapter.ViewHolder> {
    private ArrayList<String> mList;

    public DropdownChoiceItemAdapter(ArrayList<String> values) {
        this.mList = values;

    }


    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    @Override
    public DropdownChoiceItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutRecyclerviewSimpleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_recyclerview_simple_item, parent, false);
        return new DropdownChoiceItemAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DropdownChoiceItemAdapter.ViewHolder holder, final int position) {
        holder.mBinding.textviewItem.setOnClickListener(mItemClickListener);
        final String text = mList.get(position);
        holder.mBinding.textviewItem.setText(text);
        holder.mBinding.textviewItem.setPadding(4, 8, 4, 8);

//        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mEditTextDropdown.setText(text);
//                mPopupWindow.dismiss();
//            }
//        });

    }

    public void setItemClickAction(View.OnClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutRecyclerviewSimpleItemBinding mBinding;

        public ViewHolder(LayoutRecyclerviewSimpleItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}