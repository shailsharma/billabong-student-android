package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRevisionQuizCardItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.view.activity.RevisionPlayerActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.RevisionSubject;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

public class RevisionListAdapter extends RecyclerView.Adapter<RevisionListAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<RevisionSubject> mList;

    public RevisionListAdapter(Context context, ArrayList<RevisionSubject> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRevisionQuizCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_revision_quiz_card_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RevisionSubject revisionSubject = mList.get(position);

        setCardBackground(holder.mBinding.rootView, revisionSubject.getSubjectColorCode());
        setSubject(holder.mBinding.textViewSubject, revisionSubject.getSubjectName());
        setTopicCount(holder.mBinding.textViewTopicCount, revisionSubject.getTopicLength());

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    Log.e("revisionSubject", revisionSubject.getSubjectName() + "  id - - - " + revisionSubject.getSubjectId());
                    mContext.startActivity(RevisionPlayerActivity.getStartIntent(mContext, revisionSubject.getSubjectId()));
                } else {
                    SnackBarUtils.showNoInternetSnackBar(mContext, holder.mBinding.getRoot());
                }
            }

        });
    }

    private void setCardBackground(LinearLayoutCompat cardView, String subjectColorCode) {
        if (!TextUtils.isEmpty(subjectColorCode)) {
            cardView.setBackground(CommonUtils.getInstance().getGradientDrawableFromSingleColor(Color.parseColor(subjectColorCode)));
        } else {
            cardView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gradient_curved));
        }
    }

    private void setSubject(AppCompatTextView textView, String subject) {
        if (!TextUtils.isEmpty(subject)) {
            textView.setVisibility(View.VISIBLE);
            String subjectString = subject.trim();
            textView.setText(subjectString);
        }
    }

    private void setTopicCount(AppCompatTextView textView, String title) {

        if (!TextUtils.isEmpty(title)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutRevisionQuizCardItemBinding mBinding;

        ViewHolder(LayoutRevisionQuizCardItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}
