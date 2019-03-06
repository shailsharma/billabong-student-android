package in.securelearning.lil.android.learningnetwork.comparator;

import java.util.Comparator;

import in.securelearning.lil.android.base.dataobjects.Group;

/**
 * Created by Pushkar Raj on 6/11/2016.
 */
public class SortGroup {


    public static class UidSorter implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            String firstUid = ((Group) lhs).getObjectId();
            String secondUid = ((Group) rhs).getObjectId();

            return firstUid.compareTo(secondUid);
        }
    }

    public static class LastConversationTimeSorter implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs != null && rhs != null && ((Group) lhs).getLastMessageTime() != null && ((Group) rhs).getLastMessageTime() != null) {

                long secondDate = ((Group) rhs).getLastMessageTime().getTime();
                long firstDate = ((Group) lhs).getLastMessageTime().getTime();

                if (firstDate == 0 || secondDate == 0) {
                    return 0;
                }
                if (firstDate > secondDate) return -1;
                else if (firstDate < (secondDate)) return 1;
                else return 0;
            } else {
                if (lhs != null && ((Group) lhs).getLastMessageTime() != null && ((Group) lhs).getLastMessageTime().getTime() > 0)
                    return -1;
                if (rhs != null && ((Group) rhs).getLastMessageTime() != null && ((Group) rhs).getLastMessageTime().getTime() > 0)
                    return 1;

                return 0;
            }
        }
    }


}
