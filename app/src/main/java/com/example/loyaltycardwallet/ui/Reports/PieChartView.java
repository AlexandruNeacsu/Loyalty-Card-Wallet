package com.example.loyaltycardwallet.ui.Reports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PieChartView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] COLORS = {
            Color.BLUE, Color.BLACK,
            Color.GREEN, Color.YELLOW,
            Color.GRAY, Color.MAGENTA,
            Color.CYAN, Color.GRAY,
            Color.RED, Color.DKGRAY,
            Color.LTGRAY
    };

    RectF rectf = new RectF(0, 0, getWidth(), getHeight());

    private float[] values_degree;

    public PieChartView(Context context) {
        super(context);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // get canvas size and update the rect
        int width = getWidth();
        int height = getHeight();

        int size = Math.min(width, height);

        int MARGIN = 50;
        rectf.set(MARGIN, MARGIN, size - MARGIN, size - MARGIN);

        float startAngle = 0;

        for (int i = 0; values_degree != null && i < values_degree.length; i++) {
            if (values_degree[i] > 0) {
                paint.setColor(COLORS[i]);

                canvas.drawArc(rectf, startAngle, values_degree[i], true, paint);

                startAngle += values_degree[i];
            }
        }
    }

    public void setValues_degree(float[] values_degree) {
        if (values_degree.length <= 11) {
            this.values_degree = values_degree;
        }

        invalidate();
    }
}
