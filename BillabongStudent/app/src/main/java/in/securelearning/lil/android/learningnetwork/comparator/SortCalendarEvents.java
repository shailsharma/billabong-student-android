package in.securelearning.lil.android.learningnetwork.comparator;

import java.util.Comparator;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.utils.DateUtils;

/**
 * Created by Pushkar Raj on 6/11/2016.
 */
public class SortCalendarEvents {



    public static class CreatedTimeSorter implements Comparator {
            @Override
            public int compare(Object lhs, Object rhs) {

                //Sort by descending order of created time(latest first)

                long firstDate = DateUtils.convertrIsoDate(((CalendarEvent) lhs).getCreationTime()).getTime();
                long secondDate = DateUtils.convertrIsoDate(((CalendarEvent) rhs).getCreationTime()).getTime();

                if (firstDate == 0 || secondDate == 0) {
                    return 0;
                }

                if (firstDate > secondDate) return -1;
                else if (firstDate < (secondDate)) return 1;
                else return 0;

            }

    }


}
