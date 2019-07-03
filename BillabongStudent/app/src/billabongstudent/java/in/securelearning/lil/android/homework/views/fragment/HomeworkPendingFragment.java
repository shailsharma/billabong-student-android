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
import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkRecyclerViewBinding;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.views.adapter.HomeworkAssignedAdapter;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;


public class HomeworkPendingFragment extends Fragment {
    LayoutHomeworkRecyclerViewBinding mBinding;
    RecyclerView mRecyclerView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String mName;
    private List<Homework> mAssignmentPendingList;
    private LinearLayoutManager mLinearLayoutManager;


    public HomeworkPendingFragment() {
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
    public static HomeworkPendingFragment newInstance(String param1, List<Homework> param2) {
        HomeworkPendingFragment fragment = new HomeworkPendingFragment();
        Bundle args = new Bundle();
        args.putString(ConstantUtil.TITLE, param1);
        args.putSerializable(ConstantUtil.PENDING, (Serializable) param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ConstantUtil.TITLE);
            mAssignmentPendingList = (List<Homework>) getArguments().getSerializable(ConstantUtil.PENDING);
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

        if (mAssignmentPendingList != null && !mAssignmentPendingList.isEmpty()) {
            ArrayList<Integer> selectedPosition = new ArrayList<>();
            selectedPosition=getTypePosition(selectedPosition);

            mBinding.layoutNoResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            HomeworkAssignedAdapter adapter = new HomeworkAssignedAdapter(mAssignmentPendingList,selectedPosition);
            mRecyclerView.setAdapter(adapter);

        } else {
            mRecyclerView.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Integer> getTypePosition(ArrayList<Integer> selectedPosition) {

        for (Homework homework : mAssignmentPendingList) {
            if (homework.getHomeworkType().equalsIgnoreCase(ConstantUtil.TODAY)) {
                selectedPosition.add(mAssignmentPendingList.indexOf(homework));
                break;
            }
        }
        for (Homework homework : mAssignmentPendingList) {
            if (homework.getHomeworkType().equalsIgnoreCase(ConstantUtil.NEW)) {
                selectedPosition.add(mAssignmentPendingList.indexOf(homework));
                break;
            }
        }
        for (Homework homework : mAssignmentPendingList) {
            if (homework.getHomeworkType().equalsIgnoreCase(ConstantUtil.UPCOMING)) {
                selectedPosition.add(mAssignmentPendingList.indexOf(homework));
                break;
            }
        }

       return selectedPosition;

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
