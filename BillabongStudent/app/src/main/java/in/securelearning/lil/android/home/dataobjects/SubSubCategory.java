package in.securelearning.lil.android.home.dataobjects;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Created by Prabodh Dhabaria on 28-04-2017.
 */

public class SubSubCategory {
    String mName;
    int mImageDrawableId = 0;
    int mColor = 0xFF000000;
    String mSearchTag = "";

    public SubSubCategory(String name, String searchTag, @DrawableRes int imageDrawableId, @ColorInt int color) {
        mName = name;
        mImageDrawableId = imageDrawableId;
        mColor = color;
        mSearchTag = searchTag;
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

}
