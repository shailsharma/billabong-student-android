package in.securelearning.lil.android.learningnetwork.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Cp on 8/29/2016.
 */
public class RecyclerViewImageAdapter extends RecyclerView.Adapter<RecyclerViewImageAdapter.ViewHolder> {
    private final String mBaseFolderPath;
    Context mContext;
    ArrayList<Resource> mAttachResourcesList;
    private File mImageFile;

    public RecyclerViewImageAdapter(Context context, ArrayList<Resource> mAttachResourcesList) {
        mContext = context;
        this.mAttachResourcesList = mAttachResourcesList;
        mBaseFolderPath = context.getFilesDir().getAbsolutePath() + File.separator;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_recycler_itemview, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (FileUtils.checkIsFilePath(mAttachResourcesList.get(position).getDeviceURL())) {
            mImageFile = new File(FileUtils.getPathFromFilePath(mAttachResourcesList.get(position).getDeviceURL()));
        } else {
            mImageFile = new File(mBaseFolderPath + mAttachResourcesList.get(position));
        }


        String mimeType = URLConnection.guessContentTypeFromName(mAttachResourcesList.get(position).getDeviceURL());
        if (mimeType.contains("image")) {
            Picasso.with(mContext).load(mImageFile).into(holder.mImageView);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_image_white);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FullScreenImage.setUpFullImageView(mContext, position, true,true, mAttachResourcesList);

                }
            });
        } else if (mimeType.contains("video")) {
            Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), mAttachResourcesList.get(position).getDeviceURL());
            String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "any_Title", null);
            Picasso.with(mContext).load(path).noFade().into(holder.mImageView);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_video_white);
        } else if (mimeType.contains("audio")) {
            holder.mImageView.setImageResource(R.drawable.gradient_black);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_audio_white);

        } else if (mimeType.contains("pdf")) {
            holder.mImageView.setImageResource(R.drawable.gradient_black);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_pdf_white);

        } else if (mimeType.contains("doc")) {
            holder.mImageView.setImageResource(R.drawable.gradient_black);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_document_white);
        } else {
            holder.mImageView.setImageResource(R.drawable.gradient_black);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_file_white);
        }
    }

    @Override
    public int getItemCount() {
        return mAttachResourcesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private ImageView mImageView, mFileTypeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mImageView = (ImageView) mRootView.findViewById(R.id.imageview_resource);
            mFileTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
        }
    }
}
