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
import android.widget.Spinner;

/**
 * Dialog for filtering categories
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 5/15/2016.
 */
public class FilterCategoriesDialog extends DialogFragment {
    public static final int ORDER_NAME = 0;
    public static final int ORDER_DETAILS = 1;
    public static final int DIRECTION_ASC = 0;
    public static final int DIRECTION_DESC = 1;
    private View v;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_filter_categories, null);
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
                        Spinner spinner = (Spinner) v.findViewById(R.id.filterCategories_orderBy);
                        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterCategories_direction);

                        FilterCategoriesDialogFinishedListener fc = (FilterCategoriesDialogFinishedListener) getActivity();
                        if (fc == null) throw new NullPointerException("The class does not implement FilterCategoriesDialogFinishedListener");
                        fc.onFilterCategoriesFinishedDialog(true, spinner.getSelectedItemPosition(), spinner1.getSelectedItemPosition());
                        dialog.dismiss();

                    }
                });
                Button resetButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                resetButton.setTextColor(ContextCompat.getColor(getContext(), R.color.neutralColor));// getResources().getColor(R.color.neutralColor));
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetFilter();
                        FilterCategoriesDialogFinishedListener fc = (FilterCategoriesDialogFinishedListener) getActivity();
                        fc.onFilterCategoriesFinishedDialog(false, -1, -1);
                        dialog.dismiss();
                    }
                });
            }
        });

        Bundle args = getArguments();
        int orderPos =0;
        int directionPos = 0;
        if (args != null) {
            orderPos = args.getInt("categories_order_by", ORDER_NAME);
            directionPos = args.getInt("categories_direction", DIRECTION_ASC);
        }
        String[] attr = getResources().getStringArray(R.array.categories_sort_by);
        Spinner spinner = (Spinner) v.findViewById(R.id.filterCategories_orderBy);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, attr);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(orderPos, false);

        String[] dirs = new String[]{"Ascending", "Descending"};
        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterCategories_direction);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, dirs);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(directionPos, false);

        return dialog;
    }


    private void resetFilter() {
        Spinner spinner = (Spinner) v.findViewById(R.id.filterCategories_orderBy);
        Spinner spinner1 = (Spinner) v.findViewById(R.id.filterCategories_direction);

        spinner.setSelection(ORDER_NAME, false);
        spinner1.setSelection(DIRECTION_ASC, false);
    }

    public interface FilterCategoriesDialogFinishedListener {
        void onFilterCategoriesFinishedDialog(boolean result, int orderPos, int directionPos);
    }
}
