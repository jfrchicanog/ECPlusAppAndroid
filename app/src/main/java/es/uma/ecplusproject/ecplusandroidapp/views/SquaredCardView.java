package es.uma.ecplusproject.ecplusandroidapp.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by francis on 8/12/16.
 */

public class SquaredCardView extends CardView {
    public SquaredCardView(Context context) {
        super(context);
    }

    public SquaredCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
