package in.securelearning.lil.android.thirdparty.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutWikiHowListItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHow;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;

public class WikiHowRecyclerViewAdapter extends RecyclerView.Adapter<WikiHowRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<WikiHowParent> mList;

    public WikiHowRecyclerViewAdapter(Context context, ArrayList<WikiHowParent> list) {
        mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public WikiHowRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutWikiHowListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_wiki_how_list_item, parent, false);
        return new WikiHowRecyclerViewAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final WikiHowRecyclerViewAdapter.ViewHolder holder, int position) {
        final WikiHowParent response = mList.get(position);
        setThumbnail(holder.mBinding.imageViewBackground, response.getWikiHow());
        setCardTitle(holder.mBinding.textViewTitle, response.getWikiHow());

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    WebPlayerCordovaLiveActivity.startWebPlayerForWikiHow(mContext, response.getWikiHow().getId());
                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.connect_internet));
                }


            }
        });
    }

    private void setCardTitle(AppCompatTextView textView, WikiHow wikiHow) {
        if (wikiHow != null && !TextUtils.isEmpty(wikiHow.getTitle())) {
            textView.setText(wikiHow.getTitle());
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);

        }
    }

    private void setThumbnail(ImageView imageView, WikiHow wikiHow) {
        if (wikiHow != null && wikiHow.getThumbnail() != null) {
            if (!TextUtils.isEmpty(wikiHow.getThumbnail().getThumb())) {
                Picasso.with(mContext).load(wikiHow.getThumbnail().getThumb()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);

            } else if (!TextUtils.isEmpty(wikiHow.getThumbnail().getThumbLarge())) {
                Picasso.with(mContext).load(wikiHow.getThumbnail().getThumbLarge()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);

            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);

            }
        } else {
            Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutWikiHowListItemBinding mBinding;

        ViewHolder(LayoutWikiHowListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}