package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutVocationalTopicListItemBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopic;

public class VocationalTopicsAdapter extends RecyclerView.Adapter<VocationalTopicsAdapter.ViewHolder> {

    private ArrayList<VocationalTopic> mList;
    private Context mContext;

    public VocationalTopicsAdapter(Context context, ArrayList<VocationalTopic> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public VocationalTopicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutVocationalTopicListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_vocational_topic_list_item, parent, false);
        return new VocationalTopicsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final VocationalTopicsAdapter.ViewHolder holder, final int position) {
        VocationalTopic topic = mList.get(position);

        holder.mBinding.imageViewThirdParty.setVisibility(View.VISIBLE);

        setTitle(topic.getTopicName(), holder.mBinding.textViewHeader);
        setRecyclerViewList(holder.mBinding.recyclerViewSubTopic, holder.mBinding.textViewErrorSubTopic, topic.getVocationalSubTopics());

    }

    private void setRecyclerViewList(RecyclerView recyclerViewSubTopic, AppCompatTextView textViewErrorSubTopic, ArrayList<VocationalSubTopic> vocationalSubTopics) {

        if (vocationalSubTopics != null && !vocationalSubTopics.isEmpty()) {
            recyclerViewSubTopic.setVisibility(View.VISIBLE);
            textViewErrorSubTopic.setVisibility(View.GONE);

            LinearLayoutManager layoutManager;
            if (mContext.getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(mContext, 3);
            } else {
                layoutManager = new GridLayoutManager(mContext, 2);
            }

            recyclerViewSubTopic.setLayoutManager(layoutManager);
            VocationalSubTopicsAdapter subTopicsAdapter = new VocationalSubTopicsAdapter(mContext, vocationalSubTopics);
            recyclerViewSubTopic.setAdapter(subTopicsAdapter);


        } else {
            recyclerViewSubTopic.setVisibility(View.GONE);
            textViewErrorSubTopic.setVisibility(View.VISIBLE);
        }

    }

    private void setTitle(String topicName, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(topicName)) {
            textView.setText(topicName);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutVocationalTopicListItemBinding mBinding;

        public ViewHolder(LayoutVocationalTopicListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}