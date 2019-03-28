package in.securelearning.lil.android.analytics.helper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MyPercentFormatter implements IValueFormatter, IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return Math.round(value) + " %";
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value > 0) {
            return Math.round(value) + " %";

        } else {
            return "";

        }
    }
}