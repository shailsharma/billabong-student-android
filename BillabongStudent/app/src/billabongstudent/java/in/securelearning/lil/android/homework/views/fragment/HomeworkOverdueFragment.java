package in.securelearning.lil.android.homework.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.views.adapter.HomeworkAssignedAdapter;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;


public class HomeworkOverdueFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mName;
    private List<Homework> mAssignmentOverdueList;
    LayoutHomeworkRecyclerViewBinding mBinding;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    public HomeworkOverdueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeworkOverdueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeworkOverdueFragment newInstance(String param1, List<Homework> param2) {
        HomeworkOverdueFragment fragment = new HomeworkOverdueFragment();
        Bundle args = new Bundle();
        args.putString(ConstantUtil.TITLE, param1);
        args.putSerializable(ConstantUtil.OVERDUE, (Serializable) param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ConstantUtil.TITLE);
            mAssignmentOverdueList = (List<Homework>) getArguments().getSerializable(ConstantUtil.OVERDUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_homework_recycler_view, container, false);
        mRecyclerView = mBinding.list;
        setAdapter();


        return mBinding.getRoot();
    }

    private void setAdapter() {
        if (mAssignmentOverdueList != null && !mAssignmentOverdueList.isEmpty()) {
            for (Homework overDueList : mAssignmentOverdueList) {
                overDueList.setHomeworkType(ConstantUtil.OVERDUE);
            }
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            HomeworkAssignedAdapter adapter = new HomeworkAssignedAdapter(mAssignmentOverdueList);
            mRecyclerView.setAdapter(adapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
