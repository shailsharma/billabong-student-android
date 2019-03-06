package in.securelearning.lil.android.learningnetwork.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.widget.ImageViewPager;
import in.securelearning.lil.android.base.widget.TouchImageView;

/**
 * Created by Chaitendra on 4/8/2017.
 */

public class FullScreenImage {

    public static void setUpImageGridView(Context context, ArrayList<Resource> mAttachResourcesList, String strHeaderText) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_image_grid_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        RecyclerView mImageRecyclerView = (RecyclerView) mDialog.findViewById(R.id.recyclerview_gridview);
        ImageButton mCloseDialogButton = (ImageButton) mDialog.findViewById(R.id.button_close);
        TextView mHeaderTextView = (TextView) mDialog.findViewById(R.id.textview_grid_dialog_header);
        mHeaderTextView.setText(strHeaderText);
        mCloseDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        setUpImageGridViewItem(context, mImageRecyclerView, mAttachResourcesList);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.show();

    }

    /**
     * set up recycler view item contain post images
     *
     * @param mImageRecyclerView
     * @param mAttachResourcesList
     */
    private static void setUpImageGridViewItem(Context context, RecyclerView mImageRecyclerView, ArrayList<Resource> mAttachResourcesList) {

        RecyclerViewImageAdapter mRecyclerViewImageAdapter;
        GridLayoutManager layoutManager;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(context, 3);
        } else {
            layoutManager = new GridLayoutManager(context, 2);
        }

        mImageRecyclerView.setLayoutManager(layoutManager);
        mRecyclerViewImageAdapter = new RecyclerViewImageAdapter(context, mAttachResourcesList);
        mImageRecyclerView.setAdapter(mRecyclerViewImageAdapter);

    }

    /**
     * setup view for showing images in full view
     * user can slide images.
     *
     * @param position
     * @param isComesFromGrid
     * @param mAttachmentPathList
     */
    public static void setUpFullImageView(Context context, int position, boolean isComesFromGrid, boolean isCountEnabled, final ArrayList<Resource> mAttachmentPathList) {
        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_gallery_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorGrey77)));
        final ImageViewPager mImageViewPager = (ImageViewPager) mDialog.findViewById(R.id.viewpager_images);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.button_back);

        final LinearLayout mPreviousButton = (LinearLayout) mDialog.findViewById(R.id.button_previous);
        final LinearLayout mNextButton = (LinearLayout) mDialog.findViewById(R.id.button_next);
        final LinearLayout mToolbarLayout = (LinearLayout) mDialog.findViewById(R.id.layout_toolbar);
        final TextView mAttachmentCountsTextView = (TextView) mDialog.findViewById(R.id.textViewAttachmentCounts);
        if (!isCountEnabled) {
            mAttachmentCountsTextView.setVisibility(ViewPager.GONE);
        }
        setUpFullImageViewItem(context, mImageViewPager, mAttachmentPathList);

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() - 1, true);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() + 1, true);
            }
        });

        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAttachmentCountsTextView.setText(String.valueOf(position + 1) + " of " + String.valueOf(mAttachmentPathList.size()));
                if (position == 0) {
                    mPreviousButton.setVisibility(View.INVISIBLE);

                } else {
                    mPreviousButton.setVisibility(View.VISIBLE);
                }

                if (position == (mImageViewPager.getAdapter().getCount() - 1)) {
                    mNextButton.setVisibility(View.INVISIBLE);

                } else {
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

        if (isComesFromGrid) {
            mAttachmentCountsTextView.setText(String.valueOf(1) + " of " + String.valueOf(mAttachmentPathList.size()));
            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);

            mImageViewPager.setCurrentItem(position, true);
        } else {
            mAttachmentCountsTextView.setText(String.valueOf(1) + " of " + String.valueOf(mAttachmentPathList.size()));
            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * set up viewpager item for showing images
     *
     * @param context
     * @param mImageViewPager
     * @param mAttachmentPathList
     */
    public static void setUpFullImageViewItem(Context context, ViewPager mImageViewPager, ArrayList<Resource> mAttachmentPathList) {
        ViewPagerImageAdapter mViewPagerImageAdapter;
        mViewPagerImageAdapter = new ViewPagerImageAdapter(context, mAttachmentPathList);
        mImageViewPager.setAdapter(mViewPagerImageAdapter);
        mImageViewPager.setOffscreenPageLimit(1);
    }

    public static ArrayList<Resource> getResourceArrayList(ArrayList<String> mAttachmentPathList) {
        Resource resource;
        ArrayList<Resource> resources = new ArrayList<>();

        for (int i = 0; i < mAttachmentPathList.size(); i++) {
            resource = new Resource();
            String mimeType = URLConnection.guessContentTypeFromName(mAttachmentPathList.get(i));
            if (mimeType.contains("image")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
            } else if (mimeType.contains("video")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_VIDEO);
            } else if (mimeType.contains("audio")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_AUDIO);
            } else if (mimeType.contains("application")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_DOC);
            }
            resource.setDeviceURL(mAttachmentPathList.get(i));
            resources.add(resource);

        }

        return resources;
    }

    public static class ViewPagerImageAdapter extends PagerAdapter {
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

            Picasso.with(mContext).load(mImageFile).into(mImageView);


            ((ViewPager) container).addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((LinearLayout) object);

        }
    }

    public static void checkPathOrUrl(Context context, String path, ImageView imageView) {
        if (!TextUtils.isEmpty(path) && imageView != null) {
            if (path.startsWith("http")) {
                Picasso.with(context).load(path).into(imageView);

            } else {
//                if (path.startsWith("file:///")) {
//                    path.replace("file:///", "file://");
//                }
//                if (path.startsWith(":")) {
//                    path = path.substring(1);
//                }
//                if (path.startsWith("/")) {
//                    path = path.substring(1);
//                }
//                if (path.startsWith("/")) {
//                    path = path.substring(1);
//                }
//                if (path.startsWith("/")) {
//                    path = path.substring(1);
//                }
//                File file = new File(path);
                //imageView.setImageDrawable(new BitmapDrawable(path));
                Picasso.with(context).load(new File(path)).into(imageView);

            }
        }
    }
}
