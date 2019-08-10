package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.app.databinding.ActivityEventItemBinding;
import in.securelearning.lil.android.app.databinding.AnnouncementEventItemBinding;
import in.securelearning.lil.android.app.databinding.FragmentBulletinBinding;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.customchrometabutils.LinkTransformationMethod;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.model.CalendarEventModel;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 2/25/2017.
 */
public class BulletinFragment extends BaseLNFragment {

    private View mRootView;

    @Inject
    CalendarEventModel mCalendarEventModel;

    @Inject
    RxBus mRxBus;

    private Disposable mSubscription;

    FragmentBulletinBinding mBinding;

    private CalendarEventAdapter mCalendarEventAdapter;
    private int mColumnCount;
    private String mSelectedGroupObjectId;
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;
    private String mStartTime = "";
    private String mEndTime = "";

    public static BulletinFragment newInstance() {
        BulletinFragment bulletinFragment = new BulletinFragment();
        return bulletinFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bulletin, container, false);
        mRootView = mBinding.getRoot();
//        final String splashPath = AppPrefs.getSplashPath(getContext());
//        if (!TextUtils.isEmpty(splashPath)) {
//            mRootView.setBackground(new BitmapDrawable(getResources(), FileUtils.getPathFromFilePath(splashPath)));
//        }
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        if (getArguments() != null) {
            mSelectedGroupObjectId = getArguments().getString(ARG_GROUP_OBJECT_ID);
        }

        setupSubscription();
        setDefault();
        setupRecyclerViewForPost(new ArrayList<CalendarEvent>());
        getData(mStartTime, mEndTime, mSkip, mLimit);

