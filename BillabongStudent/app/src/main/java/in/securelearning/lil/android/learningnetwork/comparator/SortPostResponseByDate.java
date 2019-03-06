package in.securelearning.lil.android.learningnetwork.comparator;

import java.util.Comparator;

import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.utils.DateUtils;


/**
 * Created by Pushkar Raj on 6/11/2016.
 */
public class SortPostResponseByDate {


    public static class CreatedDateSorter implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            //Sort by ascending order of created time

            long firstDate = DateUtils.convertrIsoDate(((PostResponse) rhs).getCreatedTime()).getTime();
            long secondDate =DateUtils.convertrIsoDate (((PostResponse) lhs).getCreatedTime()).getTime();

            if (firstDate == 0 || secondDate == 0) {
                return 0;
            }

            if (firstDate > secondDate) return -1;
            else if (firstDate < (secondDate)) return 1;
            else return 0;
        }
    }


}
