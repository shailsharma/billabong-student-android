package in.securelearning.lil.android.blog.views.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.app.R;

public class BlogDetailsActivity extends AppCompatActivity {

    private Blog mBlog;
    private TextView mTitleTextView, mPostedOnTextView, mBloggerNameTextView, mCommentsCountTextView, mBlogDescriptionTextView;
    private ImageView mBlogThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_blog_details_asctivity);

        mBlog = (Blog) getIntent().getExtras().getSerializable("blogItem");
        findViewById(R.id.postButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) findViewById(R.id.edittext_comment)).setText("");
            }
        });

        setupBlogDetails();

    }

    private void setupBlogDetails() {

        mTitleTextView = (TextView) findViewById(R.id.textview_blog_title);
        mBlogThumbnail = (ImageView) findViewById(R.id.image_view);
        mPostedOnTextView = (TextView) findViewById(R.id.textview_blog_time);
        mBloggerNameTextView = (TextView) findViewById(R.id.textview_blogger_name);
        mCommentsCountTextView = (TextView) findViewById(R.id.textview_comments_count);
        mBlogDescriptionTextView = (TextView) findViewById(R.id.textview_blog_text);


        if (mBlog != null) {
            String imagePath = mBlog.getThumbnail().getSecureUrl();
            if (!imagePath.isEmpty()) {
                mBlogThumbnail.setImageBitmap(ImageUtils.getScaledBitmapFromPath(getResources(), imagePath));
            }
            mTitleTextView.setText(mBlog.getTitle());
            mPostedOnTextView.setText("Posted on " + DateUtils.getTimeStringFromDateString(mBlog.getCreationTime()));
            mBloggerNameTextView.setText(" by " + mBlog.getCreatedBy());
            mBlogDescriptionTextView.setText(mBlog.getDescription());

        }

    }


}
