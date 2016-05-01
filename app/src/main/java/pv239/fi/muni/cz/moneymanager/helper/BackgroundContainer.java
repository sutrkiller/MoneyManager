package pv239.fi.muni.cz.moneymanager.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Created by Tobias on 5/1/2016.
 */
public class BackgroundContainer extends FrameLayout {

    boolean mShowing = false;
    Drawable mShadowedBackgroundLeft;
    Drawable mShadowedBackgroundRight;
    int mOpenAreaTop, mOpenAreaBottom, mOpenAreaHeight, mOpenAreaRight, mOpenAreaXTrans, mOpenAreaWidth;
    boolean mUpdateBounds = false;

    public BackgroundContainer(Context context) {
        super(context);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShadowedBackgroundLeft =
                getContext().getResources().getDrawable(R.drawable.delete_left);
        mShadowedBackgroundRight = getContext().getResources().getDrawable(R.drawable.delete_right);
                //getContext().getResources().getDrawable(R.drawable.ic_delete_sweep_black_24dp);
                //getContext().getDrawable(R.drawable.shadowed_background);
    }

    public void showBackground(int top, int bottom, int left, int right, int width) {
        setWillNotDraw(false);
        mOpenAreaTop = top;
        mOpenAreaHeight = bottom;
        mOpenAreaRight = right;
        mOpenAreaXTrans = left;
        mOpenAreaWidth = width;
        mShowing = true;
        mUpdateBounds = true;

    }

    public void hideBackground() {
        setWillNotDraw(true);
        mShowing = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowing) {
            if (mUpdateBounds) {
                if (mOpenAreaXTrans>0) {
                    mShadowedBackgroundRight.setBounds(0, 0, mOpenAreaXTrans, mOpenAreaHeight);
                    canvas.save();
                    canvas.translate(0, mOpenAreaTop);
                    mShadowedBackgroundRight.draw(canvas);
                    canvas.restore();
                } else {
                    mShadowedBackgroundLeft.setBounds(getWidth()+mOpenAreaXTrans, 0, getWidth(), mOpenAreaHeight);
                    canvas.save();
                    canvas.translate(0, mOpenAreaTop);
                    mShadowedBackgroundLeft.draw(canvas);
                    canvas.restore();
                }
            }

        }
    }

}