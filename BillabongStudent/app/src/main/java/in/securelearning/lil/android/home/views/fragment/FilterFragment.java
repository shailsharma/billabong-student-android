package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this activity must implement the
 * {@link OnFilterFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this activity.
 */

public class FilterFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the activity initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FILTER = "filter";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private FilterList mFilter;
    private String mParam2;

    private OnFilterFragmentInteractionListener mListener;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @param filterList Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of activity FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(FilterList filterList, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILTER, filterList);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onDoneButtonPressed(FilterList filter) {
        if (mListener != null) {
            mListener.onFilterFragmentInteraction(filter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterFragmentInteractionListener) {
            mListener = (OnFilterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilter = (FilterList) getArguments().getSerializable(ARG_FILTER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setStyle(STYLE_NORMAL,R.style.ThemeFilterFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this activity
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        View done = view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButtonPressed(mFilter);
                dismiss();
            }
        });
        View close = view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView filterTitle = (TextView) view.findViewById(R.id.filter_title);
        filterTitle.setText(mFilter.getTitle());

        View filterContainer = view.findViewById(R.id.filter);
        int sectionLayoutId;
        int sectionItemLayoutId;
        for (FilterList.FilterSection section :
                mFilter.getSections()) {
            if (section.getSectionType() == FilterList.SECTION_SELECTION_TYPE_CHECKBOX) {
                sectionLayoutId = R.layout.filter_section_checkbox;
                sectionItemLayoutId = R.layout.filter_section_item_checkbox;
            } else {
                sectionLayoutId = R.layout.filter_section_radio;
                sectionItemLayoutId = R.layout.filter_section_item_radio;
            }
            View sectionView = inflater.inflate(sectionLayoutId, (ViewGroup) filterContainer, false);
            TextView title = (TextView) sectionView.findViewById(R.id.section_title);
            title.setText(section.getSectionTitle());
            ((ViewGroup) filterContainer).addView(sectionView);

            View itemContainer = sectionView.findViewById(R.id.section);
            for (final FilterList.FilterSectionItem item :
                    section.getItems()) {
                final CompoundButton sectionItemView = (CompoundButton) inflater.inflate(sectionItemLayoutId, (ViewGroup) itemContainer, false);
                sectionItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setSelected(isChecked);
                    }
                });
                // should be before doing set checked
                // so that the features of icon_radio_button_multiple group kick in
                ((ViewGroup) itemContainer).addView(sectionItemView);
                sectionItemView.setText(item.getName());
                sectionItemView.setChecked(item.isSelected());

            }
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFilterFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFilterFragmentInteraction(FilterList filterList);
    }
}
