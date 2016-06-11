package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Tobias on 6/10/2016.
 */
public class ActionSpinnerAdapter<T> extends ArrayAdapter<T> {

    public ActionSpinnerAdapter(Context ctx, T[] objects)
    {
        super(ctx, android.R.layout.simple_spinner_item,objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);

        TextView text = (TextView)view.findViewById(android.R.id.text1);
        text.setTextColor(Color.WHITE);
        return view;
    }
}
