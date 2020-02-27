package in.securelearning.lil.android.lrpa.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutChapterListChildItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutChapterListHeaderItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutChaptersListFragmentBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.lrpa.events.FetchSubjectDetailEvent;
import in.securelearning.lil.android.lrpa.model.LRPAModel;
import in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapter;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_YET_TO_START;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_YET_TO_START;

public class ChaptersFragment extends Fragment {

    private final static String SUBJECT_ID = "subjectId";

    @Inject
    public RxBus mRxBus;

    @Inject
    LRPAModel mLRPAModel;

    LayoutChaptersListFragmentBinding mBinding;

    private Context mContext;
    private String mSubjectId;

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @return A new instance of activity ChaptersFragment.
     */
    public static ChaptersFragment newInstance(String subjectId) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putString(SUBJECT_ID, subjectId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_chapters_list_fragment, container, false);

        if (getArguments() != null) {
            mSubjectId = getArguments().getString(SUBJECT_ID);
        }
        getChapterResult(mSubjectId);

        return mBinding.getRoot();
    }

    @SuppressLint("CheckResult")
    private void getChapterResult(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            LessonPlanChapterPost lessonPlanChapterPost = new LessonPlanChapterPost();
            lessonPlanChapterPost.setType("topic");
            lessonPlanChapterPost.setSubjectId(subjectId);
            mLRPAModel.getChapterResult(lessonPlanChapterPost).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanChapterResult>() {
                        @Override
                        public void accept(LessonPlanChapterResult lessonPlanChapterResults) throws Exception {
                            if (lessonPlanChapterResults != null && lessonPlanChapterResults.getLessonPlanChapters() != null) {

                                initializeExpandableList(lessonPlanChapterResults.getCompletedChapters(), lessonPlanChapterResults.getInProgressChapters(), lessonPlanChapterResults.getYetToStartChapters());

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            throwable.printStackTrace();
                        }

                    });
        } else {
            // showInternetSnackBar();
        }

    }


    private void initializeExpandableList(ArrayList<LessonPlanChapter> lessonPlanCompleteChapterResults, ArrayList<LessonPlanChapter> lessonPlanInProgressChapterResults, ArrayList<LessonPlanChapter> lessonPlanYetToStartChapterResults) {

        final HashMap<ChapterHeaderData, List<LessonPlanChapter>> listDataChild = new HashMap<ChapterHeaderData, List<LessonPlanChapter>>();
        List<LessonPlanChapter> listComplete = new ArrayList<LessonPlanChapter>();
        List<LessonPlanChapter> listInProgress = new ArrayList<LessonPlanChapter>();
        List<LessonPlanChapter> listYetToStart = new ArrayList<LessonPlanChapter>();

        List<ChapterHeaderData> listDataHeader = new ArrayList<>();
        listDataHeader.add(new ChapterHeaderData(0, ChapterHeaderData.HEADER_IN_PROGRESS));
        listDataHeader.add(new ChapterHeaderData(1, ChapterHeaderData.HEADER_YET_TO_START));
        listDataHeader.add(new ChapterHeaderData(2, ChapterHeaderData.HEADER_COMPLETED));

        if (lessonPlanInProgressChapterResults.size() >= 1) {
            for (int i = 0; i < lessonPlanInProgressChapterResults.size(); i++) {
                listInProgress.add(lessonPlanInProgressChapterResults.get(i));
                listDataChild.put(listDataHeader.get(0), listInProgress);
            }
        }
        if (lessonPlanYetToStartChapterResults.size() >= 1) {
            for (int i = 0; i < lessonPlanYetToStartChapterResults.size(); i++) {
                listYetToStart.add(lessonPlanYetToStartChapterResults.get(i));
                listDataChild.put(listDataHeader.get(1), listYetToStart);
            }
        }

        if (lessonPlanCompleteChapterResults.size() >= 1) {
            for (int i = 0; i < lessonPlanCompleteChapterResults.size(); i++) {
                listComplete.add(lessonPlanCompleteChapterResults.get(i));
                listDataChild.put(listDataHeader.get(2), listComplete);
            }
        }

        final ExpandableListAdapter expandableAdapter = new ExpandableListAdapter(listDataHeader, listDataChild);
        mBinding.expandableListViewChapters.setAdapter(expandableAdapter);
        mBinding.expandableListViewChapters.setNestedScrollingEnabled(false);
        mBinding.expandableListViewChapters.setDividerHeight(0);
        mBinding.expandableListViewChapters.setChildDivider(null);

        mBinding.expandableListViewChapters.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                int childCount = expandableAdapter.getChildrenCount(groupPosition);
                return childCount == 0;
            }
        });

        mBinding.expandableListViewChapters.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem)
                    mBinding.expandableListViewChapters.collapseGroup(previousItem);
                previousItem = groupPosition;
            }
        });
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private List<ChapterHeaderData> mHeaderData;
        private HashMap<ChapterHeaderData, List<LessonPlanChapter>> mChildData;

        ExpandableListAdapter(List<ChapterHeaderData> listDataHeader,
                              HashMap<ChapterHeaderData, List<LessonPlanChapter>> listDataChild) {
            this.mHeaderData = listDataHeader;
            this.mChildData = listDataChild;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.mChildData.get(this.mHeaderData.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutChapterListChildItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_chapter_list_child_item, parent, false);
            final LessonPlanChapter lessonPlanChapter = mChildData.get(mHeaderData.get(groupPosition)).get(childPosition);
            convertView = binding.getRoot();

            AnimationUtils.fadeInFast(mContext, binding.getRoot());
            binding.textViewChapterTitle.setText(lessonPlanChapter.getName());

            if (groupPosition == 0) {
                if (childPosition % 2 == 1) {
                    binding.textViewChapterTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_chapter_in_progress_child));
                } else {
                    binding.textViewChapterTitle.setBackground(null);
                }
            } else if (groupPosition == 1) {
                if (childPosition % 2 == 1) {
                    binding.textViewChapterTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_chapter_yet_to_start_child));

                } else {
                    binding.textViewChapterTitle.setBackground(null);
                }
            } else if (groupPosition == 2) {
                if (childPosition % 2 == 1) {
                    binding.textViewChapterTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_chapter_completed_child));
                } else {
                    binding.textViewChapterTitle.setBackground(null);
                }
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRxBus.send(new FetchSubjectDetailEvent(lessonPlanChapter.getId(), lessonPlanChapter.getName(), getChapterStatus(mHeaderData.get(groupPosition).getHeaderTitle())));
                }
            });
            return convertView;
        }

        private String getChapterStatus(String status) {
            switch (status) {
                case HEADER_IN_PROGRESS:
                    return STATUS_IN_PROGRESS;
                case HEADER_YET_TO_START:
                    return STATUS_YET_TO_START;
                case HEADER_COMPLETED:
                    return STATUS_COMPLETED;
                default:
                    return STATUS_IN_PROGRESS;
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<LessonPlanChapter> childList = this.mChildData.get(this.mHeaderData.get(groupPosition));
            if (childList != null && !childList.isEmpty()) {
                return childList.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.mHeaderData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.mHeaderData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            LayoutChapterListHeaderItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_chapter_list_header_item, parent, false);
            convertView = binding.getRoot();

            ChapterHeaderData chapterHeaderData = mHeaderData.get(groupPosition);
            if (isExpanded) {
                binding.imageViewStateIndicator.setBackgroundResource(R.drawable.chevron_up_dark_24dp);
            } else {
                binding.imageViewStateIndicator.setBackgroundResource(R.drawable.chevron_down_dark);
            }

            if (chapterHeaderData.getHeaderTitle().equalsIgnoreCase(ChapterHeaderData.HEADER_COMPLETED)) {
                binding.layoutStatus.setBackgroundResource(R.drawable.background_chapter_completed);
            } else if (chapterHeaderData.getHeaderTitle().equalsIgnoreCase(ChapterHeaderData.HEADER_IN_PROGRESS)) {
                binding.layoutStatus.setBackgroundResource(R.drawable.background_chapter_in_progress);
            } else if (chapterHeaderData.getHeaderTitle().equalsIgnoreCase(ChapterHeaderData.HEADER_YET_TO_START)) {
                binding.layoutStatus.setBackgroundResource(R.drawable.background_chapter_yet_to_start);
            }

            binding.textViewStatus.setText(chapterHeaderData.getHeaderTitle());

            int childCount = getChildrenCount(groupPosition);
            if (childCount == 0) {
                binding.textViewChapterCount.setText(ConstantUtil.STRING_ZERO);
                binding.imageViewStateIndicator.setVisibility(View.GONE);
            } else if (childCount > 1) {
                String text = String.valueOf(getChildrenCount(groupPosition));
                binding.textViewChapterCount.setText(text);
                binding.imageViewStateIndicator.setVisibility(View.VISIBLE);
            } else {
                String text = String.valueOf(getChildrenCount(groupPosition));
                binding.textViewChapterCount.setText(text);
                binding.imageViewStateIndicator.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

}
