package in.securelearning.lil.android.learningnetwork.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_image_grid_view);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerview_gridview);
        ImageButton closeDialogButton = (ImageButton) dialog.findViewById(R.id.button_close);
        TextView headerTextView = (TextView) dialog.findViewById(R.id.textview_grid_dialog_header);
        headerTextView.setText(strHeaderText);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        setUpImageGridViewItem(context, recyclerView, mAttachResourcesList);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    /**
     * set up recycler view item contain post images
     *
     * @param recyclerView
     * @param mAttachResourcesList
     */
    private static void setUpImageGridViewItem(Context context, RecyclerView recyclerView, ArrayList<Resource> mAttachResourcesList) {

        RecyclerViewImageAdapter recyclerViewImageAdapter;
        GridLayoutManager layoutManager;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(context, 3);
        } else {
            layoutManager = new GridLayoutManager(context, 2);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerViewImageAdapter = new RecyclerViewImageAdapter(context, mAttachResourcesList);
        recyclerView.setAdapter(recyclerViewImageAdapter);

    }

    /**
     * setup view for showing images in full view
     * user can slide images.
     *
     * @param position
     * @param isComesFromGrid
     * @param isNetworkResource
     * @param attachmentPathList
     */
    public static void setUpFullImageView(Context context, int position, boolean isComesFromGrid, boolean isCountEnabled, boolean isNetworkResource, final ArrayList<Resource> attachmentPathList) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_gallery_view);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorGrey77)));
        final ImageViewPager imageViewPager = dialog.findViewById(R.id.viewpager_images);
        ImageButton closeButton = dialog.findViewById(R.id.button_back);

        final LinearLayout previousButton = dialog.findViewById(R.id.button_previous);
        final LinearLayout nextButton = dialog.findViewById(R.id.button_next);
        final TextView attachmentCountsTextView = dialog.findViewById(R.id.textViewAttachmentCounts);
        if (!isCountEnabled) {
            attachmentCountsTextView.setVisibility(ViewPager.GONE);
        }
        setUpFullImageViewItem(context, isNetworkResource, imageViewPager, attachmentPathList);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewPager.setCurrentItem(imageViewPager.getCurrentItem() - 1, true);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewPager.setCurrentItem(imageViewPager.getCurrentItem() + 1, true);
            }
        });

        imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String toolbarTitle = (position + 1) + " of " + attachmentPathList.size();
                attachmentCountsTextView.setText(toolbarTitle);
                if (position == 0) {
                    previousButton.setVisibility(View.INVISIBLE);

                } else {
                    previousButton.setVisibility(View.VISIBLE);
                }

                if (position == (imageViewPager.getAdapter().getCount() - 1)) {
                    nextButton.setVisibility(View.INVISIBLE);

                } else {
                    nextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();

        if (isComesFromGrid) {
            attachmentCountsTextView.setText(1 + " of " + String.valueOf(attachmentPathList.size()));
            if (position == 0) previousButton.setVisibility(View.INVISIBLE);

            if (imageViewPager.getAdapter().getCount() <= 1)
                nextButton.setVisibility(View.INVISIBLE);

            imageViewPager.setCurrentItem(position, true);
        } else {
            attachmentCountsTextView.setText(String.valueOf(1) + " of " + String.valueOf(attachmentPathList.size()));
            if (position == 0) previousButton.setVisibility(View.INVISIBLE);

            if (imageViewPager.getAdapter().getCount() <= 1)
                nextButton.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * set up viewpager item for showing images
     *
     * @param context
     * @param isNetworkResource
     * @param imageViewPager
     * @param mAttachmentPathList
     */
    private static void setUpFullImageViewItem(Context context, boolean isNetworkResource, ViewPager imageViewPager, ArrayList<Resource> mAttachmentPathList) {
        ViewPagerImageAdapter viewPagerImageAdapter = new ViewPagerImageAdapter(context, isNetworkResource, mAttachmentPathList);
        imageViewPager.setAdapter(viewPagerImageAdapter);
        imageViewPager.setOffscreenPageLimit(1);
    }

    public static ArrayList<Resource> getResourceArrayList(ArrayList<String> attachmentPathList) {
        Resource resource;
        ArrayList<Resource> resources = new ArrayList<>();

        for (int i = 0; i < attachmentPathList.size(); i++) {
            resource = new Resource();
            String mimeType = URLConnection.guessContentTypeFromName(attachmentPathList.get(i));
            if (mimeType.contains("image")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
            } else if (mimeType.contains("video")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_VIDEO);
            } else if (mimeType.contains("audio")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_AUDIO);
            } else if (mimeType.contains("application")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_DOC);
            }
            resource.setDeviceURL(attachmentPathList.get(i));
            resources.add(resource);

        }

        return resources;
    }

    public static class ViewPagerImageAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater inflater;
        ArrayList<Resource> mAttachmentPathList;
        private String mBaseFolderPath;
        private boolean mIsNetworkResource;

        ViewPagerImageAdapter(Context context, boolean isNetworkResource, ArrayList<Resource> mAttachmentPathList) {
            this.mContext = context;
            this.mAttachmentPathList = mAttachmentPathList;
            this.mIsNetworkResource = isNetworkResource;
            this.mBaseFolderPath = mContext.getFilesDir().getAbsolutePath() + File.separator;

        }


        @Override
        public int getCount() {
            return mAttachmentPathList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.layout_image_viewpager_itemview, container, false);

            TouchImageView imageView = itemView.findViewById(R.id.imageview_viewpager_images);

            Resource resource = mAttachmentPathList.get(position);

            if (mIsNetworkResource) {
                String networkFile;
                if (!TextUtils.isEmpty(resource.getUrl())) {
                    networkFile = resource.getUrl();
                } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
                    networkFile = resource.getUrlMain();
                } else if (!TextUtils.isEmpty(resource.getThumbXL())) {
                    networkFile = resource.getThumbXL();
                } else if (!TextUtils.isEmpty(resource.getThumb())) {
                    networkFile = resource.getThumb();
                } else {
                    networkFile = resource.getDeviceURL();
                }
                Picasso.with(mContext).load(networkFile).placeholder(R.drawable.background_transparent).into(imageView);

            } else {
                File localFile;

                if (FileUtils.checkIsFilePath(resource.getDeviceURL())) {
                    localFile = new File(FileUtils.getPathFromFilePath(resource.getDeviceURL()));
                } else {
                    localFile = new File(mBaseFolderPath + resource);
                }
                Picasso.with(mContext).load(localFile).placeholder(R.drawable.background_transparent).into(imageView);
            }


            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);

        }
    }

}
