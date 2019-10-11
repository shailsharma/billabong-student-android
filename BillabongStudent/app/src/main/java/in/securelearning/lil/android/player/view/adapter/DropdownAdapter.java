package in.securelearning.lil.android.player.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.util.ArrayList;
import java.util.Objects;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutQuestionDropdownItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewInCardBinding;
import in.securelearning.lil.android.base.dataobjects.QuestionPart;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class DropdownAdapter extends RecyclerView.Adapter<DropdownAdapter.ViewHolder> {
    private ArrayList<QuestionPart> mList;
    private ArrayList<QuestionPart> mResponseList;
    private boolean mIsAttemptMode;
    private Context mContext;
    public final static String TYPE_CHOICE = "choice";
    public final static String TYPE_QUESTION = "question";

    public DropdownAdapter(Context context, ArrayList<QuestionPart> list, boolean isAttempt) {
        this.mContext = context;
        this.mList = list;
        this.mIsAttemptMode = isAttempt;
        initializeResponseList(mList);
    }

    private void initializeResponseList(ArrayList<QuestionPart> list) {
        mResponseList = new ArrayList<>();
        for (QuestionPart questionPart : list) {
            if (questionPart.getType().equals(TYPE_CHOICE)) {
                  /*Here setting user selection to check validation,
                used setQuestion because no extra variable available in the pojo*/
                questionPart.setQuestion(ConstantUtil.BLANK);
                mResponseList.add(questionPart);
            }
        }
    }

    @Override
    public DropdownAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutQuestionDropdownItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_question_dropdown_item, parent, false);
        return new DropdownAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final DropdownAdapter.ViewHolder holder, final int position) {
        final QuestionPart questionPart = mList.get(position);

        if (!TextUtils.isEmpty(questionPart.getType())) {

            String type = questionPart.getType();

            if (type.equalsIgnoreCase(TYPE_CHOICE)) {

                if (!mIsAttemptMode) {
                    holder.mBinding.editTextDropdown.setEnabled(false);
                    holder.mBinding.editTextDropdown.setEms(questionPart.getCorrectValue().length());
                    holder.mBinding.editTextDropdown.setText(questionPart.getCorrectValue());
                } else {
                    holder.mBinding.editTextDropdown.setEnabled(true);
                }

                holder.mBinding.editTextDropdown.setVisibility(View.VISIBLE);
                holder.mBinding.textViewText.setVisibility(View.GONE);
                holder.mBinding.editTextDropdown.setTag(questionPart.getCorrectValue());

                holder.mBinding.editTextDropdown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setUpChoicePopup(questionPart, holder.mBinding.editTextDropdown);
                    }
                });

            } else if (type.equalsIgnoreCase(TYPE_QUESTION)) {
                holder.mBinding.editTextDropdown.setVisibility(View.GONE);
                holder.mBinding.textViewText.setVisibility(View.VISIBLE);
                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(holder.mBinding.textViewText);
                htmlHttpImageGetter.enableCompressImage(true, 80);
                holder.mBinding.textViewText.setHtml(questionPart.getQuestion(), htmlHttpImageGetter);
                holder.mBinding.textViewText.setRemoveTrailingWhiteSpace(true);
            } else {
                holder.mBinding.editTextDropdown.setVisibility(View.GONE);
                holder.mBinding.textViewText.setVisibility(View.GONE);
            }
        }

    }

    private void setUpChoicePopup(final QuestionPart questionPart, final AppCompatEditText editTextDropdown) {

        LayoutRecyclerViewInCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_recycler_view_in_card, null, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.recycler_view_item_divider)));
        binding.recyclerView.addItemDecoration(itemDecorator);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double width = metrics.widthPixels * 0.60;

        ArrayList<String> values = questionPart.getValues();

        int popupWidth = width.intValue();
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(editTextDropdown.getContext());
        popupWindow.setContentView(binding.getRoot());
        popupWindow.setWidth(popupWidth);
        popupWindow.setHeight(popupHeight);
        popupWindow.setFocusable(true);

        int OFFSET_X = 10;
        int OFFSET_Y = editTextDropdown.getHeight();

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(8f);
        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);

        int[] location = new int[2];
        editTextDropdown.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        popupWindow.showAtLocation(binding.getRoot(), Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);

        final DropdownChoiceItemAdapter arrayAdapter = new DropdownChoiceItemAdapter(values);
        binding.recyclerView.setAdapter(arrayAdapter);

        arrayAdapter.setItemClickAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Here setting user selection to check validation,
                used setQuestion because no extra variable available in the pojo*/
                questionPart.setQuestion(((TextView) view).getText().toString());

                editTextDropdown.setText(((TextView) view).getText().toString());
                popupWindow.dismiss();
            }
        });
    }

    public ArrayList<QuestionPart> getResponseList() {
        return mResponseList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutQuestionDropdownItemBinding mBinding;

        public ViewHolder(LayoutQuestionDropdownItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}