package pv239.fi.muni.cz.moneymanager.crypto;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Helper class for showing pin in readable format
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class PasswordEditTextOnTouchListener implements View.OnTouchListener {

    private Context context;
    public PasswordEditTextOnTouchListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //final int DRAWABLE_LEFT = 0;
        //final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        //final int DRAWABLE_BOTTOM = 3;
        EditText editText = (EditText)v;

        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                if ((editText.getInputType() & InputType.TYPE_NUMBER_VARIATION_PASSWORD) > 0) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(context, R.drawable.ic_visibility_off_black_24dp),null);
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(context,R.drawable.ic_visibility_black_24dp),null);
                }
                return true;
            }
        }
        return false;
    }
}
