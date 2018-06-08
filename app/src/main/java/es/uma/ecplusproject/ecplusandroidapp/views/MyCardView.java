package es.uma.ecplusproject.ecplusandroidapp.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;

public class MyCardView extends CardView {
    private float ratio=1.0f;


    public MyCardView(Context context) {
        super(context);
    }

    public MyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d("MC Measure width", MeasureSpec.toString(widthMeasureSpec));
        //Log.d("MC Measure height", MeasureSpec.toString(heightMeasureSpec));
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int)Math.ceil(width*ratio);
        int myHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, myHeightMeasureSpec);
        //Log.d("MC width", ""+getMeasuredWidth());
        //Log.d("MC height", ""+getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //Log.d("MC left", ""+left);
        //Log.d("MC top", ""+left);
        //Log.d("MC right", ""+right);
        //Log.d("MC bottom", ""+bottom);
    }
}
