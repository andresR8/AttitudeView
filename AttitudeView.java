//package YOUR_PACKAGE_HERE;
/**** This widget is based on CompassView from RedInput see NOTICE file*****/
/**** Modifications are tagged with %mod comment ****/
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;


public class AttitudeView extends View { //%mod  Class & constructor renamed
    private static final String TAG = "AttitudeView";//%mod

    /* OnAttitudeDragListener mListener;             //%mod removed

    public interface OnCompassDragListener {
        /**
         * Indicates when a drag event has ocurred
         *
         * @param degrees Actual value of the compass

        public void onCompassDragListener(float degrees);
    }*/


    private Paint mTextPaint, mMainLinePaint, mSecondaryLinePaint, mTerciaryLinePaint, mMarkerPaint, mTextPaintVal;
    private Paint mTextPaintBlack, mRollPaint;//%mod new
    private Path pathMarker;
    private int mTextColor, mBackgroundColor, mLineColor, mMarkerColor;
    private float mTextSize; //%mod mDegrees, , mRangeDegrees;  removed



    private float mPitch, mRangePitch,mRoll,mRangeRoll; //%mod new
    private boolean mShowMarkerPitch;//%mod  renamed from mShowMarker
    private static final int TICK_ROLL = 20;  //%mod new

    //private GestureDetector mDetector; %mod removed


    public AttitudeView(Context context, AttributeSet attrs) { //%mod
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AttitudeView, 0, 0);

        mBackgroundColor = a.getColor(R.styleable.AttitudeView_backgroundC, Color.TRANSPARENT); //%mod color changed to Color.TRANSPARENT
        mMarkerColor = a.getColor(R.styleable.AttitudeView_markerC, Color.BLACK);  //%mod color changed to Color.BLACK
        mShowMarkerPitch = a.getBoolean(R.styleable.AttitudeView_showM, true); //%mod  renamed from mShowMarker
        mLineColor = a.getColor(R.styleable.AttitudeView_lineC, Color.WHITE);
        mTextColor = a.getColor(R.styleable.AttitudeView_textC, Color.WHITE);
        mTextSize = a.getDimension(R.styleable.AttitudeView_textS, 15 * getResources().getDisplayMetrics().scaledDensity);
        mPitch = a.getFloat(R.styleable.AttitudeView_gradesPitch, 0f);             //%mod new
        mRoll = a.getFloat(R.styleable.AttitudeView_gradesRoll,0f);                //%mod new
        mRangePitch = a.getFloat(R.styleable.AttitudeView_rangeGradesPitch, 20f); //%mod new
        mRangeRoll = a.getFloat(R.styleable.AttitudeView_rangeGradesRoll, 20f);   //%mod new

