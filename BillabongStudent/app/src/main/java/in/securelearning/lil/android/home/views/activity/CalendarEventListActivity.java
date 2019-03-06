package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutCalendarAnnouncementItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutCalendarCategoryListBinding;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.LoadCalendarEventDownloaded;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Cp on 1/11/2017.
 */
public class CalendarEventListActivity extends AppCompatActivity {


    private String mEventType, mEventDate;
    private int intEventColor;
    @Inject
    GroupModel mGroupModel;
    @Inject
    HomeModel mHomeModel;
    @Inject
    RxBus mRxBus;

    private Disposable mSubscription;
    private LayoutCalendarCategoryListBinding mBinding;
    private EventAdapter mEventAdapter;
    private String mStartDate, mEndDate;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Inject
    CalEventModel mCalEventModel;

    public static Intent startCalendarEventActivity(Context context, String eventType, int eventColor, String startDate, String endDate, String titleDate) {
        Intent intent = new Intent(context, CalendarEventListActivity.class);
        intent.putExtra("eventType", eventType);
        intent.putExtra("eventColor", eventColor);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("titleDate", titleDate);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar_category_list);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mStartDate = getIntent().getStringExtra("startDate");
        mEndDate = getIntent().getStringExtra("endDate");
        mEventDate = getIntent().getStringExtra("titleDate");
        mEventType = getIntent().getExtras().getString("eventType");
        intEventColor = getIntent().getExtras().getInt("eventColor");
        listenRxBusEvents();
        initializeUIAndClickListeners();
        getEventData(mEventType, mStartDate, mEndDate);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void getEventData(final String strEventType, final String startDate, final String endDate) {
        mHomeModel.getEventListOfSelectedDate(strEventType, startDate, endDate).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CalendarEvent>>() {
            @Override
            public void accept(ArrayList<CalendarEvent> calendarEvents) throws Exception {
                initializeRecyclerView(calendarEvents);

            }
        });

    }

    private void initializeUIAndClickListeners() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEventDate = mEventDate.replaceAll("\n", ", ");
        setTitle(mEventDate);
        mBinding.toolbar.setBackgroundColor(intEventColor);
        getWindow().setStatusBarColor(intEventColor);


    }

    private void initializeRecyclerView(List<CalendarEvent> calendarEvents) {
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(CalendarEventListActivity.this, LinearLayoutManager.VERTICAL, false));
        mEventAdapter = new EventAdapter(calendarEvents);
        mBinding.recyclerView.setAdapter(mEventAdapter);

    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof LoadCalendarEventDownloaded) {
                    CalendarEvent calendarEvent = ((LoadCalendarEventDownloaded) event).getCalendarEvent();
                    Date date = DateUtils.convertrIsoDate(calendarEvent.getStartDate());
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE', 'd MMM yyyy");
                    String strDate = formatter.format(date);
                    if (calendarEvent.getEventType().equals(mEventType) && strDate.equals(mEventDate)) {
                        if (mEventAdapter != null) {
                            mEventAdapter.clear();
                            getEventData(mEventType, mStartDate, mEndDate);
                        }
                    }

                }

            }
        });
    }

    private class PersonalEventAdapter extends RecyclerView.Adapter<PersonalEventAdapter.ViewHolder> {

        private List<CalendarEvent> mCalendarEvents = new ArrayList<>();
        private ArrayList<Resource> mAttachmentPathList = new ArrayList<>();
        private Context mContext;
        private List<String> mIds;

        public PersonalEventAdapter(Context baseContext, List<CalendarEvent> calendarEvents) {
            this.mCalendarEvents = calendarEvents;
            this.mContext = baseContext;
            mIds = getIdList(calendarEvents);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calendar_personal_event_list_itemview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final CalendarEvent calendarEvent = mCalendarEvents.get(position);
            String strStartDate = "", strEndDate = "", strStartTime = "", strEndTime = "";
            if (calendarEvent.getStartDate() != null)
                strStartDate = DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(calendarEvent.getStartDate()));
            if (calendarEvent.getEndDate() != null)
                strEndDate = DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(calendarEvent.getEndDate()));
            if (calendarEvent.getStartTime() != null)
                strStartTime = calendarEvent.getStartTime();
            if (calendarEvent.getEndTime() != null)
                strEndTime = calendarEvent.getEndTime();

            holder.mEventTitleTextView.setText(calendarEvent.getEventTitle());
            holder.mEventLocationTextView.setText(calendarEvent.getLocation().getCity());
            holder.mEventStartTextView.setText(strStartDate + ", " + strStartTime);
            holder.mEventEndTextView.setText(strEndDate + ", " + strEndTime);
            holder.mEventDescriptionTextView.setText(TextViewMore.viewMore(calendarEvent.getDescription(), holder.mEventDescriptionTextView, holder.mViewMoreLessTextView));

            mAttachmentPathList = calendarEvent.getAttachments();
            if (!mAttachmentPathList.isEmpty()) {
                holder.mEventAttachmentTextView.setVisibility(View.VISIBLE);
                holder.mResourceRecyclerView.setVisibility(View.VISIBLE);
                holder.mEventAttachmentTextView.setText(getString(R.string.attachments) + " " + String.valueOf(mAttachmentPathList.size()));
                holder.mResourceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(getBaseContext(), mAttachmentPathList);
                holder.mResourceRecyclerView.setAdapter(mResourceGridAdapter);
            } else {
                holder.mEventAttachmentTextView.setVisibility(View.GONE);
                holder.mResourceRecyclerView.setVisibility(View.GONE);
            }

            holder.mEditEventButton.setVisibility(View.GONE);
            holder.mEditEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(getBaseContext(), PersonalEventCreationActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mIntent.putExtra("event", mCalendarEvents.get(position));
                    mContext.startActivity(mIntent);
                }
            });


        }

        private List<String> getIdList(List<CalendarEvent> values) {
            List<String> ids = new ArrayList<>();
            for (CalendarEvent calendarEvent :
                    values) {
                ids.add(calendarEvent.getAlias());
            }
            return ids;
        }

        private void itemRefreshed(CalendarEvent calendarEvent) {
            if (calendarEvent != null) {
                if (mIds.contains(calendarEvent.getAlias())) {
                    for (int i = 0; i < mCalendarEvents.size(); i++) {
                        if (mCalendarEvents.get(i).getAlias().equals(calendarEvent.getAlias())) {
                            notifyItemChanged(i);
                        }
                    }
                }
            }

        }

        @Override
        public int getItemCount() {
            return mCalendarEvents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mAdapterView;
            private TextView mEventTitleTextView, mEventStartTextView, mEventAttachmentTextView,
                    mEventEndTextView, mEventLocationTextView, mEventDescriptionTextView, mViewMoreLessTextView;
            private RecyclerView mResourceRecyclerView;
            private ImageButton mEditEventButton;

            public ViewHolder(View itemView) {
                super(itemView);
                mAdapterView = itemView;

                mEventTitleTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_title);
                mEventStartTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_start);
                mEventEndTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_end);
                mEventLocationTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_location);
                mEventAttachmentTextView = (TextView) mAdapterView.findViewById(R.id.textView_attachments);
                mEventDescriptionTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_description);
                mViewMoreLessTextView = (TextView) mAdapterView.findViewById(R.id.textViewMoreLess);
                mResourceRecyclerView = (RecyclerView) mAdapterView.findViewById(R.id.recyclerViewResource);
                mEditEventButton = (ImageButton) mAdapterView.findViewById(R.id.imageButtonEditEvent);
            }
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

        private List<CalendarEvent> mCalendarEvents = new ArrayList<>();
        private ArrayList<Resource> mAttachmentPathList = new ArrayList<>();
        private Context mContext;

        public ActivityAdapter(Context baseContext, List<CalendarEvent> calendarEvents) {
            this.mCalendarEvents = calendarEvents;
            this.mContext = baseContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calendar_activity_list_itemview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final CalendarEvent calendarEvent = mCalendarEvents.get(position);

            holder.mEventTitleTextView.setText(calendarEvent.getEventTitle());
            holder.mEventLocationTextView.setText(calendarEvent.getLocation().getCity());
            holder.mEventDescriptionTextView.setText(TextViewMore.viewMore(calendarEvent.getDescription(), holder.mEventDescriptionTextView, holder.mViewMoreLessTextView));
            holder.mEventGroupTextView.setText(calendarEvent.getGroupAbstract().getName());
            if (calendarEvent.getAllDay()) {
                holder.mEventPeriodTextView.setText("For all day");
            } else {
                if (calendarEvent.getPeriodTo() == null) {
                    holder.mEventPeriodTextView.setText(calendarEvent.getPeriodFrom());
                } else {
                    holder.mEventPeriodTextView.setText(calendarEvent.getPeriodFrom() + " to " + calendarEvent.getPeriodTo());
                }
            }

            mAttachmentPathList = calendarEvent.getAttachments();
            if (!mAttachmentPathList.isEmpty()) {
                holder.mEventAttachmentTextView.setVisibility(View.VISIBLE);
                holder.mResourceRecyclerView.setVisibility(View.VISIBLE);
                holder.mEventAttachmentTextView.setText(getString(R.string.attachments) + " " + String.valueOf(mAttachmentPathList.size()));
                holder.mResourceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(getBaseContext(), mAttachmentPathList);
                holder.mResourceRecyclerView.setAdapter(mResourceGridAdapter);
            } else {
                holder.mEventAttachmentTextView.setVisibility(View.GONE);
                holder.mResourceRecyclerView.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {
            return mCalendarEvents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mAdapterView;
            private TextView mEventTitleTextView, mEventPeriodTextView, mEventGroupTextView,
                    mEventDescriptionTextView, mEventAttachmentTextView, mEventLocationTextView, mViewMoreLessTextView;
            private RecyclerView mResourceRecyclerView;

            public ViewHolder(View itemView) {
                super(itemView);
                mAdapterView = itemView;

                mEventTitleTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_title);
                mEventPeriodTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_periods);
                mEventGroupTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_groups);
                mEventLocationTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_location);
                mEventDescriptionTextView = (TextView) mAdapterView.findViewById(R.id.textview_event_description);
                mEventAttachmentTextView = (TextView) mAdapterView.findViewById(R.id.textView_attachments);
                mViewMoreLessTextView = (TextView) mAdapterView.findViewById(R.id.textViewMoreLess);
                mResourceRecyclerView = (RecyclerView) mAdapterView.findViewById(R.id.recyclerViewResource);
            }
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

        private List<CalendarEvent> mCalendarEvents = new ArrayList<>();

        public EventAdapter(List<CalendarEvent> calendarEvents) {
            this.mCalendarEvents = calendarEvents;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutCalendarAnnouncementItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_calendar_announcement_item, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final CalendarEvent calendarEvent = mCalendarEvents.get(position);
            holder.mBinding.textviewEventTitle.setText(calendarEvent.getEventTitle());
            setEventDate(calendarEvent, holder.mBinding);
            setEventLocation(calendarEvent, holder.mBinding);
            setEventTiming(calendarEvent, holder.mBinding);
            setEventGroup(calendarEvent, holder.mBinding);
            setEventAttachments(calendarEvent, holder.mBinding);
            setFontAwesomeColor(intEventColor, holder.mBinding);
            TextViewMore.viewMore(calendarEvent.getDescription(), holder.mBinding.textViewDescription, holder.mBinding.includeTextViewMoreLess.textViewMoreLess);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = calendarEvent.getStartDate() + "---" + calendarEvent.getEndDate();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCalendarEvents.size();
        }

        public void clear() {
            mCalendarEvents.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCalendarAnnouncementItemBinding mBinding;

            public ViewHolder(LayoutCalendarAnnouncementItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private void setFontAwesomeColor(int intEventColor, LayoutCalendarAnnouncementItemBinding binding) {
        binding.fontAwesomeCalendar.setTextColor(intEventColor);
        binding.fontAwesomeTiming.setTextColor(intEventColor);
        binding.fontAwesomeGroups.setTextColor(intEventColor);
        binding.fontAwesomeLocation.setTextColor(intEventColor);
    }

    private void setEventAttachments(CalendarEvent calendarEvent, LayoutCalendarAnnouncementItemBinding binding) {
        if (calendarEvent.getAttachments() != null && !calendarEvent.getAttachments().isEmpty()) {

            binding.recyclerViewResource.setVisibility(View.VISIBLE);
            //holder.mEventAttachmentTextView.setText(getString(R.string.attachments) + " " + String.valueOf(mAttachmentPathList.size()));
            binding.recyclerViewResource.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
            ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(getBaseContext(), calendarEvent.getAttachments());
            binding.recyclerViewResource.setAdapter(mResourceGridAdapter);
        } else {
            // holder.mEventAttachmentTextView.setVisibility(View.GONE);
            binding.recyclerViewResource.setVisibility(View.GONE);
        }
    }

    private void setEventGroup(CalendarEvent calendarEvent, LayoutCalendarAnnouncementItemBinding binding) {
        if (calendarEvent.getGroupAbstract() != null && !TextUtils.isEmpty(calendarEvent.getGroupAbstract().getName())) {
            binding.textviewEventGroups.setText(calendarEvent.getGroupAbstract().getName());
        } else {
            binding.layoutGroups.setVisibility(View.GONE);
        }
    }

    private void setEventTiming(CalendarEvent calendarEvent, LayoutCalendarAnnouncementItemBinding binding) {

        if (mEventType.equals(EventType.TYPE_ACTIVITY.getEventType())) {
            if (calendarEvent.getAllDay()) {
                binding.textViewEventTiming.setText("For all day");
            } else if (!TextUtils.isEmpty(calendarEvent.getPeriodFrom()) && !TextUtils.isEmpty(calendarEvent.getPeriodTo())) {
                binding.textViewEventTiming.setText(calendarEvent.getPeriodFrom() + " to " + calendarEvent.getPeriodTo() + " period");

            } else {
                binding.layoutTime.setVisibility(View.GONE);
            }
        } else {
            binding.layoutTime.setVisibility(View.GONE);

        }
    }

    private void setEventLocation(CalendarEvent calendarEvent, LayoutCalendarAnnouncementItemBinding binding) {
        if (calendarEvent.getLocation() != null && !TextUtils.isEmpty(calendarEvent.getLocation().getCity())) {
            binding.textviewEventLocation.setText(calendarEvent.getLocation().getCity());
        } else {
            binding.layoutLocation.setVisibility(View.GONE);
        }
    }

    private void setEventDate(CalendarEvent calendarEvent, LayoutCalendarAnnouncementItemBinding binding) {
        String strStartDate = "";
        String strEndDate = "";

        if (!TextUtils.isEmpty(calendarEvent.getStartDate())) {
            strStartDate = DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(calendarEvent.getStartDate()));
        }
        if (!TextUtils.isEmpty(calendarEvent.getEndDate())) {
            strEndDate = DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(calendarEvent.getEndDate()));

        }

        if (mEventType.equals(EventType.TYPE_PERSONAL.getEventType())) {
            String startTime = TextUtils.isEmpty(calendarEvent.getStartTime()) ? "" : " , " + calendarEvent.getStartTime();
            String endTime = TextUtils.isEmpty(calendarEvent.getEndTime()) ? "" : " - " + calendarEvent.getEndTime();

            if (strStartDate.equals(strEndDate)) {
                binding.textViewEventDate.setText(strStartDate + startTime + endTime);

            } else {
                binding.textViewEventDate.setText(strStartDate + startTime + "\n" + strEndDate + (TextUtils.isEmpty(calendarEvent.getEndTime()) ? "" : " , " + calendarEvent.getEndTime()));
            }
        } else if (mEventType.equals(EventType.TYPE_ACTIVITY.getEventType())) {
            binding.textViewEventDate.setText(strStartDate);
        } else {
            if (strStartDate.equals(strEndDate)) {
                binding.textViewEventDate.setText(strStartDate);

            } else {
                binding.textViewEventDate.setText(strStartDate + " - " + strEndDate);

            }

        }
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_attachments_item_small, parent, false);
            ViewHolder mViewHolder = new ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            if (!TextUtils.isEmpty(mPathArrayList.get(position).getDeviceURL())) {
                filePath = mPathArrayList.get(position).getDeviceURL();
            } else if (!TextUtils.isEmpty(mPathArrayList.get(position).getUrlMain())) {
                filePath = mPathArrayList.get(position).getUrlMain();
            } else if (!TextUtils.isEmpty(mPathArrayList.get(position).getSourceURL())) {
                filePath = mPathArrayList.get(position).getSourceURL();
            }
            String fileType = URLConnection.guessContentTypeFromName(filePath);

            if (fileType.contains("image")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_white);
                Picasso.with(mContext).load(filePath).resize(300, 300).centerCrop().into(holder.mResourceImageView);
            } else if (fileType.contains("video")) {
                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 300, 300, false);
                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "any_Title", null);
                Picasso.with(mContext).load(path).resize(300, 300).centerInside().into(holder.mResourceImageView);
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

            holder.mResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(filePath);
                    if (mimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(CalendarEventListActivity.this, position, true, true, mPathArrayList);
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
            ImageView mResourceImageView, mResourceTypeImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_attach);
                mResourceTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
            }


        }


    }
}