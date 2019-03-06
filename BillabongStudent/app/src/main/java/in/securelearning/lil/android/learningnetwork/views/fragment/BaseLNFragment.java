package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.widget.ImageViewPager;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.learningnetwork.adapter.RecyclerViewImageAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.ViewPagerImageAdapter;


public class BaseLNFragment extends Fragment {

    public static final String ARG_GROUP_OBJECT_ID = "group_object_id";
    public static final String ARG_GROUP_SELECTED_INDEX = "selectedGroupIndex";
    private GridLayoutManager mLayoutManager;
    private ImageViewPager mImageViewPager;

    public static BaseLNFragment newInstance(String groupId) {
        BaseLNFragment baseLNFragment = new BaseLNFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BaseLNFragment.ARG_GROUP_OBJECT_ID, groupId);
        baseLNFragment.setArguments(bundle);
        return baseLNFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        return view;
    }


    /**
     * setup grid view for resources of post like images for now
     */
    public void setUpImageGridView(ArrayList<Resource> mAttachResourcesList, String strHeaderText) {

        final Dialog mDialog = new Dialog(getActivity());
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
        setUpImageGridViewItem(mImageRecyclerView, mAttachResourcesList);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

    }

    /**
     * set up recycler view item contain post images
     *
     * @param mImageRecyclerView
     * @param mAttachResourcesList
     */
    private void setUpImageGridViewItem(RecyclerView mImageRecyclerView, ArrayList<Resource> mAttachResourcesList) {

        RecyclerViewImageAdapter mRecyclerViewImageAdapter;
        if (getResources().getBoolean(R.bool.isTablet)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        mImageRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewImageAdapter = new RecyclerViewImageAdapter(getActivity(), mAttachResourcesList);
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
    public void setUpFullImageView(int position, boolean isComesFromGrid, ArrayList<Resource> mAttachmentPathList) {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_gallery_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
        mImageViewPager = (ImageViewPager) mDialog.findViewById(R.id.viewpager_images);
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
        mViewPagerImageAdapter = new ViewPagerImageAdapter(getActivity(), mAttachmentPathList);
        mImageViewPager.setAdapter(mViewPagerImageAdapter);
        mImageViewPager.setOffscreenPageLimit(2);
    }


}
