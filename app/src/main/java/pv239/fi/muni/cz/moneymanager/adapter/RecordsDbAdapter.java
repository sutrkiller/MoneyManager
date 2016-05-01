package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Adapter for @Record model
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 7/4/2016
 */
public class RecordsDbAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private View.OnTouchListener mListener;


    public RecordsDbAdapter(Context context, Cursor c, int flags, View.OnTouchListener mListenr) {
        super(context, c, flags);
        mListener = mListenr;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rView = inflater.inflate(R.layout.list_item_record,parent,false);
        rView.setOnTouchListener(mListener);
        return rView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        BigDecimal value = new BigDecimal(cursor.getDouble(1));
        Currency currency = Currency.getInstance(cursor.getString(2));
        String dateTime = cursor.getString(3);
        String item = cursor.getString(4);
        String catName = cursor.getString(5);
        String catDet = cursor.getString(6);

        ImageView vicon = (ImageView) view.findViewById(R.id.record_item_icon);
        TextView vdate = (TextView) view.findViewById(R.id.record_item_date);
        TextView vvalue = (TextView) view.findViewById(R.id.record_item_value);
        TextView vitem = (TextView) view.findViewById(R.id.record_item_item);
        TextView vcategoryName = (TextView) view.findViewById(R.id.record_item_category_name);

        int iconId = value.compareTo(BigDecimal.ZERO) < 0 ? R.drawable.ic_remove_circle_outline_black_24dp : R.drawable.ic_add_circle_outline_black_24dp;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        format.setCurrency(currency);

        vicon.setImageResource(iconId);
        vdate.setText(Record.formatDateTime(context,dateTime));
        vvalue.setText(format.format(value.abs().setScale(2).doubleValue()));
        vvalue.setTextColor(ContextCompat.getColor(context,value.compareTo(BigDecimal.ZERO) < 0 ? R.color.recordNegativeValue : R.color.recordPositiveValue));
        vitem.setText(item);
        vitem.setTextColor(ContextCompat.getColor(context,R.color.black));
        vcategoryName.setText(catName + ((catDet != null && !catDet.isEmpty()) ? " ("+catDet+")" : ""));
    }
}
