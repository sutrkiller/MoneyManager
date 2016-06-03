package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Created by Klas on 5/31/2016.
 */
public class IncomeSumStatsAdapter extends CursorAdapter {

    private LayoutInflater inflater;


    public IncomeSumStatsAdapter(Context context, Cursor c, int flags) {
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

        TextView fincome = (TextView) view.findViewById(R.id.incomeSumStats);

        fincome.setText(cursor.getString(1));
        Log.i("Hello worls",null);
    }
}
