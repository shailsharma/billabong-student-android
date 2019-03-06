package in.securelearning.lil.android.home.views.fragment;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Secure on 14-04-2017.
 */

public class PieDataExt extends ChartData<IPieDataSet> {
    public List<Float> innerDataCount;



    public List<Float> getInnerDataCount() {
        return innerDataCount;
    }

    public void setInnerDataCount(List<Float> innerDataCount) {
        this.innerDataCount = innerDataCount;
    }

    public PieDataExt() {
        super();
    }

    public PieDataExt(List<String> xVals) {
        super(xVals);
    }

    public PieDataExt(String[] xVals) {
        super(xVals);
    }

    public PieDataExt(List<String> xVals, IPieDataSet dataSet, ArrayList<Float> innerCount) {
        super(xVals, toList(dataSet));
        this.innerDataCount = innerCount;
    }

    public PieDataExt(String[] xVals, IPieDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<IPieDataSet> toList(IPieDataSet dataSet) {
        List<IPieDataSet> sets = new ArrayList<IPieDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Sets the PieDataSet this data object should represent.
     *
     * @param dataSet
     */
    public void setDataSet(IPieDataSet dataSet) {
        mDataSets.clear();
        mDataSets.add(dataSet);
        init();
    }

    /**
     * Returns the DataSet this PieData object represents. A PieData object can
     * only contain one DataSet.
     *
     * @return
     */
    public IPieDataSet getDataSet() {
        return mDataSets.get(0);
    }

    /**
     * The PieData object can only have one DataSet. Use getDataSet() method instead.
     *
     * @param index
     * @return
     */
    @Override
    public IPieDataSet getDataSetByIndex(int index) {
        return index == 0 ? getDataSet() : null;
    }

    @Override
    public IPieDataSet getDataSetByLabel(String label, boolean ignorecase) {
        return ignorecase ? label.equalsIgnoreCase(mDataSets.get(0).getLabel()) ? mDataSets.get(0)
                : null : label.equals(mDataSets.get(0).getLabel()) ? mDataSets.get(0) : null;
    }

    /**
     * Returns the sum of all values in this PieData object.
     *
     * @return
     */
    public float getYValueSum() {

        float sum = 0;

        for (int i = 0; i < getDataSet().getEntryCount(); i++)
            sum += getDataSet().getEntryForIndex(i).getVal();


        return sum;
    }
}

