package in.securelearning.lil.android.quizcreator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.app.R;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 07-03-2016.
 */
public class QuestionListItemAdapter extends RecyclerView.Adapter<QuestionListItemAdapter.QuestionListItemViewHolder> {


    protected View.OnClickListener mItemAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
    protected View.OnClickListener mRemoveItemAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
    private ArrayList<Question> mData;
    private LayoutInflater mLayoutInflater;
    private int mLastSelected;
    private int mSelectedTextColor;
    private int mUnselectedTextColor;

    public QuestionListItemAdapter(Context context, ArrayList<Question> data, int selectedTextColorQuizCreator, int unselectedTextColorQuizCreator) {
        this.mData = data;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mLastSelected = -1;
        this.mSelectedTextColor = selectedTextColorQuizCreator;
        this.mUnselectedTextColor = unselectedTextColorQuizCreator;
    }

    @Override
    public QuestionListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View container = mLayoutInflater.inflate(R.layout.layout_question_list_item, parent, false);
        container.setOnClickListener(mItemAction);

        QuestionListItemViewHolder questionListItemViewHolder = new QuestionListItemViewHolder(container);
        questionListItemViewHolder.mRemoveQuestionButton.setOnClickListener(mRemoveItemAction);
        return questionListItemViewHolder;

    }

    @Override
    public void onBindViewHolder(QuestionListItemViewHolder holder, int position) {
        Question question = mData.get(position);

        if (question.isSelected()) {
            holder.mQuestionTextView.setTextColor(mSelectedTextColor);
            holder.mQuestionNumberTextView.setTextColor(mSelectedTextColor);
        } else {
            holder.mQuestionTextView.setTextColor(mUnselectedTextColor);
            holder.mQuestionNumberTextView.setTextColor(mUnselectedTextColor);
        }
        holder.mQuestionTextView.setText(question.getQuestionText());
        holder.mQuestionNumberTextView.setText("Q " + String.valueOf(position + 1) + ".");
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<Question> getmData() {
        return mData;
    }

    /**
     * set item listener on each item of the list
     *
     * @param itemAction
     */
    public void setItemAction(View.OnClickListener itemAction) {
        mItemAction = itemAction;
    }

    public void setRemoveItemAction(View.OnClickListener itemAction) {
        mRemoveItemAction = itemAction;
    }

    public void addItem(Question question) {
        mData.add(question);
        notifyItemInserted(mData.size() - 1);
    }

    public boolean removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        if (mLastSelected == position) {
            mLastSelected = -1;
            return true;
        } else {
            return false;
        }
    }

    public Question getQuestionItemData(int position) {
        if (mLastSelected != -1) {
            mData.get(mLastSelected).setIsSelected(false);
            notifyItemChanged(mLastSelected);
        }
        Question question = mData.get(position);
        question.setIsSelected(true);
        mLastSelected = position;
        notifyItemChanged(position);

        return question;
    }

    public void closeAll() {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            Question question = mData.get(i);
            if (question.isSelected()) {
                question.setIsSelected(false);
                notifyItemChanged(i);
            }
        }
        //reset the last selected variable
        mLastSelected = -1;
    }

    public void updateItem(Question question) {
        mData.set(mLastSelected, question);
        notifyItemChanged(mLastSelected);
        closeAll();

    }

    /**********************************************************************************************/

    class QuestionListItemViewHolder extends RecyclerView.ViewHolder {
        TextView mQuestionTextView, mQuestionNumberTextView;
        ImageButton mRemoveQuestionButton;

        public QuestionListItemViewHolder(View itemView) {
            super(itemView);
            mQuestionTextView = (TextView) itemView.findViewById(R.id.question_list_item_text);
            mRemoveQuestionButton = (ImageButton) itemView.findViewById(R.id.question_list_item_delete);
            mQuestionNumberTextView = (TextView) itemView.findViewById(R.id.textview_question_number);
        }
    }
}
