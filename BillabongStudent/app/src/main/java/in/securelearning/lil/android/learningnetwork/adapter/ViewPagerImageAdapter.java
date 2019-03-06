package in.securelearning.lil.android.learningnetwork.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.widget.TouchImageView;
import in.securelearning.lil.android.app.R;

/**
 * Created by Cp on 8/29/2016.
 */
public class ViewPagerImageAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<Resource> mAttachmentPathList;
    private File mImageFile;
    private String mBaseFolderPath;

    public ViewPagerImageAdapter(Context context, ArrayList<Resource> mAttachmentPathList) {
        mContext = context;
        this.mAttachmentPathList = mAttachmentPathList;
        mBaseFolderPath = mContext.getFilesDir().getAbsolutePath() + File.separator;

    }

    @Override
    public int getCount() {
        return mAttachmentPathList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView mImageView;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.layout_image_viewpager_itemview, container, false);
        mImageView = (TouchImageView) itemView.findViewById(R.id.imageview_viewpager_images);

        if (FileUtils.checkIsFilePath(mAttachmentPathList.get(position).getDeviceURL())) {
            mImageFile = new File(FileUtils.getPathFromFilePath(mAttachmentPathList.get(position).getDeviceURL()));
        } else {
            mImageFile = new File(mBaseFolderPath + mAttachmentPathList.get(position));
        }

        Picasso.with(mContext).load(mImageFile).resize(1000, 1000).centerInside().into(mImageView);


        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}
