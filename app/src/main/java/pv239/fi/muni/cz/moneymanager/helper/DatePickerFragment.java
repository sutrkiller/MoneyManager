package pv239.fi.muni.cz.moneymanager.helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Dialog for selecting date
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static int ADD_RECORDS =1;
    public static int ORDER_RECORDS =2;
    private OnDateInteractionListener mListener;
    private int caller = -1;

    public static void setDateButtonTag(Context context, DatePicker date, Button button) {
        button.setTag(date);

        int day = date.getDayOfMonth();
        int month = date.getMonth();
        int year = date.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String dateF = formatDate(context,calendar);
        button.setText(dateF);
    }

    public static Date stringToDate(Context context, String str) {
        if (str == null) return null;
        DateFormat dateFormat = new SimpleDateFormat( context.getString(R.string.datepicker_date_format), Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException ignored) {
        }
        return date;
    }

    public static String dateToString(Context context, Date date) {
        DateFormat format = new SimpleDateFormat( context.getString(R.string.datepicker_date_format), Locale.getDefault());
        return format.format(date);
    }

    public static String formatDate(Context context, Calendar calendar) {
        DateFormat dateFormat = DateFormat.getInstance();
        dateFormat.setCalendar(calendar);

        DateFormat format = new SimpleDateFormat( context.getString(R.string.datepicker_date_format), Locale.getDefault());
        return format.format(calendar.getTime());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        Bundle bundle = getArguments();
        int year = bundle.getInt("year",c.get(Calendar.YEAR));
        int month = bundle.getInt("month",c.get(Calendar.MONTH));
        int day = bundle.getInt("day",c.get(Calendar.DAY_OF_MONTH));
        caller = bundle.getInt("caller",-1);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mListener!=null) {
            mListener.onDateInteraction(view,caller);
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
        void onDateInteraction(DatePicker datePicker,int caller);
    }
}
