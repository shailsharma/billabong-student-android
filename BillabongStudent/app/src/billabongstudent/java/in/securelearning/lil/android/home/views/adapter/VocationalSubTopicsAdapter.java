package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutVocationalSubTopicListItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubTopic;

public class VocationalSubTopicsAdapter extends RecyclerView.Adapter<VocationalSubTopicsAdapter.ViewHolder> {

    private ArrayList<VocationalSubTopic> mList;
    private Context mContext;

    public VocationalSubTopicsAdapter(Context context, ArrayList<VocationalSubTopic> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public VocationalSubTopicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutVocationalSubTopicListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_vocational_sub_topic_list_item, parent, false);
        return new VocationalSubTopicsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final VocationalSubTopicsAdapter.ViewHolder holder, final int position) {
        final VocationalSubTopic subTopic = mList.get(position);

        setTitle(subTopic.getLogiQidsTopicName(), holder.mBinding.textViewTitle);
        setBackgroundImage(subTopic.getLogiQidsImageUrl(), holder.mBinding.imageViewBackground);


        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUtils.showToastShort(mContext, subTopic.getLogiQidsTopicName());
                Log.e("TAG", "onClick: " + subTopic.getLogiQidsTopicName());
            }
        });

    }

    private void setBackgroundImage(String logiQidsImageUrl, AppCompatImageView imageView) {
        if (!TextUtils.isEmpty(logiQidsImageUrl)) {
            Picasso.with(mContext).load(logiQidsImageUrl).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
        }
    }

    private void setTitle(String subTopicName, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(subTopicName)) {
            textView.setText(subTopicName);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutVocationalSubTopicListItemBinding mBinding;

        public ViewHolder(LayoutVocationalSubTopicListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}