package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.securelearning.lil.android.base.utils.DateUtils;


public class TimeUtils {

    public static final Calendar FIRST_DAY_OF_TIME;
    public static final Calendar LAST_DAY_OF_TIME;
    public static final int DAYS_OF_TIME;
    public static final int WEEKS_OF_TIME;

    static {
        FIRST_DAY_OF_TIME = Calendar.getInstance();
        FIRST_DAY_OF_TIME.set(1900, Calendar.JANUARY, 1);
        LAST_DAY_OF_TIME = Calendar.getInstance();
        LAST_DAY_OF_TIME.set(2100, Calendar.DECEMBER, 31);
        DAYS_OF_TIME = 73413; //(int) ((LAST_DAY_OF_TIME.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis()) / (24 * 60 * 60 * 1000));
        WEEKS_OF_TIME = 10000;
    }

    public static String getDueString(Date dueDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);
        Calendar current = Calendar.getInstance();
        final int dueDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        final int currentDayOfYear = current.get(Calendar.DAY_OF_YEAR);
        if (dueDayOfYear < currentDayOfYear) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(calendar.getTime());
            temp.add(Calendar.DAY_OF_YEAR, 1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "overdue yesterday";
            }
            temp.add(Calendar.DAY_OF_YEAR, 1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "overdue 2 days ago";
            }
            temp.add(Calendar.DAY_OF_YEAR, 1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "overdue 3 days ago";
            }
            return "overdue";
        } else if (dueDayOfYear > currentDayOfYear) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(calendar.getTime());
            temp.add(Calendar.DAY_OF_YEAR, -1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "due tomorrow";
            }
            temp.add(Calendar.DAY_OF_YEAR, -1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "due in 2 days";
            }
            temp.add(Calendar.DAY_OF_YEAR, -1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "due in 3 days";
            }
            return "due date";

        } else {
            return "due today";
        }

    }

    public static String getRealTimeString(String s) {
        Date date = DateUtils.convertrIsoDate(s);
        return getRealTimeString(date);
    }

    public static String getRealTimeString(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar current = Calendar.getInstance();
            final int dueDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            final int currentDayOfYear = current.get(Calendar.DAY_OF_YEAR);
            if (dueDayOfYear < currentDayOfYear) {
                Calendar temp = Calendar.getInstance();
                temp.setTime(calendar.getTime());
                temp.add(Calendar.DAY_OF_YEAR, 1);
                if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                    return "Yesterday";
                } else {
                    return DateUtils.getFormatedDateFromDate(date);
                }

            } else {

                return getTimeString(date);
            }
        } else {
            return "";
        }
    }

    public static String getRealDateString(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int dueDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        Calendar current = Calendar.getInstance();
        final int currentDayOfYear = current.get(Calendar.DAY_OF_YEAR);

        if (dueDayOfYear < currentDayOfYear) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(calendar.getTime());
            temp.add(Calendar.DAY_OF_YEAR, 1);
            if (temp.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                return "Yesterday";
            } else {
                return getDateString(date);
            }

        } else if (dueDayOfYear == currentDayOfYear) {
            return "Today";
        } else {
            return getDateString(date);
        }

    }

    public static String getTimeString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static int compare(Calendar c1, Calendar c2) {
        final int c1DayOfYear = c1.get(Calendar.DAY_OF_YEAR);
        final int c2DayOfYear = c2.get(Calendar.DAY_OF_YEAR);
        if (c1DayOfYear < c2DayOfYear) {
            return -1;
        } else if (c1DayOfYear > c2DayOfYear) {
            return 1;
        } else {
            return 0;
        }

    }

    public static int compare(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return compare(c1, c2);
    }

    public static int compare(String s1, String s2) {
        Date d1 = DateUtils.convertrIsoDate(s1);
        Date d2 = DateUtils.convertrIsoDate(s2);
        return compare(d1, d2);
    }


    /**
     * Get the position in the ViewPager for a given day
     *
     * @param day
     * @return the position or 0 if day is null
     */
    public static int getPositionForDay(Calendar day) {
        if (day != null) {
            return (int) ((day.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis())
                    / 86400000  //(24 * 60 * 60 * 1000)
            );
        }
        return 0;
    }

    /**
     * Get the position in the ViewPager for a given week
     *
     * @param day
     * @return the position or 0 if day is null
     */
    public static int getPositionForWeek(Calendar day) {
        if (day != null) {
            return (int) ((day.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis())
                    / 86400000 / 7  //(24 * 60 * 60 * 1000)
            );
        }
        return 0;
    }


    /**
     * Get the day for a given position in the ViewPager
     *
     * @param position
     * @return the day
     * @throws IllegalArgumentException if position is negative
     */
    public static Calendar getDayForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, position);
        return cal;
    }

    /**
     * Get the day for a given position in the ViewPager
     *
     * @param position
     * @return the day
     * @throws IllegalArgumentException if position is negative
     */
    public static Calendar getWeekForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.WEEK_OF_YEAR, position);
        return cal;
    }

    public static ArrayList<Calendar> getWeekDaysFromDay(Calendar day) {

        if (day != null) {
            ArrayList<Calendar> calendars = new ArrayList<>();
            int start = day.get(Calendar.DAY_OF_WEEK);
            calendars.add(day);

            if (start == Calendar.SUNDAY) {
                return calendars;
            }

            start++;
            int i = 0;
            for (i = 1; start <= 8; i++, start++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(day.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, i);
                calendars.add(calendar);
            }

            return calendars;

        }
        return null;
    }

    /**
     * Get the day for a given position in the ViewPager
     *
     * @param position
     * @return the day
     * @throws IllegalArgumentException if position is negative
     */
    public static ArrayList<Calendar> getDaysOfWeekForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        ArrayList<Calendar> calendars = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.WEEK_OF_YEAR, position);
        calendars.add(cal);
        for (int i = 1; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cal.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, i);
            calendars.add(calendar);
        }
        return calendars;
    }

    /**
     * Get the day for a given position in the ViewPager
     *
     * @param position
     * @return the day
     * @throws IllegalArgumentException if position is negative
     */
    public static Calendar getFirstDayOfWeekForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.WEEK_OF_YEAR, position);
        return cal;
    }


    public static String getFormattedDate(Context context, long date) {
        final String defaultPattern = "yyyy-MM-dd";

        String pattern = null;
        if (context != null) {
            pattern = "dd-MM-yyyy";
        }
        if (pattern == null) {
            pattern = defaultPattern;
        }
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } catch (IllegalArgumentException e) {
            simpleDateFormat = new SimpleDateFormat(defaultPattern);
        }

        return simpleDateFormat.format(new Date(date));
    }


}
