package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.model.StatRecord;

/**
 * This class serves as adapter for each item in statistics expenses/incomes lists
 *
 * Created by Klas on 5/31/2016.
 */
public class RecordsDbToStatsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<StatRecord> recordsList;
    private Context context;

    public RecordsDbToStatsAdapter(Context context, List<StatRecord> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return recordsList.size();
    }

    @Override
    public StatRecord getItem(int position) {
        return recordsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recordsList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_statistics,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        StatRecord record = getItem(position);


        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate color based on a key (same key returns the same color), useful for list/grid views
        int color2 = generator.getColor(record.getItem());
// declare the builder object once.
        TextDrawable draw = TextDrawable.builder().buildRound(String.valueOf(record.getItem().charAt(0)).toUpperCase(), color2);
// reuse the builder specs to create multiple drawables
        holder.icon.setImageDrawable(draw);
        holder.item.setText(record.getItem());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        format.setMaximumFractionDigits(2);
        holder.value.setText(format.format(record.getValue().abs().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        holder.value.setTextColor(ContextCompat.getColor(context, record.getValue().compareTo(BigDecimal.ZERO) < 0 ? R.color.recordNegativeValue : R.color.recordPositiveValue));

        return convertView;
    }


    private class ViewHolder
    {
        public ImageView icon;
        public TextView item;
        public TextView value;

        public ViewHolder(View view)
        {
            item = (TextView) view.findViewById(R.id.statistics_item_name);
            value = (TextView) view.findViewById(R.id.statistics_item_value);
            icon = (ImageView) view.findViewById(R.id.statistics_item_icon);
        }


    }
}
