package pv239.fi.muni.cz.moneymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import pv239.fi.muni.cz.moneymanager.adapter.NothingSelectedSpinnerAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.helper.DatePickerFragment;
import pv239.fi.muni.cz.moneymanager.helper.ExchangeRateCalculator;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Dialog for adding new record
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class AddRecordDialog extends DialogFragment  {
    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_record,null);
        final AlertDialog dialog = builder.setView(v)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
/*
                        String item = String.valueOf(((TextView) v.findViewById(R.id.addRecord_item)).getText());
                        String amount = String.valueOf(((TextView) v.findViewById(R.id.addRecord_price)).getText());
                        Currency currency = Currency.getInstance(String.valueOf(((Spinner) v.findViewById(R.id.addRecord_currencies)).getSelectedItem()));
                        String category = String.valueOf(((Spinner) v.findViewById(R.id.addRecord_categories)).getSelectedItem());
                        DatePicker date = ((DatePicker) v.findViewById(R.id.addRecord_date).getTag());
                        Calendar cal = Calendar.getInstance();
                        cal.set(date.getYear(),date.getMonth(),date.getDayOfMonth());
                        Date d = cal.getTime();
                        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");

                        String finalDate = iso8601Format.format(d);

                        String[] parts= category.split(" ");
                        String catName = parts[0];
                        String catDet = "";
                        if (parts.length == 2) {
                            catDet = parts[1].substring(1,parts[1].length()-1);
                        }
//TODO: validate
                        Record rec = new Record(0, new BigDecimal(amount),currency,item,finalDate,new Category(0,catName,catDet));
                        MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
                        long id = helper.addRecord(rec);

                        AddRecordDialogFinishedListener ac = (AddRecordDialogFinishedListener) getActivity();
                        ac.onAddRecordFinishedDialog(true);
                        dialog.dismiss();*/
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView vitem = (TextView) v.findViewById(R.id.addRecord_item);
                        TextView vamount = (TextView) v.findViewById(R.id.addRecord_price);
                        Spinner scategory = (Spinner) v.findViewById(R.id.addRecord_categories);

                        if (vitem.getText().length()==0) {
                            vitem.setError("Item name cannot be empty");
                        } else if (vamount.getText().length()==0) {
                            vamount.setError("Amount cannot be empty");
                        } else if (scategory.getSelectedItemPosition()==0) {

                        } else {

                            String item = String.valueOf((vitem).getText());
                            String amount = String.valueOf((vamount).getText());
                            Currency currency = Currency.getInstance(String.valueOf(((Spinner) v.findViewById(R.id.addRecord_currencies)).getSelectedItem()));
                            String category = String.valueOf((scategory).getSelectedItem());
                            DatePicker date = ((DatePicker) v.findViewById(R.id.addRecord_date).getTag());
                            Calendar cal = Calendar.getInstance();
                            if (date!=null) {
                                cal.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
                            }
                            Date d = cal.getTime();
//                            SimpleDateFormat iso8601Format = new SimpleDateFormat(
//                                    "yyyy-MM-dd HH:mm:ss");
                            String finalDate = MMDatabaseHelper.convertDateForDb(d);

                            String[] parts = category.split(" ");
                            String catName = parts[0];
                            String catDet = "";
                            if (parts.length >= 2) {
                                for (int i=2;i<parts.length;++i) {
                                    parts[1] = parts[1].concat(" "+parts[i]);
                                }
                                catDet = parts[1].substring(1, parts[1].length() - 1);
                            }

                            Log.i("EXCHANGERATE: ", String.valueOf(ExchangeRateCalculator.TransferRate(currency, Currency.getInstance("EUR"),new BigDecimal(amount))));

                            Record rec = new Record(0, new BigDecimal(amount), currency, item, finalDate, new Category(0, catName, catDet));
                            MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
                            long id = helper.addRecord(rec);

                            AddRecordDialogFinishedListener ac = (AddRecordDialogFinishedListener) getActivity();
                            ac.onAddRecordFinishedDialog(true);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

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
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(eurPos);

        /* Categories spinner */
        //

        MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
        List<Category> cats = helper.getAllCategoriesAsList();

        //List<Category> cats = Category.getTestingCategories();
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
        dateButton.setText(DatePickerFragment.formatDate(Calendar.getInstance()));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                DatePicker date = (DatePicker) dateButton.getTag();
                if (date!=null) {
                    bundle.putInt("year", date.getYear());
                    bundle.putInt("month", date.getMonth());
                    bundle.putInt("day", date.getDayOfMonth());

                }
                bundle.putInt("caller",DatePickerFragment.ADD_RECORDS);
                newFragment.setArguments(bundle);

                newFragment.show(getChildFragmentManager(),"datePicker");
            }
        });

        return dialog;
    }


    public void setDateButtonTag(DatePicker date) {
        Button button = (Button)v.findViewById(R.id.addRecord_date);
        DatePickerFragment.setDateButtonTag(date,button);
        /*button.setTag(date);

        int day = date.getDayOfMonth();
        int month = date.getMonth();
        int year = date.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        DateFormat dateFormat = DateFormat.getInstance();
        dateFormat.setCalendar(calendar);

        DateFormat format = new SimpleDateFormat("EEE, MMM d,yyyy");
        String dateF = format.format(calendar.getTime());
        button.setText(dateF);*/
    }

    public interface AddRecordDialogFinishedListener {
        void onAddRecordFinishedDialog(boolean result);
    }

}