        a.recycle();
        checkValues();
        init();
    }



    private void checkValues() {
        if ((mPitch < 0) || (mPitch > 359))
            throw new IndexOutOfBoundsException("Pitch value is out of bounds");

        if ( mRangePitch > 30)
            throw new IndexOutOfBoundsException("Pitch value is out of bounds");
    }

    /*private void checkValues() {  //              %mod entire method replaced
        if ((mDegrees < 0) || (mDegrees > 359))
            throw new IndexOutOfBoundsException(getResources()
                    .getString(R.string.out_index_degrees));

        if ((mRangeDegrees < 90) || (mRangeDegrees > 360))
            throw new IndexOutOfBoundsException(getResources().getString(
                    R.string.out_index_range_degrees));
    }*/

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //< %mod new
        mTextPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintBlack.setTextAlign(Paint.Align.CENTER);
        mTextPaintBlack.setColor(Color.BLACK);
        mTextPaintBlack.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //%mod >

        mTextPaintVal= new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintVal.setTextAlign(Paint.Align.CENTER);
        mTextPaintVal.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mMainLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainLinePaint.setStrokeWidth(8f);

        mSecondaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondaryLinePaint.setStrokeWidth(6f);

        mTerciaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTerciaryLinePaint.setStrokeWidth(3f);

        //< %mod new
        mRollPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRollPaint.setColor(Color.YELLOW);
        mRollPaint.setStyle(Paint.Style.STROKE);
        mRollPaint.setStrokeWidth(10);
        //%mod >

        mMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerPaint.setStyle(Paint.Style.FILL);
        pathMarker = new Path();

        //mDetector = new GestureDetector(getContext(), new mGestureListener()); %mod removed
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable("instanceState", super.onSaveInstanceState());
        b.putFloat("degrees", mPitch);

        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle b = (Bundle) state;
            mPitch = b.getFloat("degrees", 0);

            state = b.getParcelable("instanceState");
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int minWidth = (int) Math.floor(50 * getResources().getDisplayMetrics().density);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            result = minWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int minHeight = (int) Math.floor(30 * getResources().getDisplayMetrics().density);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            result = minHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mTextPaintVal.setColor(mTextColor);
        mTextPaintVal.setTextSize(mTextSize);



        mMainLinePaint.setColor(mLineColor);
        mSecondaryLinePaint.setColor(mLineColor);
        mTerciaryLinePaint.setColor(mLineColor);

        mMarkerPaint.setColor(mMarkerColor);
        canvas.drawColor(mBackgroundColor);

        int  height= getMeasuredHeight();
        int  width = getMeasuredWidth();

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();


        //******************<%mod new Pitch View*********************************/
        int unitHeight = (height - paddingTop - paddingBottom) / 12;
        int unitWidth = (width - paddingLeft - paddingRight) / 17;

        float pixDeg = (height - paddingTop - paddingBottom)  / (2* mRangePitch);

        int minDegrees = Math.round(mPitch - mRangePitch);
        int maxDegrees = Math.round(mPitch + mRangePitch);
        canvas.save();
        canvas.rotate(mRoll, getWidth() / 2, getHeight() / 2);

        for (int i = minDegrees; i <= maxDegrees; i ++) {
              if( i>=-90 && i<=90 && i!=minDegrees){

                if (i % 5 == 0 )
                    canvas.drawLine( width/2  - 2*unitWidth, paddingTop+pixDeg * (i - minDegrees),
                        width/2  + 2*unitWidth, paddingTop+pixDeg * (i - minDegrees)
                        , mTerciaryLinePaint);

                if (i % 10 == 0 ) {

                    canvas.drawLine(width / 2 - 2.4f * unitWidth, pixDeg * (i - minDegrees),
                            width / 2 + 2.4f * unitWidth, pixDeg * (i - minDegrees)
                            , mSecondaryLinePaint);

                    canvas.drawText("" + (-i),  width/2  - 4*unitWidth, 10 + pixDeg * (i - minDegrees), mTextPaint);

                }

              }
        }

        if (mShowMarkerPitch) {
            String marker=(mPitch==0)?"0":""+(Math.round(-mPitch));
            canvas.drawText(marker, width / 2 + 4*unitWidth , height/2 - 0.5f*unitHeight, mTextPaintVal);
        }

        //%mod>


        //******************<%mod new Roll View*********************************/


        float cx= getWidth()/2;
        float cy= getHeight()/2;
        // Center Lines
        canvas.drawLine(getWidth() / 7,cy, 2 * getWidth() / 5,cy, mRollPaint);
        canvas.drawLine(3 * getWidth() / 5, cy, getWidth() - getWidth() / 7, cy, mRollPaint);
        //Center indicator
        canvas.drawLine(2 * getWidth() / 5, 11 * getHeight() / 20, cx+0.2f*unitWidth, cy, mRollPaint);
        canvas.drawLine(cx-0.2f*unitWidth, cy, 3 * getWidth() / 5, 11 * getHeight() / 20, mRollPaint);

        canvas.restore();

        //Arc
        float offset=this.getWidth()/34;
        float ovalSize= getWidth()-offset;
        RectF rectF = new RectF(offset, offset, ovalSize, ovalSize);
        canvas.drawArc(rectF, 180+mRangeRoll,  180-2*mRangeRoll, false, mRollPaint);

        //Ticks
       for (int i =(int) mRangeRoll-90; i <= 90-mRangeRoll; i+=1) {

           if(i%TICK_ROLL==0) {
               canvas.save();
               canvas.rotate(i, cx, cy);
               canvas.drawRect(cx, offset - 5, cx, offset + 30, mRollPaint);
               canvas.restore();
           }
        }
        //Marker
        pathMarker.moveTo(cx, 0.5f * unitHeight + offset);
        pathMarker.lineTo(cx + 20, offset);
        pathMarker.lineTo(cx - 20, offset);
        pathMarker.close();
        canvas.save();
        canvas.rotate(mRoll, cx, cy);
        canvas.drawPath(pathMarker, mMarkerPaint);
        String pitchValue=""+ Math.round(mRoll);
        float w = mTextPaintVal.measureText(pitchValue)/2;
        canvas.drawRect(cx - 1.5f*w, 1.2f * unitHeight + offset - mTextSize, cx + 1.5f*w, 1.6f*unitHeight, mMarkerPaint);
        canvas.drawText(pitchValue, cx, 1.1f * unitHeight + offset, mTextPaintVal);
        canvas.restore();

        //%mod>

    }

    public float getPitch() {  //%mod new method
        return mPitch;
    }

    public void setPitch(float degrees) {  //%mod new method
        mPitch=angleRange(degrees);
        invalidate();
        requestLayout();
    }

    public float getRoll() {  //%mod new method
        return mRoll;
    }


    public void setRoll(float roll){     //%mod new method
            mRoll=angleRange(roll);
            invalidate();
            requestLayout();
    }

    public float angleRange(float angle){
        float angleF= Math.round(angle);

        if(angleF>=270 && angleF<=360)
            return angleF-360.0f;
        else
            return angleF;
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
        requestLayout();
    }

    public void setLineColor(int color) {
        mLineColor = color;
        invalidate();
        requestLayout();
    }

    public void setMarkerColor(int color) {
        mMarkerColor = color;
        invalidate();
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        invalidate();
        requestLayout();
    }

    public void setShowMarker(boolean show) {
        mShowMarkerPitch = show;
        invalidate();
        requestLayout();
    }

    public void setTextSize(int size) {
        mTextSize = size;
        invalidate();
        requestLayout();
    }

    public void setRangeDegrees(float range) {
        if ((mRangePitch < 90) || (mRangePitch > 360))
            throw new IndexOutOfBoundsException("Pitch range is out of bounds "  + mRangePitch);

        mRangePitch = range;
        invalidate();
        requestLayout();
    }




/*<%mod  removed Methods

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null) {
            boolean result = mDetector.onTouchEvent(event);
            if (!result) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    result = true;
                }
            }
            return result;
        } else {
            return true;
        }
    }


    private class mGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mPitch += distanceX / 5;
            if (mPitch < 0) {
                mPitch += 360;
            } else if (mPitch >= 360) {
                mPitch -= 360;
            }

            if (mListener != null) {
                mListener.onAttitudeDragListener(mPitch);
            }

            postInvalidate();
            return true;
        }
    }

    public void setOnAttitudeDragListener(OnAttitudeDragListener onAttitudeDragListener) {
        this.mListener = onAttitudeDragListener;
    }%mod>*/







}
