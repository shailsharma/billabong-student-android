package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentPostNewBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class PostFragmentNew extends Fragment {
    @Inject
    Context mContext;
    @Inject
    RxBus mRxBus;

    private String mBaseFolder, mLearningNetworkFolder;

    private static final String ARG_GROUP_OBJECT_ID = "group_object_id";
    private String mSelectedGroupObjectId;
    private Disposable mSubscription;

    FragmentPostNewBinding mBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostFragmentNew() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostFragmentNew newInstance(String groupId) {
        PostFragmentNew fragment = new PostFragmentNew();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_OBJECT_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_new, container, false);
        if (getArguments() != null) {
            mSelectedGroupObjectId = getArguments().getString(ARG_GROUP_OBJECT_ID);
        }
        mBaseFolder = getContext().getFilesDir().getAbsolutePath();
        initializeResourceFolders("LearningNetwork");
        setupSubscription();
        if (!TextUtils.isEmpty(mSelectedGroupObjectId))
            getData();
        return mBinding.getRoot();
    }

    public void getData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) mSubscription.dispose();

    }

    /**
     * set up Disposable to listen to RxBus
     */
    private void setupSubscription() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object eventObject) {

            }
        });
    }

    /**
     * method to hide soft keyboard
     *
     * @param mPostEditText
     */
    public void hideSoftKeyboard(EditText mPostEditText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPostEditText.getWindowToken(), 0);

    }


    /**
     * initialize the folders for resources
     *
     * @param parentFolderAbsolutePath
     */
    private void initializeResourceFolders(String parentFolderAbsolutePath) {
        mLearningNetworkFolder = parentFolderAbsolutePath + File.separator + "learning network resources";
    }


}
