package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostResponseListFragment;

/**
 * Created by Chaitendra on 04-Aug-17.
 */

public class PostResponseListActivity extends AppCompatActivity {

    public static final String GROUP_ID = "groupId";
    public static final String POST_ID = "postId";
    public static final String POST_ALIAS = "postAlias";
    private String mGroupId = "";
    private String mPostId = "";
    private String mPostAlias = "";

    public static Intent getIntentForPostResponseList(Context context, String groupId, String postId, String postAlias) {
        Intent intent = new Intent(context, PostResponseListActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(POST_ID, postId);
        intent.putExtra(POST_ALIAS, postAlias);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.layout_activity_post_response);
        handleIntent();
    }

    private void handleIntent() {
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mPostId = getIntent().getStringExtra(POST_ID);
        mPostAlias = getIntent().getStringExtra(POST_ALIAS);
        PostResponseListFragment fragment = PostResponseListFragment.newInstance(1, mGroupId, mPostId, mPostAlias);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerMain, fragment);
        fragmentTransaction.commit();
    }
}
