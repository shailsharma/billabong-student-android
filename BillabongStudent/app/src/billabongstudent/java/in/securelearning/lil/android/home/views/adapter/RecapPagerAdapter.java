package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutLessonPlanCardItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.helper.OnCheckUserRecapOpenListener;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

public class RecapPagerAdapter extends RecyclerView.Adapter<RecapPagerAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<LessonPlanMinimal> mList;
    private OnCheckUserRecapOpenListener mRecapOpenListener;

    public RecapPagerAdapter(Context context, OnCheckUserRecapOpenListener onCheckUserRecapOpenListener, ArrayList<LessonPlanMinimal> list) {
        this.mContext = context;
        this.mRecapOpenListener = onCheckUserRecapOpenListener;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutLessonPlanCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_lesson_plan_card_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final LessonPlanMinimal lessonPlan = mList.get(position);
        setCardBackground(holder.mBinding.rootView, lessonPlan.getSubjectColorCode());
        setTitle(holder.mBinding.textViewTitle, lessonPlan.getTitle());
        setSubject(holder.mBinding.textViewSubject, lessonPlan.getSubject());
        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    mRecapOpenListener.OnCheckUserStatusListener();
                    mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, lessonPlan.getCourseId()));
                } else {
                    SnackBarUtils.showNoInternetSnackBar(mContext, holder.mBinding.getRoot());
                }
            }

        });
    }

    private void setCardBackground(LinearLayout cardView, String subjectColorCode) {
        if (!TextUtils.isEmpty(subjectColorCode)) {
            cardView.setBackground(CommonUtils.getInstance().getGradientDrawableFromSingleColor(Color.parseColor(subjectColorCode)));
        } else {
            cardView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gradient_curved));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void setTitle(AppCompatTextView textView, String title) {
        if (!TextUtils.isEmpty(title)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(Html.fromHtml(title));
        } else {
            textView.setVisibility(View.GONE);
        }

    }

    private void setDescription(AppCompatTextView textView, String description) {
        if (!TextUtils.isEmpty(description)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(Html.fromHtml(description));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setGrade(AppCompatTextView textView, String grade, String section) {
        if (!TextUtils.isEmpty(grade) && !TextUtils.isEmpty(section)) {
            textView.setVisibility(View.VISIBLE);
            String gradeString = grade.toUpperCase().trim();
            String value = gradeString + "-" + section;
            textView.setText(value);
        } else if (!TextUtils.isEmpty(grade)) {
            textView.setVisibility(View.VISIBLE);
            String gradeString = grade.toUpperCase().trim();
            textView.setText(gradeString);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void setSubject(AppCompatTextView textView, String subject) {
        if (!TextUtils.isEmpty(subject)) {
            textView.setVisibility(View.VISIBLE);
            String subjectString = subject.trim();
            textView.setText(subjectString);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutLessonPlanCardItemBinding mBinding;

        ViewHolder(LayoutLessonPlanCardItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}
