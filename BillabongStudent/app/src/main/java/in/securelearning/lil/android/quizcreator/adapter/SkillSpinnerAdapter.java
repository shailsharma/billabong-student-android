package in.securelearning.lil.android.quizcreator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.app.R;

/**
 * Created by Cp on 7/25/2016.
 */
public class SkillSpinnerAdapter extends BaseAdapter {

    ArrayList<Skill> mSkillArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public SkillSpinnerAdapter(Context context, ArrayList<Skill> skills) {
        mContext = context;
        mSkillArrayList = skills;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return mSkillArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return mSkillArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.layout_skill_spinner_item, viewGroup, false);
        TextView mSpinnerTextView = (TextView) view.findViewById(R.id.textview_skill_name);
        mSpinnerTextView.setText(mSkillArrayList.get(i).getSkillName());
        return view;
    }
}
