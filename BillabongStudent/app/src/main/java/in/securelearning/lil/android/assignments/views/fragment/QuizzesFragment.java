package in.securelearning.lil.android.assignments.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.events.LoadQuizListEvent;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizAssemblerActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizMetaDataActivity;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.widget.FloatingActionButton;
import in.securelearning.lil.android.base.widget.FloatingActionsMenu;
import in.securelearning.lil.android.quizcreator.events.NewQuizCreationEvent;
import in.securelearning.lil.android.quizcreator.events.QuizMinimalDeleteEvent;
import in.securelearning.lil.android.quizcreator.views.activity.QuizCreatorActivity;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class QuizzesFragment extends Fragment {

    private static FilterList filterList;
    @Inject
    public AssignmentTeacherModel mTeacherModel;
    @Inject
    public RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    private View mRootView;
    private RecyclerView mQuizRecyclerView;
    private QuizAdapter mQuizAdapter;
    private Disposable mSubscription;
    private static ArrayList<String> FILTER_BY_LIST2 = new ArrayList();
    private List<QuizMinimal> mActualQuizzes;
    private ArrayList<QuizMinimal> mFilterList;
    private String mSearchQuery;
    private LinearLayout mNoResultLayout;
    private FloatingActionsMenu mQuizMenu;
    private FloatingActionButton mInstantQuizButton, mManualQuizButton;
    private RelativeLayout mFadeLayout;
    private int mLimit = 10;
    private int mSkip = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private String mFilterBySubject = "";
    private boolean loading = true;

    public QuizzesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mRootView = inflater.inflate(R.layout.layout_quiz_recycler_view, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        setUpEventListener();
        initializeViews();
        //initializeFab();
        setDefault();
        getData(mSkip, mLimit, mFilterBySubject);
        return mRootView;
    }

    private void setDefault() {
        mFilterBySubject = "";
        initializeRecyclerView(new ArrayList<QuizMinimal>());
        mSkip = 0;
    }

    private void initializeViews() {
        mQuizRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mNoResultLayout = (LinearLayout) mRootView.findViewById(R.id.layoutNoResult);
        mQuizMenu = (FloatingActionsMenu) mRootView.findViewById(R.id.buttonActionQuiz);
        mInstantQuizButton = (FloatingActionButton) mRootView.findViewById(R.id.buttonInstantQuiz);
        mManualQuizButton = (FloatingActionButton) mRootView.findViewById(R.id.buttonManualQuiz);
        mFadeLayout = (RelativeLayout) mRootView.findViewById(R.id.layouFade);

    }

    private void getData(int skip, final int limit, String filterBySubject) {

        mTeacherModel.getQuizzesMinimalList(skip, limit, filterBySubject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<QuizMinimal>>() {
            @Override
            public void accept(ArrayList<QuizMinimal> quizzes) throws Exception {
                mSkip += quizzes.size();
                previousTotal = quizzes.size();
                noResultFound(mSkip);
                if (quizzes.size() < limit) {
                    mQuizRecyclerView.removeOnScrollListener(null);
                }

                mQuizAdapter.addItem(quizzes);

            }
        });
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mNoResultLayout.setVisibility(View.GONE);
            mQuizRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultLayout.setVisibility(View.VISIBLE);
            mQuizRecyclerView.setVisibility(View.GONE);
        }

    }

    /**
     * initialize recycler view for mValues
     * check device is tablet or phone and load recycler view according to device
     *
     * @param quizzes
     */
    private void initializeRecyclerView(final List<QuizMinimal> quizzes) {

        noResultFound(quizzes.size());
        setupFilterAttributes(quizzes);
        mActualQuizzes = quizzes;
        setAdapterAndDoPagination(quizzes);

    }

    private void setAdapterAndDoPagination(List<QuizMinimal> quizzes) {
        LinearLayoutManager layoutManager = null;
        if (getActivity() != null) {
            if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(getActivity(), 2);
                mQuizRecyclerView.setLayoutManager(layoutManager);
                mQuizAdapter = new QuizAdapter(quizzes);
                mQuizRecyclerView.setAdapter(mQuizAdapter);

            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mQuizRecyclerView.setLayoutManager(layoutManager);
                mQuizAdapter = new QuizAdapter(quizzes);
                mQuizRecyclerView.setAdapter(mQuizAdapter);

            }

        }

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mQuizRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {

                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == previousTotal - 1) {

                            getData(mSkip, mLimit, mFilterBySubject);

                        }
                    }

                }

            });
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        loading = true;
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        loading = true;
//    }

    public void filter(FilterList filterList) {
        if (mQuizAdapter != null) mQuizAdapter.applyFilter(filterList);
    }

    public static FilterList getFilter() {
        filterList = buildFilterListWithTitle("SkillMasteryFilter Quiz");
        return filterList;
    }

    private static FilterList buildFilterListWithTitle(String title) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        return builder.addSection(new FilterList.SectionBuilder()
                .addSectionItems(FILTER_BY_LIST2.toArray(new String[FILTER_BY_LIST2.size()]))
                .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                .sectionTitle("SkillMasteryFilter By")
                .build())
                .title(title)
                .build();
    }

    private void setupFilterAttributes(List<QuizMinimal> quizs) {
        Set<String> subjectSet = new LinkedHashSet<>();
        for (QuizMinimal quiz : quizs) {
            FILTER_BY_LIST2.add(quiz.getMetaInformation().getSubject().getName());
        }
        FILTER_BY_LIST2.add(0, "All");
        subjectSet = new LinkedHashSet<>(FILTER_BY_LIST2);
        FILTER_BY_LIST2 = new ArrayList<>(subjectSet);

    }

    private void closeFloatingActionMenu() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mQuizMenu != null)
                    mQuizMenu.collapse();
            }
        }, 500);

    }

    private void initializeFab() {

        mQuizMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mFadeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mFadeLayout.setVisibility(View.GONE);
            }
        });

        mFadeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mQuizMenu.isExpanded()) {
                    mQuizMenu.collapse();
                    return true;
                }

                return false;
            }
        });

        mManualQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManualQuizButton.setEnabled(false);
                Intent mIntent = new Intent(getActivity(), QuizMetaDataActivity.class);
                getActivity().startActivity(mIntent);
                mManualQuizButton.setEnabled(true);
                closeFloatingActionMenu();
            }
        });

        mInstantQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mInstantQuizButton.setEnabled(false);
                Intent mIntent = new Intent(getActivity(), QuizAssemblerActivity.class);
                getActivity().startActivity(mIntent);
                mInstantQuizButton.setEnabled(true);
                closeFloatingActionMenu();
            }
        });

    }

    private void setUpEventListener() {
        mSubscription = mRxBus.toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {

                if (event instanceof LoadQuizListEvent) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initializeRecyclerView(((LoadQuizListEvent) event).getQuizs());
                        }
                    });
                } else if (event instanceof NewQuizCreationEvent) {
                    setDefault();
                    getData(mSkip, mLimit, mFilterBySubject);
                } else if (event instanceof SearchSubmitEvent) {
                    mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                    if (mQuizAdapter != null)
                        mQuizAdapter.search(mSearchQuery);
                } else if (event instanceof SearchOpenEvent) {
                    mSearchQuery = "";

                } else if (event instanceof SearchCloseEvent) {
                    mSearchQuery = "";
                    if (mQuizAdapter != null)
                        mQuizAdapter.clearSearch();

                } else if (event instanceof QuizMinimalDeleteEvent) {
                    setDefault();
                    getData(mSkip, mLimit, mFilterBySubject);

                }

            }


        });
    }

    private void unSubscribeEvent() {
        mSubscription.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribeEvent();
    }

    public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
        List<QuizMinimal> mValues;
        List<QuizMinimal> mPermanentValues;

        public QuizAdapter(List<QuizMinimal> mValues) {
            this.mValues = mValues;
            mPermanentValues = new ArrayList<>();
            this.mPermanentValues.addAll(mValues);
        }

        public void applyFilter(FilterList filterList) {
            this.filter(filterList);
            this.sort(filterList);
        }

        public void filter(FilterList filterList) {

            setDefault();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }

            getData(mSkip, mLimit, mFilterBySubject);

        }

        public void sort(FilterList filterList) {
//            for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
//                if (filterSectionItem.isSelected()) {
//                    //sortAssignmentResponses(filterSectionItem.getName());
//                }
//            }
//            notifyDataSetChanged();
        }

        private void filterQuizzes(final List<String> filterBySubjectsList) {
            mFilterList = new ArrayList<>();
            mFilterList = PredicateListFilter.filter((ArrayList<QuizMinimal>) mActualQuizzes, new Predicate<QuizMinimal>() {
                @Override
                public boolean apply(QuizMinimal quiz) {

                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (quiz.getMetaInformation().getSubject().getName().equalsIgnoreCase(s)) {
                            mFilterList.add(quiz);
                            isMatched = true;
                            break;
                        }
                    }

                    mValues = mFilterList;
                    if (isMatched)
                        return true;
                    else return false;
                }
            });
        }

        public void addItem(ArrayList<QuizMinimal> quizzes) {
            if (mValues != null && mPermanentValues != null) {
                mPermanentValues.addAll(quizzes);
                mValues.addAll(quizzes);
                notifyDataSetChanged();
            }
        }

        @Override
        public QuizAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_quiz_itemview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final QuizAdapter.ViewHolder holder, final int position) {

            final QuizMinimal quiz = mValues.get(position);
            holder.mTitleTextView.setText(quiz.getTitle());
            holder.mSubjectTextView.setText(quiz.getMetaInformation().getSubject().getName());
            //holder.mAssignedByNameTextView.setText(quiz.getCreatedBy().getName() != null ? getString(R.string.create_by) + " " + quiz.getCreatedBy().getName() : "");
            setQuizThumbnail(quiz, holder.mQuizThumbnail);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!quiz.isAssembledQuiz()) {
                        Intent mIntent = QuizCreatorActivity.getLaunchIntentForQuizEdit(getActivity(), quiz.getObjectDocId());
                        getActivity().startActivity(mIntent);
                    } else {
                        SnackBarUtils.showColoredSnackBar(getActivity(), view, getActivity().getString(R.string.cant_edit_assembled_quiz), ContextCompat.getColor(getActivity(), R.color.colorRed));
                    }
                }
            });
            holder.mAssignQuizBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = AssignActivity.getLaunchIntentForQuiz(getActivity(), quiz.getObjectId(), quiz.getAlias());
                    startActivity(mIntent);


                }

            });


        }

        private void setQuizThumbnail(QuizMinimal quiz, ImageView quizThumbnailImageView) {

            String thumbnailPath = "";
            if (quiz.getThumbnail() != null) {
                thumbnailPath = quiz.getThumbnail().getLocalUrl();
                if (TextUtils.isEmpty(thumbnailPath)) {
                    thumbnailPath = quiz.getThumbnail().getUrl();
                }
                if (TextUtils.isEmpty(thumbnailPath)) {
                    thumbnailPath = quiz.getThumbnail().getThumb();
                }
            }

            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(quizThumbnailImageView);
                } else {
                    Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(quizThumbnailImageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(quiz.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(quizThumbnailImageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(quizThumbnailImageView);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (QuizMinimal quiz : mPermanentValues) {
                    if (quiz.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        mValues.add(quiz);
                    }
                }
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        }


        public void clearSearch() {
            if (mValues.size() != mPermanentValues.size()) {
                mValues.clear();
                mValues.addAll(mPermanentValues);
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView mTitleTextView, mAssignedByNameTextView, mSubjectTextView;
            private ImageView mQuizThumbnail;
            private Button mAssignQuizBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mQuizThumbnail = (ImageView) itemView.findViewById(R.id.imageview_assignment_thumbnail);
                mTitleTextView = (TextView) itemView.findViewById(R.id.textview_assignment_title);
                mAssignedByNameTextView = (TextView) itemView.findViewById(R.id.textview_assigned_by_name);
                mSubjectTextView = (TextView) itemView.findViewById(R.id.textview_subject);
                mAssignQuizBtn = (Button) itemView.findViewById(R.id.assign_quiz_btn);
            }
        }
    }

}
