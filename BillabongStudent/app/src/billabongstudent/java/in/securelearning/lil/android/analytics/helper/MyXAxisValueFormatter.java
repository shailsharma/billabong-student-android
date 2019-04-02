package in.securelearning.lil.android.analytics.helper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class MyXAxisValueFormatter implements IAxisValueFormatter {
    private List labels;

    public MyXAxisValueFormatter(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        try {
            int index = (int) value;
            return String.valueOf(labels.get(index));
        } catch (Exception e) {
            return "";
        }
    }
}