package pv239.fi.muni.cz.moneymanager.helper;

import android.view.MotionEvent;
import android.view.View;

/**
 * This class implements OnTouchListener and serves for detecting swipes on ListView to correctly delete them on swipe left or right.
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 5/1/2016.
 */

public class SwipeDetector implements View.OnTouchListener {

    private static final int MIN_DISTANCE = 100;
    private float downX;
    private float downY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        //Logger.show(Log.INFO,logTag, "Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        //Logger.show(Log.INFO,logTag, "Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                } else

                    // vertical swipe detection
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            //Logger.show(Log.INFO,logTag, "Swipe Top to Bottom");
                            mSwipeDetected = Action.TB;
                            return false;
                        }
                        if (deltaY > 0) {
                            //Logger.show(Log.INFO,logTag, "Swipe Bottom to Top");
                            mSwipeDetected = Action.BT;
                            return false;
                        }
                    }
                return true;
            }
        }
        return false;
    }

    public enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }
}
