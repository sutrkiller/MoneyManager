package pv239.fi.muni.cz.moneymanager.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Dialog for selecting date
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DatePicker datePicker;
    private OnDateInteractionListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        Bundle bundle = getArguments();
        int year = bundle.getInt("year",c.get(Calendar.YEAR));
        int month = bundle.getInt("month",c.get(Calendar.MONTH));
        int day = bundle.getInt("day",c.get(Calendar.DAY_OF_MONTH));
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mListener!=null) {
            mListener.onDateInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDateInteractionListener) {
            mListener = (OnDateInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDateInteractionListener {
        void onDateInteraction(DatePicker datePicker);
    }
}
