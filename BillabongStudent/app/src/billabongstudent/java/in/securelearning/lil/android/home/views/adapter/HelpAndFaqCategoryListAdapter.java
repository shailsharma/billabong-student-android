package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHelpAndFaqCategoryListItemBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqCategory;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqModule;

public class HelpAndFaqCategoryListAdapter extends RecyclerView.Adapter<HelpAndFaqCategoryListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<HelpAndFaqCategory> mList;
    private int mExpandedPosition = 0;

    public HelpAndFaqCategoryListAdapter(Context context, ArrayList<HelpAndFaqCategory> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public HelpAndFaqCategoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutHelpAndFaqCategoryListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_help_and_faq_category_list_item, parent, false);
        return new HelpAndFaqCategoryListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final HelpAndFaqCategoryListAdapter.ViewHolder holder, final int position) {
        final HelpAndFaqCategory helpAndFaqCategory = mList.get(position);

        setCategoryName(helpAndFaqCategory.getCategory(), holder.mBinding.textViewCategory);
        initializeRecyclerView(helpAndFaqCategory.getHelpAndFaqModuleList(), holder.mBinding);


        if (mExpandedPosition == position) {
            holder.mBinding.imageViewHeaderIndicator.setImageResource(R.drawable.chevron_up_dark_24dp);
            holder.mBinding.recyclerViewModule.setVisibility(View.VISIBLE);
        } else {
            holder.mBinding.imageViewHeaderIndicator.setImageResource(R.drawable.chevron_down_dark);
            holder.mBinding.recyclerViewModule.setVisibility(View.GONE);
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

    private void setCategoryName(String category, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(category)) {
            textView.setText(category);
        }
    }

    private void initializeRecyclerView(ArrayList<HelpAndFaqModule> helpAndFaqModuleList, LayoutHelpAndFaqCategoryListItemBinding binding) {
        if (!helpAndFaqModuleList.isEmpty()) {
            binding.recyclerViewModule.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            HelpAndFaqModuleListAdapter faqModuleListAdapter = new HelpAndFaqModuleListAdapter(mContext, helpAndFaqModuleList);
            binding.recyclerViewModule.setAdapter(faqModuleListAdapter);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addValues(ArrayList<HelpAndFaqCategory> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutHelpAndFaqCategoryListItemBinding mBinding;

        public ViewHolder(LayoutHelpAndFaqCategoryListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}