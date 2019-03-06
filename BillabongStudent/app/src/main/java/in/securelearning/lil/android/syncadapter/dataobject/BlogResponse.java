package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Blog;

/**
 * Created by Chaitendra on 07-Nov-17.
 */

public class BlogResponse implements Serializable {

    @SerializedName("blogInstances")
    @Expose
    private ArrayList<Blog> mBlogs = null;

    @SerializedName("publishedBlogsCount")
    @Expose
    private int mPublishedBlogsCount = 0;

    public ArrayList<Blog> getBlogs() {
        return mBlogs;
    }

    public void setBlogs(ArrayList<Blog> blogs) {
        this.mBlogs = blogs;
    }

    public int getPublishedBlogsCount() {
        return mPublishedBlogsCount;
    }

    public void setPublishedBlogsCount(int publishedBlogsCount) {
        this.mPublishedBlogsCount = publishedBlogsCount;
    }
}
