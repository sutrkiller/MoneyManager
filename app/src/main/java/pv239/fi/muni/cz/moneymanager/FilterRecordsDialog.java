package pv239.fi.muni.cz.moneymanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import pv239.fi.muni.cz.moneymanager.helper.DatePickerFragment;

/**
 * Dialog for filtering records
 *
 * Created by Tobias on 5/15/2016.
 */
public class FilterRecordsDialog extends DialogFragment {
    public static final int ORDER_AMOUNT = 0;
    public static final int ORDER_DATE = 1;
    public static final int ORDER_NAME = 2;
    public static final int ORDER_CATEGORY = 3;
    public static final int DIRECTION_ASC = 0;
    public static final int DIRECTION_DESC = 1;
    private View v;
    private int lastButtonClicked = -1;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_filter_records,null);
        final AlertDialog dialog = builder.setView(v)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNeutralButton("Reset filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do filter
                        final Button dateFrom = (Button)v.findViewById(R.id.filterRecords_date_from);
                        final Button dateTo = (Button)v.findViewById(R.id.filterRecords_date_to);
                        Spinner spinner = (Spinner) v.findViewById(R.id.filterRecords_orderBy);
                        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterRecords_direction);

                        Date date1=DatePickerFragment.stringToDate( getActivity(),(String) dateFrom.getText());
                        Date date2= DatePickerFragment.stringToDate(getActivity(),(String) dateTo.getText());
                        if (date1!= null && date2!=null) {
                            if (date1.compareTo(date2) > 0) {
                                Toast.makeText(getActivity(),"First date must be sooner.",Toast.LENGTH_LONG).show();
                            } else {
                                FilterRecordsDialogFinishedListener fc = (FilterRecordsDialogFinishedListener) getActivity();
                                fc.onFilterRecordsFinishedDialog(true,spinner.getSelectedItemPosition(),spinner1.getSelectedItemPosition(),date1,date2);
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(),"There was an error getting date",Toast.LENGTH_LONG).show();
                        }


                    }
                });
                Button resetButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                resetButton.setTextColor(ContextCompat.getColor(getContext(), R.color.neutralColor));
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetFilter();
                        FilterRecordsDialogFinishedListener fc = (FilterRecordsDialogFinishedListener) getActivity();
                        fc.onFilterRecordsFinishedDialog(false,-1,-1,null,null);
                        dialog.dismiss();
                    }
                });
            }
        });

        Bundle args = getArguments();
        int orderPos=1;
        int directionPos = 1;
        if (args!=null) {
            orderPos = args.getInt("records_order_by",ORDER_DATE);
            directionPos = args.getInt("records_direction",DIRECTION_DESC);
        }
        String[] attr = getResources().getStringArray(R.array.records_sort_by);
        Spinner spinner = (Spinner) v.findViewById(R.id.filterRecords_orderBy);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, attr);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(orderPos,false);

        String[] dirs = new String[] {"Ascending","Descending"};
        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterRecords_direction);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, dirs);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(directionPos,false);

        final Button dateFrom = (Button)v.findViewById(R.id.filterRecords_date_from);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        DatePicker picker = new DatePicker(getActivity());
        if (args!= null) {
            Date date = DatePickerFragment.stringToDate( getActivity(),args.getString("records_date_from",null));
            if (date!= null) {
                calendar.setTime(date);
            }
        }
        picker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dateFrom.setText(DatePickerFragment.formatDate(getActivity(),calendar));
        dateFrom.setTag(picker);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                DatePicker date = (DatePicker) dateFrom.getTag();
                if (date!=null) {
                    bundle.putInt("year",date.getYear());
                    bundle.putInt("month", date.getMonth());
                    bundle.putInt("day", date.getDayOfMonth());

                }
                bundle.putInt("caller",DatePickerFragment.ORDER_RECORDS);
                lastButtonClicked=1;
                newFragment.setArguments(bundle);
                newFragment.show(getChildFragmentManager(),"datePicker");
            }
        });

        final Button dateTo = (Button)v.findViewById(R.id.filterRecords_date_to);
        Calendar calendar1 = Calendar.getInstance();
        DatePicker picker1 = new DatePicker(getActivity());
        if (args!= null) {
            Date date = DatePickerFragment.stringToDate(getActivity(),args.getString("records_date_to",null));
            if (date!= null) {
                calendar1.setTime(date);
            }
        }
        picker1.updateDate(calendar1.get(Calendar.YEAR),calendar1.get(Calendar.MONTH),calendar1.get(Calendar.DAY_OF_MONTH));
        dateTo.setTag(picker1);
        dateTo.setText(DatePickerFragment.formatDate(getActivity(),Calendar.getInstance()));
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                DatePicker date = (DatePicker) dateTo.getTag();
                if (date!=null) {
                    bundle.putInt("year",date.getYear());
                    bundle.putInt("month", date.getMonth());
                    bundle.putInt("day", date.getDayOfMonth());

                }
                bundle.putInt("caller",DatePickerFragment.ORDER_RECORDS);
                lastButtonClicked=2;
                newFragment.setArguments(bundle);
                newFragment.show(getChildFragmentManager(),"datePicker");
            }
        });
        return dialog;
    }

    public void setDateButtonTag(DatePicker date) {
        int buttonId = lastButtonClicked == 1 ? R.id.filterRecords_date_from : R.id.filterRecords_date_to;
        Button button = (Button)v.findViewById(buttonId);
        DatePickerFragment.setDateButtonTag(getActivity(),date,button);

    }

    private void resetFilter() {
        Spinner spinner = (Spinner) v.findViewById(R.id.filterRecords_orderBy);
        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterRecords_direction);
        final Button dateFrom = (Button)v.findViewById(R.id.filterRecords_date_from);
        final Button dateTo = (Button)v.findViewById(R.id.filterRecords_date_to);

        spinner.setSelection(ORDER_DATE,false);
        spinner1.setSelection(DIRECTION_DESC,false);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        DatePicker picker = new DatePicker(getActivity());
        picker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dateFrom.setText(DatePickerFragment.formatDate(getActivity(),calendar));
        dateFrom.setTag(picker);
        Calendar calendar1 = Calendar.getInstance();
        DatePicker picker1 = new DatePicker(getActivity());
        picker.updateDate(calendar1.get(Calendar.YEAR),calendar1.get(Calendar.MONTH),calendar1.get(Calendar.DAY_OF_MONTH));
        dateTo.setText(DatePickerFragment.formatDate(getActivity(),calendar1));
        dateTo.setTag(picker1);
    }

    public interface FilterRecordsDialogFinishedListener {
        void onFilterRecordsFinishedDialog(boolean result,int orderPos,int directionPos, Date from, Date to);
    }

}
