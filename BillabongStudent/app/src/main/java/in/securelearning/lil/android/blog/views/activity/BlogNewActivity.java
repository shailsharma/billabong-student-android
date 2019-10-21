package in.securelearning.lil.android.blog.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityBlogNewBinding;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.blog.views.fragment.BlogFragment;


public class BlogNewActivity extends AppCompatActivity implements BlogFragment.OnListFragmentInteractionListener {
    ActivityBlogNewBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_blog_new);
        setTitle("Blogs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int colCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        BlogFragment fragment = BlogFragment.newInstance(colCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_blog, fragment);
        fragmentTransaction.commit();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, BlogNewActivity.class);
        return intent;
    }

    @Override
    public void onListFragmentInteraction(Object object) {
        if (object instanceof Blog) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
