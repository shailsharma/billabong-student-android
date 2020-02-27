package in.securelearning.lil.android.startup.views.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutIntroItemBinding;

public class IntroPagerAdapter extends PagerAdapter {
    private int[] mDrawable;
    private Context mContext;

    public IntroPagerAdapter(Context context, int[] drawable) {
        this.mContext = context;
        this.mDrawable = drawable;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutIntroItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.layout_intro_item, container, false);
        View view = binding.getRoot();

        Picasso.with(mContext).load(mDrawable[position]).fit().centerCrop().into(binding.imageViewMain);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mDrawable.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}
