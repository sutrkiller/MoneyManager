package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Created by Klas on 5/31/2016.
 */
public class ExpensesSumStatsAdapter extends CursorAdapter {

    private LayoutInflater inflater;


    public ExpensesSumStatsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rView = inflater.inflate(R.layout.list_item_statistics,parent,false);
        return rView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView fexpense = (TextView) view.findViewById(R.id.expenseSumStats);

        fexpense.setText(cursor.getString(1));
    }
}
