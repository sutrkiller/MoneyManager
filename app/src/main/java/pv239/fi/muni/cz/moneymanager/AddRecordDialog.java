package pv239.fi.muni.cz.moneymanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
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
    private BigDecimal resultInEur;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_record,null);
        final AlertDialog dialog = builder.setView(v)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        } else if (scategory.getSelectedItemPosition() != 0) {

                            final String item = String.valueOf((vitem).getText());
                            final String amount = String.valueOf((vamount).getText());

                            final Currency currency = Currency.getInstance(String.valueOf(((Spinner) v.findViewById(R.id.addRecord_currencies)).getSelectedItem()));

                            String category = String.valueOf((scategory).getSelectedItem());
                            DatePicker date = ((DatePicker) v.findViewById(R.id.addRecord_date).getTag());
                            Calendar cal = Calendar.getInstance();
                            if (date != null) {
                                cal.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
                            }
                            Date d = cal.getTime();
                            final String finalDate = MMDatabaseHelper.convertDateForDb(d);

                            String[] parts = category.split(" ");
                            final String catName = parts[0];
                            String catDet = "";
                            if (parts.length >= 2) {
                                for (int i = 2; i < parts.length; ++i) {
                                    parts[1] = parts[1].concat(" " + parts[i]);
                                }
                                catDet = parts[1].substring(1, parts[1].length() - 1);
                            }
                            final String catDetFinal = catDet;



                            //get amount in EUR
                            final ExchangeRateCalculator erc = new ExchangeRateCalculator(v.getContext());
                            BigDecimal eurs = erc.transferRate(currency, Currency.getInstance("EUR"),new BigDecimal(amount));
                            if (eurs == null) {
                                if (erc.checkOlderRateDownloaded(currency,Currency.getInstance("EUR"))) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface d, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    resultInEur = erc.getOlderRateDownloaded(currency, Currency.getInstance("EUR")).multiply(new BigDecimal(amount));
                                                    Record rec = new Record(0, new BigDecimal(amount), resultInEur, currency, item, finalDate, new Category(0, catName, catDetFinal));
                                                    MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
                                                    helper.addRecord(rec);

                                                    AddRecordDialogFinishedListener ac = (AddRecordDialogFinishedListener) getActivity();
                                                    ac.onAddRecordFinishedDialog(true);
                                                    dialog.dismiss();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    Toast.makeText(v.getContext(),"Failed to download exchange rate, please use EUR as currency instead",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    };
                                    Date dt = erc.getOlderDateDownloaded(currency, Currency.getInstance("EUR"));
                                    Calendar cl = Calendar.getInstance();
                                    cl.setTime(dt);
                                    String oldDate = DatePickerFragment.formatDate(v.getContext(), cl);
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                                    builder1.setMessage("Unfortunately, downloading of current exchange rate failed.\nDo you want to use rate from "+oldDate+"?")
                                            .setPositiveButton("Yes",dialogClickListener)
                                            .setNegativeButton("No",dialogClickListener).show();
                                } else {
                                    Toast.makeText(v.getContext(),"Failed to download exchange rate, please use EUR as currency instead",Toast.LENGTH_LONG).show();
                                }


                            } else {
                                Record rec = new Record(0, new BigDecimal(amount), eurs, currency, item, finalDate, new Category(0, catName, catDet));
                                MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
                                helper.addRecord(rec);

                                AddRecordDialogFinishedListener ac = (AddRecordDialogFinishedListener) getActivity();
                                ac.onAddRecordFinishedDialog(true);
                                dialog.dismiss();
                            }
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
            } catch (Exception ignored) {
            }
        }
        String[] arr = toret.toArray(new String[toret.size()]);
        int eurPos = Arrays.asList(arr).indexOf("EUR");
        eurPos = eurPos > 0 ? eurPos : 0;

        Spinner spinner = (Spinner) v.findViewById(R.id.addRecord_currencies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, arr);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(eurPos);

        /* Categories spinner */
        //

        MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
        List<Category> cats = helper.getAllCategoriesAsList();
        SortedSet<String> sortedCats = new TreeSet<>();
        for(Category cat : cats) {
            try {
                sortedCats.add(cat.name + ((cat.details != null && !cat.details.isEmpty()) ? " ("+cat.details+")" : ""));
            } catch (Exception ignored) {
            }
        }

        Spinner categories = (Spinner)v.findViewById(R.id.addRecord_categories);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sortedCats.toArray(new String[sortedCats.size()]));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setPrompt("Select a category!");
        categories.setAdapter(new NothingSelectedSpinnerAdapter(adapter1,R.layout.contact_spinner_row_nothing_selected,getActivity()));

        final Button dateButton = (Button)v.findViewById(R.id.addRecord_date);
        dateButton.setText(DatePickerFragment.formatDate(getActivity(), Calendar.getInstance()));
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
        DatePickerFragment.setDateButtonTag(getActivity(), date,button);

    }

    public interface AddRecordDialogFinishedListener {
        void onAddRecordFinishedDialog(boolean result);
    }

}
