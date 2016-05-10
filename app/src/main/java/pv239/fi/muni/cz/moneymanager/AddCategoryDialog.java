package pv239.fi.muni.cz.moneymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.Category;

/**
 * Dialog for adding new cateogory
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class AddCategoryDialog extends DialogFragment  {
    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_category,null);
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
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView vname = (TextView) v.findViewById(R.id.addCategory_name);

                        String name = String.valueOf(((TextView) v.findViewById(R.id.addCategory_name)).getText());
                        String details = String.valueOf(((TextView) v.findViewById(R.id.addCategory_details)).getText());

                        if (vname.getText().length() == 0) {
                            vname.setError("Name must not be empty");
                        } else {
                            Category category = new Category(0, name, details);
                            MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
                            long id = helper.addOrUpdateCategory(category);

                            AddCategoryDialogFinishedListener ac = (AddCategoryDialogFinishedListener) getActivity();
                            ac.onAddCategoryFinishedDialog(true);
                            dialog.dismiss();
                        }
                    }
                    });
                }
            });




        return dialog;
    }

    public interface AddCategoryDialogFinishedListener {
        void onAddCategoryFinishedDialog(boolean result);
    }

}
