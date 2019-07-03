package in.securelearning.lil.android.analytics.helper;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ChartXAxisRenderer extends XAxisRenderer {
    public ChartXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
       // formattedLabel= formattedLabel.replace("-"," ");
        String line[] = formattedLabel.split("-");
                //formattedLabel.split("\\s+");
        if(line!=null && line.length>=2) {
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }
        else
        {
            Utils.drawXAxisValue(c, line[0].trim(), x, y, mAxisLabelPaint, anchor, angleDegrees);
        }
        }
}