package in.securelearning.lil.android.player.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutQuestionFeedbackBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.QuestionFeedback;
import in.securelearning.lil.android.player.listener.FeedbackOptionSelectListener;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.adapter.FeedbackOptionAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class QuestionFeedbackFragment extends BottomSheetDialogFragment {

    @Inject
    PlayerModel mPlayerModel;

    LayoutQuestionFeedbackBinding mBinding;
    private final static String QUESTION_ID = "questionId";

    private Context mContext;
    private String mQuestionId;
    private String mFeedbackOptionId;

    public static QuestionFeedbackFragment newInstance(String questionId) {
        QuestionFeedbackFragment fragment = new QuestionFeedbackFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_question_feedback, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        getBundleData();
        fetchQuestionFeedbackOptions();
        initializeViews();
        return mBinding.getRoot();
    }

    private void getBundleData() {
        if (getArguments() != null) {
            mQuestionId = getArguments().getString(QUESTION_ID);
        }
    }

    private void onResultError() {
        GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
        dismiss();
    }

    private void setFeedbackOptions(ArrayList<IdNameObject> questionFeedbackOptions) {

        mBinding.recyclerViewFeedbackOptions.setVisibility(View.VISIBLE);
        mBinding.recyclerViewFeedbackOptions.setLayoutManager(new GridLayoutManager(mContext, 2));

        FeedbackOptionSelectListener listener = new FeedbackOptionSelectListener() {
            @Override
            public void OnFeedbackOptionSelected(String optionId) {
                mFeedbackOptionId = optionId;
            }
        };

        mBinding.recyclerViewFeedbackOptions.setAdapter(new FeedbackOptionAdapter(listener, questionFeedbackOptions));
    }


    @SuppressLint("CheckResult")
    private void fetchQuestionFeedbackOptions() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mPlayerModel.fetchQuestionFeedbackOptions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GlobalConfigurationParent>() {
                    @Override
                    public void accept(GlobalConfigurationParent globalConfigurationParent) throws Exception {
                        mBinding.progressBar.setVisibility(View.GONE);
                        if (globalConfigurationParent != null
                                && globalConfigurationParent.getQuestionFeedbackOptions() != null
                                && !globalConfigurationParent.getQuestionFeedbackOptions().isEmpty()) {
                            setFeedbackOptions(globalConfigurationParent.getQuestionFeedbackOptions());
                        } else {
                            onResultError();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        onResultError();
                    }
                });
    }

    /*To post question feedback*/
    @SuppressLint("CheckResult")
    private void postQuestionFeedback(QuestionFeedback questionFeedback) {
        mPlayerModel.postQuestionFeedback(questionFeedback)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        if (responseBody != null) {
                            GeneralUtils.showToastShort(mContext, mContext.getString(R.string.question_feedback_successful));
                            dismiss();
                        } else {
                            GeneralUtils.showToastLong(mContext, mContext.getString(R.string.question_feedback_failed));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        GeneralUtils.showToastLong(mContext, mContext.getString(R.string.question_feedback_failed));

                    }
                });
    }

    private void initializeViews() {
        mBinding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (TextUtils.isEmpty(mFeedbackOptionId)) {
                        GeneralUtils.showToastShort(mContext, mContext.getString(R.string.feedback_option_prompt_message));
                    } else if (TextUtils.isEmpty(mBinding.editTextIssueDetails.getText().toString().trim())) {
                        GeneralUtils.showToastShort(mContext, mContext.getString(R.string.feedback_comment_prompt_message));
                    } else {
                        QuestionFeedback questionFeedback = new QuestionFeedback();
                        questionFeedback.setQuestionId(mQuestionId);
                        questionFeedback.setIssueAppearIn(mFeedbackOptionId);
                        questionFeedback.setComment(mBinding.editTextIssueDetails.getText().toString().trim());
                        postQuestionFeedback(questionFeedback);
                    }
                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.connect_internet));

                }

            }
        });
    }


}
