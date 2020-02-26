package in.securelearning.lil.android.homework.views.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkRecyclerViewBinding;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.views.adapter.HomeworkAssignedAdapter;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;


public class HomeworkOverdueFragment extends Fragment {

    private List<Homework> mAssignmentOverdueList;
    LayoutHomeworkRecyclerViewBinding mBinding;


    public HomeworkOverdueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @param overdueHomeworkList Parameter 2.
     * @return A new instance of activity HomeworkOverdueFragment.
     */
    public static HomeworkOverdueFragment newInstance(List<Homework> overdueHomeworkList) {
        HomeworkOverdueFragment fragment = new HomeworkOverdueFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantUtil.OVERDUE, (Serializable) overdueHomeworkList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAssignmentOverdueList = (List<Homework>) getArguments().getSerializable(ConstantUtil.OVERDUE);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_homework_recycler_view, container, false);

        setAdapter();

        return mBinding.getRoot();
    }

    private void setAdapter() {
        if (mAssignmentOverdueList != null && !mAssignmentOverdueList.isEmpty()) {

            for (Homework overdueHomework : mAssignmentOverdueList) {
                overdueHomework.setHomeworkType(ConstantUtil.OVERDUE);
            }

            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);

            mBinding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
            HomeworkAssignedAdapter adapter = new HomeworkAssignedAdapter(mAssignmentOverdueList);
            mBinding.list.setAdapter(adapter);

        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
