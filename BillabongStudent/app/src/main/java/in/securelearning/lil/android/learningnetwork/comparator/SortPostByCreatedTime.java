package in.securelearning.lil.android.learningnetwork.comparator;

import java.util.Comparator;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.utils.DateUtils;

/**
 * Created by Pushkar Raj on 6/11/2016.
 */
public class SortPostByCreatedTime {

    public static class CreatedTimeSorter implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {

            //Sort by ascending order of created time
            long firstDate = DateUtils.convertrIsoDate(((PostDataDetail) lhs).getPostData().getCreatedTime()).getTime();
            long secondDate = DateUtils.convertrIsoDate(((PostDataDetail) rhs).getPostData().getCreatedTime()).getTime();


            if (firstDate == 0 || secondDate == 0) {
                return 0;
            }

            if (firstDate > secondDate) return -1;
            else if (firstDate < (secondDate)) return 1;
            else return 0;
        }


    }

    public static class LastConversationTimeSorter implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {

            long firstDate = 0, secondDate = 0;
            if (lhs != null && ((PostDataDetail) lhs).getPostData() != null && ((PostDataDetail) lhs).getPostData().getLastMessageTime() != null) {

                firstDate = ((PostDataDetail) lhs).getPostData().getLastMessageTime().getTime();

            }
            if (rhs != null && ((PostDataDetail) rhs).getPostData() != null && ((PostDataDetail) rhs).getPostData().getLastMessageTime() != null) {
                secondDate = ((PostDataDetail) rhs).getPostData().getLastMessageTime().getTime();


            }

            if (firstDate != 0 && secondDate != 0) {
                if (firstDate == 0 || secondDate == 0) {
                    return 0;
                }
                if (firstDate > secondDate) {
                    return -1;
                } else if (firstDate < (secondDate)) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (lhs != null && firstDate > 0) {
                    return -1;

                }
                if (rhs != null && secondDate > 0) {
                    return 1;
                }
                return 0;
            }


        }
    }

}
