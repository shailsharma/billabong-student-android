package in.securelearning.lil.android.home.dataobjects;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;

import in.securelearning.lil.android.home.views.activity.SearchResultListActivity;

/**
 * Created by Prabodh Dhabaria on 28-04-2017.
 */

public class SubCategory {
    String mName;
    int mImageDrawableId = 0;
    int mColor = 0xFF000000;
    String mSearchTag = "";
    ArrayList<SubSubCategory> mSubSubCategories = new ArrayList<>();

    public SubCategory(String name, String searchTag, @DrawableRes int imageDrawableId, @ColorInt int color, ArrayList<SubSubCategory> subSubCategories) {
        mName = name;
        mImageDrawableId = imageDrawableId;
        mColor = color;
        mSearchTag = searchTag;
        mSubSubCategories = subSubCategories;
    }

    public int getColor() {
        return mColor;
    }

    public int getImageDrawableId() {
        return mImageDrawableId;
    }

    public String getName() {
        return mName;
    }

    public String getSearchTag() {
        return mSearchTag;
    }

    public ArrayList<SubSubCategory> getSubSubCategories() {
        return mSubSubCategories;
    }

}
