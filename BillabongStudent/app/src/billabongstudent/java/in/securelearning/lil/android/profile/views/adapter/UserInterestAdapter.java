package in.securelearning.lil.android.profile.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutUserInterestListItemBinding;
import in.securelearning.lil.android.profile.dataobject.UserInterest;

public class UserInterestAdapter extends RecyclerView.Adapter<UserInterestAdapter.ViewHolder> {

    private ArrayList<UserInterest> mList;
    private Context mContext;

    public UserInterestAdapter(Context context, ArrayList<UserInterest> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public UserInterestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutUserInterestListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_user_interest_list_item, parent, false);
        return new UserInterestAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserInterestAdapter.ViewHolder holder, int position) {
        UserInterest interest = mList.get(position);

        if (!TextUtils.isEmpty(interest.getName())) {
            holder.mBinding.textView.setVisibility(View.VISIBLE);
            holder.mBinding.textView.setText(interest.getName());
        } else {
            holder.mBinding.textView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutUserInterestListItemBinding mBinding;

        private ViewHolder(LayoutUserInterestListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}