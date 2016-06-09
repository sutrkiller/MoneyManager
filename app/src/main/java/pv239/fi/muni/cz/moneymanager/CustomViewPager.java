package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Serves as custom xml holder
 *
 * Created by Klasovci on 6/3/2016.
 */
public class CustomViewPager extends android.support.v4.view.ViewPager{
    private boolean enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled && super.onInterceptTouchEvent(event);
    }

    public boolean isPagingEnabled() {
        return enabled;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}