package in.securelearning.lil.android.player.view.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutQuestionResourceListItemBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.player.view.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

public class QuestionResourceAdapter extends RecyclerView.Adapter<QuestionResourceAdapter.ViewHolder> {

    @Inject
    PlayerModel mPlayerModel;

    private ArrayList<Resource> mEvidenceList;
    private Context mContext;
    private String mModuleId, mModuleName;

    public QuestionResourceAdapter(Context context, ArrayList<Resource> list, String moduleId, String moduleName) {
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mEvidenceList = list;
        mContext = context;
        mModuleId = moduleId;
        mModuleName = moduleName;
    }

    @NonNull
    @Override
    public QuestionResourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutQuestionResourceListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_question_resource_list_item, parent, false);
        return new QuestionResourceAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionResourceAdapter.ViewHolder holder, final int position) {
        final Resource resource = mEvidenceList.get(position);

        setThumbnail(resource, holder.mBinding.imageViewThumbnail);
        setResourceType(resource.getResourceType(), holder.mBinding.imageViewType);
        onResourceClick(holder.mBinding.imageViewThumbnail, resource, holder.mBinding);

    }

    private void setThumbnail(Resource resource, ImageView imageView) {
        if (!TextUtils.isEmpty(resource.getUrlThumbnail())) {
            Picasso.with(mContext).load(resource.getUrlThumbnail()).into(imageView);
        }
    }

    private void setResourceType(String resourceType, AppCompatImageView imageViewType) {
        if (resourceType.contains(Resource.TYPE_RESOURCE_IMAGE)) {
            imageViewType.setImageResource(R.drawable.icon_magnify_blue);
        } else {
            imageViewType.setImageResource(R.drawable.icon_video);
        }
    }

    private void onResourceClick(AppCompatImageView imageViewThumbnail, final Resource resource, final LayoutQuestionResourceListItemBinding binding) {

        final String resourceUrl;

        if (!TextUtils.isEmpty(resource.getUrl())) {
            resourceUrl = resource.getUrl();
        } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
            resourceUrl = resource.getUrlMain();
        } else {
            resourceUrl = null;
        }

        imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mimeType = resource.getResourceType();
                if (!TextUtils.isEmpty(resourceUrl)) {
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains(Resource.TYPE_RESOURCE_IMAGE)) {
                        mContext.startActivity(PlayFullScreenImageActivity.getStartIntent(mContext, resourceUrl, true));
                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains(Resource.TYPE_RESOURCE_VIDEO)) {
                        mPlayerModel.playVideo(mModuleId, mModuleName, resource);
                    }
                } else {
                    SnackBarUtils.showSnackBar(mContext, binding.getRoot(), mContext.getString(R.string.error_something_went_wrong), SnackBarUtils.UNSUCCESSFUL);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvidenceList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutQuestionResourceListItemBinding mBinding;

        public ViewHolder(LayoutQuestionResourceListItemBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
        }

    }

}