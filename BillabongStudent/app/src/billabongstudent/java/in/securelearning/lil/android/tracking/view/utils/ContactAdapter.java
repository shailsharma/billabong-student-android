package in.securelearning.lil.android.tracking.view.utils;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.squareup.picasso.Picasso;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.CardLayoutFinalBinding;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForTeacher;

/**
 * Created by Secure on 19-05-2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Context context;
    String routeType;

    public ContactAdapter() {
    }

    public ContactAdapter(Context context, String routeType) {
        this.context = context;
        this.routeType = routeType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CardLayoutFinalBinding binding = DataBindingUtil.inflate(LayoutInflater.
                from(viewGroup.getContext()), R.layout.card_layout_final, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        setItem(viewHolder, position);
    }

    private void setItem(final ViewHolder viewHolder, final int position) {
        viewHolder.mBinding.txtName.setText(TrackingActivityForTeacher.stList.get(position).getName());
        if (TrackingActivityForTeacher.stList.get(position).getPic().getSecureUrl() != null && !TrackingActivityForTeacher.stList.get(position).getPic().getSecureUrl().isEmpty()) {
            Picasso.with(context).load(TrackingActivityForTeacher.stList.get(position).getPic().getSecureUrl()).placeholder(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(viewHolder.mBinding.cardImage);
        } else {
            Picasso.with(context).load(R.drawable.icon_audience_large).into(viewHolder.mBinding.cardImage);
        }
        viewHolder.mBinding.chkBox.setChecked(TrackingActivityForTeacher.stList.get(position).isChecked());
        viewHolder.mBinding.chkBox.setEnabled(!TrackingActivityForTeacher.stList.get(position).isDone());
        viewHolder.mBinding.chkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = ((CheckBox) v).isChecked();

                TrackingActivityForTeacher.stList.get(position).setChecked(((CheckBox) v).isChecked());
                if (b) {
                    viewHolder.mBinding.chkBox.setBackgroundColor(0x443366aa);
                } else {
                    viewHolder.mBinding.chkBox.setBackground(null);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return TrackingActivityForTeacher.stList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardLayoutFinalBinding mBinding;


        public ViewHolder(CardLayoutFinalBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
