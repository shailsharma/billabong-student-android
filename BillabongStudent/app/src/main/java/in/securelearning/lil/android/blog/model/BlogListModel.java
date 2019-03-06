package in.securelearning.lil.android.blog.model;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.model.BlogCommentModel;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.blog.InjectorBlog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Prabodh Dhabaria on 21-02-2017.
 */

public class BlogListModel {
    @Inject
    BlogModel mBlogModel;
    @Inject
    BlogCommentModel mBlogCommentModel;

    public BlogListModel() {
        InjectorBlog.INSTANCE.getComponent().inject(this);
    }

    public Observable<BlogDetails> getBlogList() {
        Observable<BlogDetails> observable = Observable.create(new ObservableOnSubscribe<BlogDetails>() {
            @Override
            public void subscribe(ObservableEmitter<BlogDetails> subscriber) {
                for (BlogDetails blogDetails :
                        mBlogModel.getCompleteBlogsDetailsesListSync()) {
                    subscriber.onNext(blogDetails);
                }
                subscriber.onComplete();

            }
        });
        return observable;

    }

    public Observable<ArrayList<BlogDetails>> getBlogList(final int skip, final int limit) {
        Observable<ArrayList<BlogDetails>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<BlogDetails>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<BlogDetails>> subscriber) {
                ArrayList<BlogDetails> blogDetails = mBlogModel.getCompleteBlogDetailListSync(skip, limit);
                subscriber.onNext(blogDetails);
                subscriber.onComplete();

            }
        });
        return observable;

    }

    public BlogDetails getBlogDetail(String id) {
        return mBlogModel.getBlogDetailsFromUidSync(id);

    }

    public Blog getBlog(String id) {
        return mBlogModel.getBlogFromUidSync(id);

    }

    public ArrayList<Blog> fetchBlogs() {
        return mBlogModel.getBlogsListSync();
    }

    public void deleteAllBlogs() {
        mBlogModel.deleteAllBlogs();
    }

    public Blog saveObject(Blog blog){
        return mBlogModel.saveBlog(blog);
    }

}