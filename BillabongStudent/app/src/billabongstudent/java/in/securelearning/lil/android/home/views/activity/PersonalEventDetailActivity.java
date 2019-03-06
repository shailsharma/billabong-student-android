package in.securelearning.lil.android.home.views.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.net.URLConnection;
import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.widget.ImageViewPager;
import in.securelearning.lil.android.learningnetwork.adapter.ViewPagerImageAdapter;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 3/22/2017.
 */
public class PersonalEventDetailActivity extends AppCompatActivity {

    private TextView mEventDateTextView, mEventNameTextView, mEventTimeTextView, mEventLocationTextView,
            mEventDescriptionTextView;
    private ImageButton mBackButton;
    private CalendarEvent mCalendarEvent;
    private RecyclerView mResourceRecyclerView;
    private String strEventDate;
    private ArrayList<Resource> mAttachmentPathList = new ArrayList<>();
    private String mBaseFolder;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar_personal_event_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPersonal));
        mBaseFolder = getFilesDir().getAbsolutePath();
        mCalendarEvent = (CalendarEvent) getIntent().getSerializableExtra("selectedEvent");
        strEventDate = getIntent().getExtras().getString("eventDate");
        mAttachmentPathList = mCalendarEvent.getAttachments();
        initializeViews();
        initializeUiAndClickListeners();
        initializeRecyclerView();
    }


    private void initializeViews() {
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mEventNameTextView = (TextView) findViewById(R.id.textView_event_name);
        mEventDateTextView = (TextView) findViewById(R.id.textView_event_date);
        mEventTimeTextView = (TextView) findViewById(R.id.textView_event_duration);
        mEventLocationTextView = (TextView) findViewById(R.id.textView_event_location);
        mEventDescriptionTextView = (TextView) findViewById(R.id.textView_event_description);
        mResourceRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewResource);

    }

    private void initializeUiAndClickListeners() {
        mEventDateTextView.setText(strEventDate);
        mEventNameTextView.setText(mCalendarEvent.getEventTitle());
        mEventTimeTextView.setText(mCalendarEvent.getStartTime() + " - " + mCalendarEvent.getEndTime());
        mEventLocationTextView.setText(mCalendarEvent.getLocation().getCity());
        mEventDescriptionTextView.setText(mCalendarEvent.getEventNote());

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializeRecyclerView() {
        if (getBaseContext().getResources().getBoolean(R.bool.isTablet)) {
            mResourceRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
        } else {
            mResourceRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        }

        ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(getBaseContext(), mAttachmentPathList);
        mResourceRecyclerView.setAdapter(mResourceGridAdapter);
    }

    private class ResourceGridAdapter extends RecyclerView.Adapter<ResourceGridAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<Resource> mPathArrayList = new ArrayList<>();

        public ResourceGridAdapter(Context context, ArrayList<Resource> mAttachmentPathList) {
            this.mContext = context;
            this.mPathArrayList = mAttachmentPathList;
        }

        @Override
        public ResourceGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attach_file_view, parent, false);
            ResourceGridAdapter.ViewHolder mViewHolder = new ResourceGridAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ResourceGridAdapter.ViewHolder holder, final int position) {
            String filePath = mPathArrayList.get(position).getDeviceURL();
            Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(),filePath);
            mBitmap = Bitmap.createScaledBitmap(mBitmap, 300, 300, false);
            String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "any_Title", null);
            Picasso.with(mContext).load(path).resize(300, 300).centerInside().into(holder.mResourceImageView);
            String fileType = URLConnection.guessContentTypeFromName(filePath);
            if (fileType.contains("image")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_white);
            } else if (fileType.contains("video")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_video_white);
            } else if (fileType.contains("audio")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_audio_white);
            } else if (fileType.contains("pdf")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_pdf_white);
            } else if (fileType.contains("doc")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_document_white);
            } else {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_file_white);
            }

            holder.mRemoveResourceImageView.setVisibility(View.GONE);

            holder.mResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(mPathArrayList.get(position).getDeviceURL());
                    if (mimeType.contains("image")) {
                        setUpFullImageView(position, true, mPathArrayList);
                    } else if (mimeType.contains("video")) {
                        Resource item = new Resource();
                        item.setType("video");
                        item.setUrlMain(mPathArrayList.get(position).getDeviceURL());
                        mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mPathArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View mRootView;
            ImageView mResourceImageView, mResourceTypeImageView, mRemoveResourceImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_attach);
                mResourceTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
                mRemoveResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_remove_attachment);
            }
        }
    }

    /**
     * setup view for showing images in full view
     * user can slide images.
     *
     * @param position
     * @param isComesFromGrid
     * @param mAttachmentPathList
     */
    public void setUpFullImageView(int position, boolean isComesFromGrid, ArrayList<Resource> mAttachmentPathList) {
        final Dialog mDialog = new Dialog(PersonalEventDetailActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_gallery_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGrey77)));
        final ImageViewPager mImageViewPager = (ImageViewPager) mDialog.findViewById(R.id.viewpager_images);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.button_back);
        final ImageButton mPreviousButton = (ImageButton) mDialog.findViewById(R.id.button_previous);
        final ImageButton mNextButton = (ImageButton) mDialog.findViewById(R.id.button_next);

        setUpFullImageViewItem(mImageViewPager, mAttachmentPathList);
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
            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);

            mImageViewPager.setCurrentItem(position, true);
        } else {

            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * set up viewpager item for showing images
     *
     * @param mImageViewPager
     * @param mAttachmentPathList
     */
    private void setUpFullImageViewItem(ViewPager mImageViewPager, ArrayList<Resource> mAttachmentPathList) {
        ViewPagerImageAdapter mViewPagerImageAdapter;
        mViewPagerImageAdapter = new ViewPagerImageAdapter(getBaseContext(), mAttachmentPathList);
        mImageViewPager.setAdapter(mViewPagerImageAdapter);
        mImageViewPager.setOffscreenPageLimit(2);
    }

    private ArrayList<Resource> getResourceArrayList(ArrayList<String> mAttachmentPathList) {
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
}
