package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDashboardStudentSubjectItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.helper.OnCheckBonusAvailabilityListener;
import in.securelearning.lil.android.home.views.activity.VocationalTopicsActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private ArrayList<LessonPlanSubject> mList;
    private Context mContext;
    private OnCheckBonusAvailabilityListener mBonusAvailabilityListener;

    public SubjectAdapter(Context context, ArrayList<LessonPlanSubject> list, OnCheckBonusAvailabilityListener bonusAvailabilityListener) {
        this.mContext = context;
        this.mList = list;
        this.mBonusAvailabilityListener = bonusAvailabilityListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutDashboardStudentSubjectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_dashboard_student_subject_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        final LessonPlanSubject lessonPlanSubject = mList.get(position);

        setSubjectName(lessonPlanSubject.getShortName(), lessonPlanSubject.getName(), holder.mBinding.textViewSubjectName);
        setSubjectIcon(lessonPlanSubject.getIconUrl(), holder.mBinding.imageViewSubjectIcon);
        setSubjectBonusAvailedIcon(lessonPlanSubject.isAvailedBonus(), holder.mBinding.viewBonusAvailed);

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (lessonPlanSubject.isVocationalSubject()) {

                        mContext.startActivity(VocationalTopicsActivity.getStartIntent(mContext, lessonPlanSubject.getId(), lessonPlanSubject.getName()));

                    } else {
                        /*Need to check is bonus available for this student*/
                        GamificationPrefs.saveSelectedId(mContext, lessonPlanSubject.getId());
                        mBonusAvailabilityListener.OnCheckBonusAvailability(lessonPlanSubject.getId());

                    }

                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.connect_internet));
                }
            }
        });
    }

    private void setSubjectIcon(String imageUrl, AppCompatImageView imageView) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(imageView);
        } else {
            Picasso.with(mContext).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(imageView);
        }
    }

    private void setSubjectName(String shortName, String name, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(shortName)) {
            textView.setText(shortName);
        } else if (!TextUtils.isEmpty(name)) {
            textView.setText(name);
        }
    }

    private void setSubjectBonusAvailedIcon(boolean bonusAvailed, View view) {

        if (bonusAvailed) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {

        return mList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutDashboardStudentSubjectItemBinding mBinding;

        ViewHolder(LayoutDashboardStudentSubjectItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}