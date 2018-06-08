package es.uma.ecplusproject.ecplusandroidapp.views;

import android.content.Context;
import android.util.AttributeSet;

public class LandscapeSquaredCardView extends SquaredCardView {
    public LandscapeSquaredCardView(Context context) {
        super(context);
    }

    public LandscapeSquaredCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LandscapeSquaredCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d("Measure width", MeasureSpec.toString(widthMeasureSpec));
        //Log.d("Measure height", MeasureSpec.toString(heightMeasureSpec));
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
