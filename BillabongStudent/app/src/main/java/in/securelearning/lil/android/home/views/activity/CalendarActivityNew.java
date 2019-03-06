package in.securelearning.lil.android.home.views.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCalendarBinding;
import in.securelearning.lil.android.app.databinding.LayoutCalendarEventCreationBinding;
import in.securelearning.lil.android.app.databinding.LayoutCalendarViewpagerItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutCalendarWeekRecyclerviewItemNewBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignmentStudentActivity;
import in.securelearning.lil.android.assignments.views.activity.AssignmentTeacherActivity;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.dataobjects.CalendarDay;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.CalendarDayCounts;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.home.events.LoadCalendarEventDownloaded;
import in.securelearning.lil.android.home.events.LoadNewEventCreated;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CalendarActivityNew extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    HomeModel mHomeModel;

    @Inject
    RxBus mRxBus;

    private Disposable mSubscription;
    LayoutCalendarBinding mBinding;
    private Calendar mCalendar = Calendar.getInstance();
    private boolean isSelfCreated = false;
    private ArrayList<String> mDaysArrayList = new ArrayList<>();

    @Override
    public void onBackPressed() {

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorCalendarPrimary));
        initializeViewPager();
        initializeViews();
        listenRxBusEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        MenuItem create = menu.findItem(R.id.action_create_event);
        if (PermissionPrefsCommon.getCalendarActivityCreatePermission(this) ||
                PermissionPrefsCommon.getCalendarAnnouncementCreatePermission(this) ||
                PermissionPrefsCommon.getCalendarPersonalCreatePermission(this)) {
            create.setVisible(true);
        } else {
            create.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_go_to_date:
                showDatePickerDialog();
                return true;

            case R.id.action_create_event:
                showEventCreationDialog();
                return true;

            case R.id.action_calendar_help:
                showHelpPopup(mBinding.toolbarCalendar);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CalendarActivityNew.class);
        return intent;
    }

    private void showEventCreationDialog() {

        final Dialog dialog = new Dialog(CalendarActivityNew.this);
        final LayoutCalendarEventCreationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_calendar_event_creation, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));

        if (PermissionPrefsCommon.getCalendarActivityCreatePermission(this)) {
            AnimationUtils.zoomInFast(getBaseContext(), binding.layoutCreateActivity);
        } else {
            binding.layoutCreateActivity.setVisibility(View.GONE);
        }

        if (PermissionPrefsCommon.getCalendarAnnouncementCreatePermission(this)) {
            AnimationUtils.zoomInFast(getBaseContext(), binding.layoutCreateAnnouncement);
        } else {
            binding.layoutCreateAnnouncement.setVisibility(View.GONE);
        }

        if (PermissionPrefsCommon.getCalendarPersonalCreatePermission(this)) {
            AnimationUtils.zoomInFast(getBaseContext(), binding.layoutCreatePersonalEvent);
        } else {
            binding.layoutCreatePersonalEvent.setVisibility(View.GONE);
        }

        binding.layoutCreatePersonalEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getBaseContext(), PersonalEventCreationActivity.class);
                startActivity(mIntent);
                dialog.dismiss();
            }
        });

        binding.layoutCreateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getBaseContext(), ActivityCreationActivity.class);
                startActivity(mIntent);
                dialog.dismiss();
            }
        });

        binding.layoutCreateAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getBaseContext(), AnnouncementCreationActivity.class);
                startActivity(mIntent);
                dialog.dismiss();

            }
        });

        binding.layoutCreation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                dialog.dismiss();
                return false;
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof LoadNewEventCreated) {
                    isSelfCreated = true;
                    if (mCalendar == null) {
                        mCalendar = Calendar.getInstance();
                    }
                    mCalendar.setTime(((LoadNewEventCreated) event).getStartDate());
                    initializeViewPager();
                } else if (!isSelfCreated && event instanceof LoadCalendarEventDownloaded) {
                    String startDate = ((LoadCalendarEventDownloaded) event).getCalendarEvent().getStartDate();
                    if (!TextUtils.isEmpty(startDate)) {
                        if (mCalendar == null) {
                            mCalendar = Calendar.getInstance();
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(DateUtils.convertrIsoDate(startDate));
                        int position = TimeUtils.getPositionForWeek(calendar);
                        if (position < mBinding.viewPagerCalendar.getCurrentItem() + 2 && position > mBinding.viewPagerCalendar.getCurrentItem() - 2) {
                            initializeViewPager();

                        }
                    }

                }

            }
        });
    }

    private void initializeViews() {

        setSupportActionBar(mBinding.toolbarCalendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.toolbarCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                mBinding.viewPagerCalendar.setCurrentItem(TimeUtils.getPositionForWeek(mCalendar), true);
            }
        });

        mBinding.viewPagerCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTitle(mBinding.viewPagerCalendar.getAdapter().getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * initialize viewpager for week - set adapter, set current week and set limit
     */
    private void initializeViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getBaseContext());
        mBinding.viewPagerCalendar.setAdapter(viewPagerAdapter);
        mBinding.viewPagerCalendar.setCurrentItem(TimeUtils.getPositionForWeek(mCalendar));
        setTitle(mBinding.viewPagerCalendar.getAdapter().getPageTitle(mBinding.viewPagerCalendar.getCurrentItem()));
    }

    /**
     * show help popup containing calendar indicators
     *
     * @param view
     */
    private void showHelpPopup(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_calendar_help_popup, null);

        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = layout.getMeasuredWidth();

        PopupWindow popupHelp = new PopupWindow(this);
        popupHelp.setContentView(layout);
        popupHelp.setWidth(popupWidth);
        popupHelp.setHeight(popupHeight);
        popupHelp.setFocusable(true);
        popupHelp.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupHelp.setOutsideTouchable(true);
        popupHelp.setAnimationStyle(android.R.style.Animation_Dialog);
        popupHelp.showAsDropDown(view, 0, -40, Gravity.RIGHT);

    }

    /**
     * show the date picker dialog
     */
    private void showDatePickerDialog() {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (mCalendar == null) {
                    mCalendar = Calendar.getInstance();
                }
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                int position = TimeUtils.getPositionForWeek(mCalendar);
                mBinding.viewPagerCalendar.setCurrentItem(position, true);

            }
        }, year, month, day);

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        datePickerDialog.setCustomTitle(linearLayout);
        datePickerDialog.show();
    }

    class ViewPagerAdapter extends PagerAdapter {
        private Context mContext;

        public ViewPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return TimeUtils.WEEKS_OF_TIME;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String calendarYear = String.valueOf(TimeUtils.getFirstDayOfWeekForPosition(position).get(Calendar.YEAR));
            String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            if (calendarYear.equals(currentYear)) {
                SimpleDateFormat pageTitleFormat = new SimpleDateFormat("MMMM");
                return pageTitleFormat.format(TimeUtils.getFirstDayOfWeekForPosition(position).getTime());

            } else {
                SimpleDateFormat pageTitleFormat = new SimpleDateFormat("MMM yyyy");
                return pageTitleFormat.format(TimeUtils.getFirstDayOfWeekForPosition(position).getTime());
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutCalendarViewpagerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.layout_calendar_viewpager_item, container, false);
            binding.recyclerViewDays.setLayoutManager(new GridLayoutManager(CalendarActivityNew.this, 7, GridLayoutManager.HORIZONTAL, false));
            final DateRecyclerAdapter dateRecyclerAdapter = new DateRecyclerAdapter();
            binding.recyclerViewDays.setAdapter(dateRecyclerAdapter);
            binding.recyclerViewDays.setHasFixedSize(true);

            Observable.create(new ObservableOnSubscribe<CalendarDayCounts>() {
                @Override
                public void subscribe(ObservableEmitter<CalendarDayCounts> e) throws Exception {

                    for (Calendar calendar : TimeUtils.getDaysOfWeekForPosition(position)) {
                        CalendarDayCounts calendarDayCounts = mHomeModel.getCalendarDayCounts(calendar, PermissionPrefsCommon.getAssignmentCreatePermission(CalendarActivityNew.this));
                        if (calendarDayCounts != null) {
                            e.onNext(calendarDayCounts);
                        }
                    }

                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CalendarDayCounts>() {
                @Override
                public void accept(CalendarDayCounts counts) throws Exception {

                    dateRecyclerAdapter.addItem(counts);
                }
            });

            (container).addView(binding.getRoot());
            return binding.getRoot();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            (container).removeView((View) object);

        }
    }

    class DateRecyclerAdapter extends RecyclerView.Adapter<DateRecyclerAdapter.ViewHolder> {

        int mLastSelectedPosition = -1;

        ArrayList<CalendarDayCounts> mDaysList = new ArrayList();

        @Override
        public DateRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutCalendarWeekRecyclerviewItemNewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_calendar_week_recyclerview_item_new, parent, false);
            binding.getRoot().setClickable(true);
            binding.getRoot().setFocusable(false);
            binding.getRoot().setFocusableInTouchMode(false);
            return new DateRecyclerAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            setSeparatorVisibilty(position, holder.mBinding.viewSeparator);
            holder.mItem = mDaysList.get(position);
            final CalendarDay calendarDay = holder.mItem.getCalendarDay();
            final long startSecond = DateUtils.getSecondsForMorningFromDate(calendarDay.getDate());
            final long endSecond = DateUtils.getSecondsForMidnightFromDate(calendarDay.getDate());
            final String startDate = DateUtils.getISO8601DateStringFromSeconds(startSecond);
            final String endDate = DateUtils.getISO8601DateStringFromSeconds(endSecond);
            final String titleDate = calendarDay.getDateFormatted();

            if (mCalendar == null) {
                mCalendar = Calendar.getInstance();
            }
            if (DateUtils.compareTwoDate(calendarDay.getDate(), mCalendar.getTime()) == 0) {
                mLastSelectedPosition = position;
                holder.mBinding.getRoot().setSelected(true);

            } else {
                holder.mBinding.getRoot().setSelected(false);
            }
            String dayOfMonth = calendarDay.getDayOfMonth();
            String textViewDateSuffix = "";
            int date = Integer.parseInt(dayOfMonth);
            int modVal = date % 10;

            if (date >= 4 && date < 20) {
                textViewDateSuffix = "th";

            } else if (modVal == 1) {
                textViewDateSuffix = "st";

            } else if (modVal == 2) {
                textViewDateSuffix = "nd";

            } else if (modVal == 3) {
                textViewDateSuffix = "rd";

            } else {
                textViewDateSuffix = "th";

            }
            String textviewDate = String.valueOf(dayOfMonth);
            String textviewMonth = String.valueOf(calendarDay.getMonthOfYear());
            String textviewDayOfWeek = calendarDay.getDayOfWeek().substring(0, 3);
            holder.mBinding.textviewDateMonth.setText(textviewDayOfWeek + ", " + textviewDate + textViewDateSuffix);

            holder.mBinding.buttonCalendarAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PermissionPrefsCommon.getAssignmentCreatePermission(CalendarActivityNew.this)) {
                        startActivity(AssignmentTeacherActivity.getStartIntentForCalendar(getBaseContext(), startDate));
                    } else {
                        startActivity(AssignmentStudentActivity.getStartIntentForCalendar(CalendarActivityNew.this, startDate));
                    }
                }
            });

            holder.mBinding.buttonCalendarPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_PERSONAL.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorPersonal),
                            startDate, endDate, titleDate));
                }
            });
            holder.mBinding.buttonCalendarActivities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_ACTIVITY.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorActivities),
                            startDate, endDate, titleDate));

                }
            });
            holder.mBinding.buttonCalendarAnnouncement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_ANNOUNCEMENT.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement),
                            startDate, endDate, titleDate));

                }
            });
            holder.mBinding.buttonCalendarHolidayEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_HOLIDAY.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorHolidayEvents),
                            startDate, endDate, titleDate));

                }
            });
            holder.mBinding.buttonCalendarVacationEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_VACATION.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorVacationEvents),
                            startDate, endDate, titleDate));

                }
            });
            holder.mBinding.buttonCalendarExamEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_EXAM.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorExamEvents),
                            startDate, endDate, titleDate));

                }
            });
            holder.mBinding.buttonCalendarCelebrationEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(CalendarEventListActivity.startCalendarEventActivity(getBaseContext(),
                            EventType.TYPE_CELEBRATION.getEventType(),
                            ContextCompat.getColor(getBaseContext(), R.color.colorCelebrationEvents),
                            startDate, endDate, titleDate));

                }
            });

            holder.mBinding.buttonCalendarPeriods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(CalendarPeriodListActivity.startCalendarPeriodActivity(getBaseContext(), startSecond, endSecond, titleDate));
                }
            });

            holder.mBinding.buttonCalendarTrainingSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(TrainingSessionsActivity.getStartIntent(getBaseContext(), startDate, endDate, titleDate));
                }
            });

            performViewCalculation(holder);

        }

        private void setSeparatorVisibilty(int position, View view) {
            if (position == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);

            }
        }


        @Override
        public int getItemCount() {
            return mDaysList.size();
        }

        private void performViewCalculation(DateRecyclerAdapter.ViewHolder holder) {

            if (holder.mItem.getAssignmentCounts() > 0) {
                holder.mBinding.buttonCalendarAssignment.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarAssignment.setText(String.valueOf(holder.mItem.getAssignmentCounts()));
            } else {
                holder.mBinding.buttonCalendarAssignment.setVisibility(View.GONE);
            }

            if (holder.mItem.getActivitiesCounts() > 0) {
                holder.mBinding.buttonCalendarActivities.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarActivities.setText(String.valueOf(holder.mItem.getActivitiesCounts()));
            } else {
                holder.mBinding.buttonCalendarActivities.setVisibility(View.GONE);
            }

            if (holder.mItem.getAnnouncementsCounts() > 0) {
                holder.mBinding.buttonCalendarAnnouncement.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarAnnouncement.setText(String.valueOf(holder.mItem.getAnnouncementsCounts()));
            } else {
                holder.mBinding.buttonCalendarAnnouncement.setVisibility(View.GONE);
            }

            if (holder.mItem.getPersonalCounts() > 0) {
                holder.mBinding.buttonCalendarPersonal.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarPersonal.setText(String.valueOf(holder.mItem.getPersonalCounts()));
            } else {
                holder.mBinding.buttonCalendarPersonal.setVisibility(View.GONE);
            }

            if (holder.mItem.getPeriodCounts() > 0) {
                holder.mBinding.buttonCalendarPeriods.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarPeriods.setText(String.valueOf(holder.mItem.getPeriodCounts()));
            } else {
                holder.mBinding.buttonCalendarPeriods.setVisibility(View.GONE);
            }
            if (holder.mItem.getExamEventCounts() > 0) {
                holder.mBinding.buttonCalendarExamEvents.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarExamEvents.setText(String.valueOf(holder.mItem.getExamEventCounts()));
            } else {
                holder.mBinding.buttonCalendarExamEvents.setVisibility(View.GONE);
            }

            int vacationEvents = holder.mItem.getVacationEventCounts();

            if (vacationEvents > 0) {
                holder.mBinding.buttonCalendarVacationEvents.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarVacationEvents.setText(String.valueOf(vacationEvents));
            } else {
                holder.mBinding.buttonCalendarVacationEvents.setVisibility(View.GONE);
            }

            int holidayEvents = holder.mItem.getHolidayEventCounts();

            if (holidayEvents > 0) {
                holder.mBinding.buttonCalendarHolidayEvents.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarHolidayEvents.setText(String.valueOf(holidayEvents));
            } else {
                holder.mBinding.buttonCalendarHolidayEvents.setVisibility(View.GONE);
            }

            int celebrationEvents = holder.mItem.getCelebrationEventCounts();

            if (celebrationEvents > 0) {
                holder.mBinding.buttonCalendarCelebrationEvents.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarCelebrationEvents.setText(String.valueOf(celebrationEvents));
            } else {
                holder.mBinding.buttonCalendarCelebrationEvents.setVisibility(View.GONE);
            }

            int trainingSessions = holder.mItem.getTrainingSessionCounts();

            if (trainingSessions > 0) {
                holder.mBinding.buttonCalendarTrainingSession.setVisibility(View.VISIBLE);
                holder.mBinding.buttonCalendarTrainingSession.setText(String.valueOf(trainingSessions));
            } else {
                holder.mBinding.buttonCalendarTrainingSession.setVisibility(View.GONE);
            }

        }

        public void addItem(CalendarDayCounts counts) {
            if (mDaysList != null) {
                mDaysList.add(counts);
                notifyDataSetChanged();
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCalendarWeekRecyclerviewItemNewBinding mBinding;
            private CalendarDayCounts mItem;

            public ViewHolder(LayoutCalendarWeekRecyclerviewItemNewBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }
        }

        private String getSelectedDate(DateRecyclerAdapter.ViewHolder holder) {
            String strSelectedDate = holder.mItem.getCalendarDay().getDateFormatted();
            strSelectedDate = strSelectedDate.replace("\n", ", ");
            String strOnlyDate = strSelectedDate.substring(5, strSelectedDate.length() - 9);
            if (strOnlyDate.equals("1") || strOnlyDate.equals("01")) {
                strSelectedDate = new StringBuilder(strSelectedDate).insert(strSelectedDate.length() - 9, "st").toString();
            } else if (strOnlyDate.equals("2") || strOnlyDate.equals("02")) {
                strSelectedDate = new StringBuilder(strSelectedDate).insert(strSelectedDate.length() - 9, "nd").toString();
            } else if (strOnlyDate.equals("3") || strOnlyDate.equals("03")) {
                strSelectedDate = new StringBuilder(strSelectedDate).insert(strSelectedDate.length() - 9, "rd").toString();
            } else {
                strSelectedDate = new StringBuilder(strSelectedDate).insert(strSelectedDate.length() - 9, "th").toString();
            }
            return strSelectedDate;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();

        }
    }
}
