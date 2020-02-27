package cardstackview;

import android.content.Context;
import android.graphics.Point;

public class CardStackUtil {

    private CardStackUtil() {}

    public static float toPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static double getRadian(float x1, float y1, float x2, float y2) {
        float width = x2 - x1;
        float height = y1 - y2;
        return Math.atan(Math.abs(height) / Math.abs(width));
    }

    public static Point getTargetPoint(float x1, float y1, float x2, float y2) {
        float radius = 2000f;
        double radian = CardStackUtil.getRadian(x1, y1, x2, y2);

        CardStackQuadrant cardStackQuadrant = getQuadrant(x1, y1, x2, y2);
        if (cardStackQuadrant == CardStackQuadrant.TopLeft) {
            double degree = Math.toDegrees(radian);
            degree = 180 - degree;
            radian = Math.toRadians(degree);
        } else if (cardStackQuadrant == CardStackQuadrant.BottomLeft) {
            double degree = Math.toDegrees(radian);
            degree = 180 + degree;
            radian = Math.toRadians(degree);
        } else if (cardStackQuadrant == CardStackQuadrant.BottomRight) {
            double degree = Math.toDegrees(radian);
            degree = 360 - degree;
            radian = Math.toRadians(degree);
        } else {
            double degree = Math.toDegrees(radian);
            radian = Math.toRadians(degree);
        }

        double x = radius * Math.cos(radian);
        double y = radius * Math.sin(radian);

        return new Point((int) x, (int) y);
    }

    public static CardStackQuadrant getQuadrant(float x1, float y1, float x2, float y2) {
        if (x2 > x1) { // Right
            if (y2 > y1) { // Bottom
                return CardStackQuadrant.BottomRight;
            } else { // Top
                return CardStackQuadrant.TopRight;
            }
        } else { // Left
            if (y2 > y1) { // Bottom
                return CardStackQuadrant.BottomLeft;
            } else { // Top
                return CardStackQuadrant.TopLeft;
            }
        }
    }

}