        return mRootView;
    }

    private void setDefault() {
        mSkip = 0;
        if (mCalendarEventAdapter != null) {
            mCalendarEventAdapter.clear();
        }
    }

    private void getData(final String startTime, final String endTime, final int skip, final int limit) {
//        mCalendarEventModel.fetchAllAnnouncementNdActivityEventList(mSelectedGroupObjectId);
//        mCalendarEventModel.getAnnouncementAndActivityEventList(startTime, endTime, skip, limit).subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ArrayList<CalendarEvent>>() {
//                    @Override
//                    public void accept(ArrayList<CalendarEvent> calendarEvents) {
//                        mSkip += calendarEvents.size();
//                        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
//                        for (int i = 0; i < calendarEvents.size(); i++) {
//                            CalendarEvent calendarEvent = calendarEvents.get(i);
//                            if (!calendarEvent.getEventType().equals(EventType.TYPE_PERSONAL.getEventType())) {
//                                events.add(calendarEvent);
//                            }
//
//                        }
//                        mPreviousTotal = events.size();
//                        if (mSkip > 0) {
//                            addItemToRecyclerView(events);
//                            mBinding.layoutNoBulletin.setVisibility(View.GONE);
//                            mBinding.recyclerViewBulletin.setVisibility(View.VISIBLE);
//                        } else {
//                            mBinding.layoutNoBulletin.setVisibility(View.VISIBLE);
//                            mBinding.recyclerViewBulletin.setVisibility(View.GONE);
//                        }
//                        if (calendarEvents.size() < limit) {
//                            mBinding.recyclerViewBulletin.removeOnScrollListener(null);
//                        }
////
//                    }
//                });
    }

    private void addItemToRecyclerView(ArrayList<CalendarEvent> calendarEvents) {
        if (mCalendarEventAdapter != null) {
            mCalendarEventAdapter.addItems(calendarEvents);
        }

    }

    /**
     * set up Disposable to listen to RxBus
     */
    private void setupSubscription() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object eventObject) {
                if (eventObject instanceof ObjectDownloadComplete) {
                    if (((ObjectDownloadComplete) eventObject).getObjectClass().equals(CalendarEvent.class)) {
                        setDefault();
                        getData(mStartTime, mEndTime, mSkip, mLimit);
                    }

                    //setupRecyclerViewForPost(((LoadAnnouncementNdActivityListEvent) eventObject).getEventList());
                }

            }
        });
    }

    private void setupRecyclerViewForPost(ArrayList<CalendarEvent> eventList) {

        LinearLayoutManager layoutManager = null;

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewBulletin.setLayoutManager(layoutManager);

        mCalendarEventAdapter = new CalendarEventAdapter(getActivity(), eventList);
        mBinding.recyclerViewBulletin.setAdapter(mCalendarEventAdapter);


        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerViewBulletin.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {

                            getData(mStartTime, mEndTime, mSkip, mLimit);

                        }
                    }

                }

            });
        }

    }

    class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.ViewHolder> {

        ArrayList<CalendarEvent> mEventList = null;
        private Context mContext;

        public CalendarEventAdapter(Context context, ArrayList<CalendarEvent> eventList) {
            this.mEventList = eventList;
            this.mContext = context;
        }

        @Override
        public BulletinFragment.CalendarEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 0) {
                AnnouncementEventItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.announcement_event_item, parent, false);
                return new BulletinFragment.CalendarEventAdapter.ViewHolderAnnouncement(binding);

            } else {
                ActivityEventItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_event_item, parent, false);
                return new BulletinFragment.CalendarEventAdapter.ViewHolderActivity(binding);

            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mEventList.get(position);
            if (holder instanceof ViewHolderAnnouncement) {
                ((ViewHolderAnnouncement) holder).mBinding.textViewActivityTitle.setText(holder.mItem.getEventTitle());
                ((ViewHolderAnnouncement) holder).mBinding.textViewLocation.setText(holder.mItem.getLocation().getCity());
                ((ViewHolderAnnouncement) holder).mBinding.textViewDescription.setText(TextViewMore.viewMore(holder.mItem.getDescription(),
                        ((ViewHolderAnnouncement) holder).mBinding.textViewDescription,
                        ((ViewHolderAnnouncement) holder).mBinding.layoutTextViewMoreLess.textViewMoreLess));

                ((ViewHolderAnnouncement) holder).mBinding.textViewDescription.setTransformationMethod(new LinkTransformationMethod(mContext, Linkify.WEB_URLS |
                        Linkify.EMAIL_ADDRESSES |
                        Linkify.PHONE_NUMBERS, R.color.colorLearningNetworkPrimary));
                ((ViewHolderAnnouncement) holder).mBinding.textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());

                //((ViewHolderAnnouncement) holder).mBinding.textViewInvitees.setText(holder.mItem.getAudience().get(0));
                ((ViewHolderAnnouncement) holder).mBinding.textViewDate.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(holder.mItem.getStartDate())));
                if (!holder.mItem.getAttachments().isEmpty()) {
                    ((ViewHolderAnnouncement) holder).mBinding.textViewAttachments.setVisibility(View.VISIBLE);
                    ((ViewHolderAnnouncement) holder).mBinding.recyclerViewResource.setVisibility(View.VISIBLE);
                    ((ViewHolderAnnouncement) holder).mBinding.textViewAttachments.setText(getString(R.string.attachments) + " " + String.valueOf(holder.mItem.getAttachments().size()));
                    ((ViewHolderAnnouncement) holder).mBinding.recyclerViewResource.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(mContext, holder.mItem.getAttachments());
                    ((ViewHolderAnnouncement) holder).mBinding.recyclerViewResource.setAdapter(mResourceGridAdapter);
                } else {
                    ((ViewHolderAnnouncement) holder).mBinding.textViewAttachments.setVisibility(View.GONE);
                    ((ViewHolderAnnouncement) holder).mBinding.recyclerViewResource.setVisibility(View.GONE);
                }
            } else if (holder instanceof ViewHolderActivity) {
                ((ViewHolderActivity) holder).mBinding.textViewActivityTitle.setText(holder.mItem.getEventTitle());
                ((ViewHolderActivity) holder).mBinding.textViewLocation.setText(holder.mItem.getLocation().getCity());
                ((ViewHolderActivity) holder).mBinding.textViewPeriod.setText(holder.mItem.getPeriodFrom() + " to " + holder.mItem.getPeriodTo());
                ((ViewHolderActivity) holder).mBinding.textViewDescription.setText(TextViewMore.viewMore(holder.mItem.getDescription(),
                        ((ViewHolderActivity) holder).mBinding.textViewDescription,
                        ((ViewHolderActivity) holder).mBinding.layoutTextViewMoreLess.textViewMoreLess));

                ((ViewHolderActivity) holder).mBinding.textViewDescription.setTransformationMethod(new LinkTransformationMethod(mContext, Linkify.WEB_URLS |
                        Linkify.EMAIL_ADDRESSES |
                        Linkify.PHONE_NUMBERS, R.color.colorLearningNetworkPrimary));
                ((ViewHolderActivity) holder).mBinding.textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());

                ((ViewHolderActivity) holder).mBinding.textViewDate.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(holder.mItem.getStartDate())));
                if (!holder.mItem.getAttachments().isEmpty()) {
                    ((ViewHolderActivity) holder).mBinding.textViewAttachments.setVisibility(View.VISIBLE);
                    ((ViewHolderActivity) holder).mBinding.textViewAttachments.setText(getString(R.string.attachments) + " " + String.valueOf(holder.mItem.getAttachments().size()));
                    ((ViewHolderActivity) holder).mBinding.recyclerViewResource.setVisibility(View.VISIBLE);
                    ((ViewHolderActivity) holder).mBinding.recyclerViewResource.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(mContext, holder.mItem.getAttachments());
                    ((ViewHolderActivity) holder).mBinding.recyclerViewResource.setAdapter(mResourceGridAdapter);
                } else {
                    ((ViewHolderActivity) holder).mBinding.textViewAttachments.setVisibility(View.GONE);
                    ((ViewHolderActivity) holder).mBinding.recyclerViewResource.setVisibility(View.GONE);
                }

                if (holder.mItem.getAllDay()) {
                    ((ViewHolderActivity) holder).mBinding.textViewPeriod.setText("For all day");
                } else {
                    if (holder.mItem.getPeriodTo() == null) {
                        ((ViewHolderActivity) holder).mBinding.textViewPeriod.setText(holder.mItem.getPeriodFrom());
                    } else {
                        ((ViewHolderActivity) holder).mBinding.textViewPeriod.setText(holder.mItem.getPeriodFrom() + " to " + holder.mItem.getPeriodTo());
                    }
                }

            }

        }

        @Override
        public int getItemViewType(int position) {
            CalendarEvent calendarEvent = mEventList.get(position);
            if (calendarEvent.getEventType().equalsIgnoreCase(EventType.TYPE_ANNOUNCEMENT.getEventType())) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return mEventList.size();
        }

        public void addItems(ArrayList<CalendarEvent> calendarEvents) {
            mEventList.addAll(calendarEvents);
            notifyDataSetChanged();
        }

        public void clear() {
            mEventList.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CalendarEvent mItem;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ViewHolderAnnouncement extends ViewHolder {
            public final AnnouncementEventItemBinding mBinding;
            public CalendarEvent mItem;

            public ViewHolderAnnouncement(AnnouncementEventItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }

        }

        public class ViewHolderActivity extends ViewHolder {
            public final ActivityEventItemBinding mBinding;
            public CalendarEvent mItem;

            public ViewHolderActivity(ActivityEventItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.dispose();
    }

    private class ResourceGridAdapter extends RecyclerView.Adapter<ResourceGridAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<Resource> mPathArrayList = new ArrayList<>();
        private String filePath = "";

        public ResourceGridAdapter(Context context, ArrayList<Resource> mAttachmentPathList) {
            this.mContext = context;
            this.mPathArrayList = mAttachmentPathList;
        }

        @Override
        public ResourceGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_attachments_item_view, parent, false);
            ResourceGridAdapter.ViewHolder mViewHolder = new ResourceGridAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ResourceGridAdapter.ViewHolder holder, final int position) {
            if (!TextUtils.isEmpty(mPathArrayList.get(position).getDeviceURL())) {
                filePath = mPathArrayList.get(position).getDeviceURL();
            } else if (!TextUtils.isEmpty(mPathArrayList.get(position).getUrlMain())) {
                filePath = mPathArrayList.get(position).getUrlMain();
            } else if (!TextUtils.isEmpty(mPathArrayList.get(position).getSourceURL())) {
                filePath = mPathArrayList.get(position).getSourceURL();
            }

            String fileType = URLConnection.guessContentTypeFromName(filePath);

            if (fileType != null && fileType.contains("image")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_white);
                Picasso.with(mContext).load(filePath).resize(300, 300).centerCrop().into(holder.mResourceImageView);
            } else if (fileType != null && fileType.contains("video")) {
                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 300, 300, false);
                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "any_Title", null);
                Picasso.with(mContext).load(path).resize(300, 300).centerInside().into(holder.mResourceImageView);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_video_white);
            } else if (fileType != null && fileType.contains("audio")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_audio_white);
            } else if (fileType != null && fileType.contains("pdf")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_pdf_white);
            } else if (fileType != null && fileType.contains("doc")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_document_white);
            } else {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_file_white);
            }

            holder.mRemoveButtonLayout.setVisibility(View.GONE);

            holder.mResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(filePath);
                    if (mimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(getActivity(), position, true,true, mPathArrayList);
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
            RelativeLayout mRemoveButtonLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_attach);
                mResourceTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
                mRemoveResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_remove_attachment);
                mRemoveButtonLayout = (RelativeLayout) mRootView.findViewById(R.id.layout_button_remove);
            }


        }

    }
}
