package pv239.fi.muni.cz.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pv239.fi.muni.cz.moneymanager.adapter.NothingSelectedSpinnerAdapter;
import pv239.fi.muni.cz.moneymanager.model.Category;

/**
 * Created by Tobias on 4/10/2016.
 */
public class AddRecordDialog extends DialogFragment implements DatePickerFragment.OnDateInteractionListener {
    View v;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_record,null);
        builder.setView(v)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: add item to database
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddRecordDialog.this.getDialog().cancel();
                    }
                });

        //Bundle arguments = getArguments();

        /* All currencies list */
        SortedSet<String> toret = new TreeSet<>();
        Locale[] locs = Locale.getAvailableLocales();
        for (Locale loc : locs) {
            try {
                toret.add(Currency.getInstance(loc).getCurrencyCode());
            } catch (Exception ex) {}
        }
        String[] arr = toret.toArray(new String[toret.size()]);
        int eurPos = Arrays.asList(arr).indexOf("EUR");
        eurPos = eurPos > 0 ? eurPos : 0;

        Spinner spinner = (Spinner) v.findViewById(R.id.addRecord_currencies);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,arr);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.currencies,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(eurPos);

        /* Categories spinner */
        //get categories here
        List<Category> cats = Category.getTestingCategories();
        SortedSet<String> sortedCats = new TreeSet<>();
        for(Category cat : cats) {
            try {
                sortedCats.add(cat.name + ((cat.details != null && !cat.details.isEmpty()) ? " ("+cat.details+")" : ""));
            } catch (Exception ex){}
        }

        Spinner categories = (Spinner)v.findViewById(R.id.addRecord_categories);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,sortedCats.toArray(new String[sortedCats.size()]));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setPrompt("Select a category!");
        categories.setAdapter(new NothingSelectedSpinnerAdapter(adapter1,R.layout.contact_spinner_row_nothing_selected,getActivity()));

        final Button dateButton = (Button)v.findViewById(R.id.addRecord_date);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
               //TODO: set arguments with date from button
                //the button can hold tag with current date
                Bundle bundle = new Bundle();
                DatePicker date = (DatePicker) dateButton.getTag();
                if (date!=null) {
                    bundle.putInt("year", date.getYear());
                    bundle.putInt("month", date.getMonth());
                    bundle.putInt("day", date.getDayOfMonth());
                }
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");

            }
        });

        return builder.create();

    }

    public void setDateButtonTag(DatePicker date) {
        Button button = (Button)v.findViewById(R.id.addRecord_date);
        button.setTag(date);

        int day = date.getDayOfMonth();
        int month = date.getMonth();
        int year = date.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        DateFormat dateFormat = DateFormat.getInstance();
        dateFormat.setCalendar(calendar);

        DateFormat format = new SimpleDateFormat("EEE, MMM d,yyyy");
        String dateF = format.format(calendar.getTime());
        button.setText(dateF);
    }

    @Override
    public void onDateInteraction(DatePicker datePicker) {
        if (v==null) return;
        //Button button = (Button) getActivity().findViewById(R.id.addRecord_date);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        DateFormat dateFormat = DateFormat.getInstance();
        dateFormat.setCalendar(calendar);

        DateFormat format = new SimpleDateFormat("EEE, MMM d,yyyy");
        String date = format.format(calendar.getTime());

        ((Button)v.findViewById(R.id.addRecord_date)).setText(date);


    }
}
