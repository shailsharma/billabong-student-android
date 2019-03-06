package in.securelearning.lil.android.home.dataobjects;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;

import in.securelearning.lil.android.syncadapter.utils.PrefManager;

/**
 * Created by Prabodh Dhabaria on 28-04-2017.
 */

public class Category extends PrefManager.SubjectExt {
    ArrayList<SubCategory> mSubCategories = new ArrayList<>();

    public Category(String name, String id, @DrawableRes int imageDrawableId, @ColorInt int color, ArrayList<SubCategory> subCategories) {
        super(id, name, color, color, color);
        mSubCategories = subCategories;
    }

    public Category() {
    }

    public ArrayList<SubCategory> getSubCategories() {
        return mSubCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        mSubCategories = subCategories;
    }
}
