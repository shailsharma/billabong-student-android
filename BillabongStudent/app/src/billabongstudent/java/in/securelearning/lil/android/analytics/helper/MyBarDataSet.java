package in.securelearning.lil.android.analytics.helper;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.dataobjects.CoverageChartData;

public class MyBarDataSet extends BarDataSet {
        List<BarEntry> mValues;

        private MyBarDataSet(List<BarEntry> values, String label) {
            super(values, label);
            mValues = values;
        }

        @Override
        public int getColor(int index) {
            CoverageChartData coverageChartData = (CoverageChartData) mValues.get(index).getData();
            float completed = (coverageChartData.getCoverage() / coverageChartData.getTotal()) * 100f;
            if (completed < 70)
                return R.color.colorAnnouncement;
            else if (completed > 70 && completed <= 90)
                return R.color.colorGreen;
            else {
                return R.color.colorGreenDark;
            }
        }


    }