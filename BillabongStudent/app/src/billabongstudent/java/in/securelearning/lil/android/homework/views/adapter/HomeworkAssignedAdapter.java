package in.securelearning.lil.android.homework.views.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkListItemBinding;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.views.activity.HomeworkDetailActivity;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

/*Adapter for showing the list of student homework and onclick
will navigate through assigned homework detail
created by prakarti 22 April 2019
*/

public class HomeworkAssignedAdapter extends RecyclerView.Adapter<HomeworkAssignedAdapter.ViewHolder> {
    private List<Homework> mAssignmentList;
    List<Integer> mSelectedPosition = new ArrayList<>();


    public HomeworkAssignedAdapter(List<Homework> mAssignmentList) {
        this.mAssignmentList = mAssignmentList;

    }

    public HomeworkAssignedAdapter(List<Homework> mAssignmentList, List<Integer> selectedPosition) {
        this.mAssignmentList = mAssignmentList;
        this.mSelectedPosition = selectedPosition;
    }


    @Override
    public HomeworkAssignedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutHomeworkListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_homework_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Homework homework = mAssignmentList.get(position);
        String homeworkType = homework.getHomeworkType();
        holder.binding.textviewAssignmentTitle.setText(homework.getTitle() != null ? homework.getTitle() : ConstantUtil.BLANK);

        if (homework.getMetaInformation() != null) {
            if (homework.getMetaInformation().getTopic() != null)
                holder.binding.textViewTopic.setText(homework.getMetaInformation().getTopic().toString());
            if (homework.getMetaInformation().getSubject() != null)
                holder.binding.textviewSubject.setText(homework.getMetaInformation().getSubject().getName());
        }

        if (homework.getAssignmentTypeId() != null) {
            holder.binding.textViewAssignmentType.setVisibility(View.VISIBLE);
            holder.binding.textViewAssignmentType.setText(CommonUtils.getInstance().firstLetterCapital(homework.getAssignmentTypeId()));

        } else {
            holder.binding.textViewAssignmentType.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(homeworkType) && !homeworkType.equalsIgnoreCase(ConstantUtil.SUBMITTED)) {
            if (homework.getAssignmentDueDate() != null) {
                holder.binding.textviewDueOn.setVisibility(View.VISIBLE);
                if (homeworkType.equalsIgnoreCase(ConstantUtil.OVERDUE)) {
                    String text = "Overdue on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(homework.getAssignmentDueDate())).toUpperCase();
                    holder.binding.textviewDueOn.setText(text);
                } else {
                    String text = "Due on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(homework.getAssignmentDueDate())).toUpperCase();
                    holder.binding.textviewDueOn.setText(text);
                }

            } else {
                holder.binding.textviewDueOn.setVisibility(View.GONE);
            }
        } else if (homework.isSubmitted() && !TextUtils.isEmpty(homework.getSubmittedDate())) {
            holder.binding.textviewDueOn.setVisibility(View.GONE);
            holder.binding.textviewSubmittedDate.setVisibility(View.VISIBLE);
            holder.binding.textviewSubmittedDate.setText("Submitted on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(homework.getSubmittedDate())).toUpperCase());
        } else {
            holder.binding.textviewDueOn.setVisibility(View.GONE);
            holder.binding.textviewSubmittedDate.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(homeworkType) && homeworkType.equalsIgnoreCase(ConstantUtil.NEW) && mSelectedPosition.indexOf(position) != -1) {
            holder.binding.textviewHomeworkType.setVisibility(View.VISIBLE);
            holder.binding.textviewHomeworkType.setText(ConstantUtil.NEW);

        } else if (!TextUtils.isEmpty(homeworkType) && homeworkType.equalsIgnoreCase(ConstantUtil.TODAY) && mSelectedPosition.indexOf(position) != -1) {
            holder.binding.textviewHomeworkType.setVisibility(View.VISIBLE);
            holder.binding.textviewHomeworkType.setText(ConstantUtil.TODAY);

        } else if (!TextUtils.isEmpty(homeworkType) && homeworkType.equalsIgnoreCase(ConstantUtil.UPCOMING) && mSelectedPosition.indexOf(position) != -1) {
            holder.binding.textviewHomeworkType.setVisibility(View.VISIBLE);
            holder.binding.textviewHomeworkType.setText(ConstantUtil.UPCOMING);

        } else {
            holder.binding.textviewHomeworkType.setVisibility(View.GONE);
        }

        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(view.getContext())) {
                    view.getContext().startActivity(HomeworkDetailActivity.getStartIntent(view.getContext(), homework.getHomeworkId(), homework.getTitle()));
                } else {
                    SnackBarUtils.showNoInternetSnackBar(view.getContext(), view);
                }


            }
        });

    }


    @Override
    public int getItemCount() {
        if (mAssignmentList != null && !mAssignmentList.isEmpty())
            return mAssignmentList.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LayoutHomeworkListItemBinding binding;

        public ViewHolder(LayoutHomeworkListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

}

