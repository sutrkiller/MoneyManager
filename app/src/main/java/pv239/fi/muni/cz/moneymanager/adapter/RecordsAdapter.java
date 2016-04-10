package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Created by Tobias on 4/7/2016.
 */
public class RecordsAdapter extends BaseAdapter {
    private List<Record> records;
    private LayoutInflater inflater;
    private Context context;

    public RecordsAdapter(Context context,List<Record> records ) {
        this.records = records;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Record getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return records.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_record,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Record record = getItem(position);

        int iconId = record.value.compareTo(BigDecimal.ZERO) < 0 ? R.drawable.ic_remove_circle_outline_black_24dp : R.drawable.ic_add_circle_outline_black_24dp;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        format.setCurrency(record.currency);

        holder.icon.setImageResource(iconId);
        holder.date.setText(Record.formatDateTime(context,record.dateTime));
        holder.value.setText(format.format(record.value.abs().setScale(2).doubleValue()));
        holder.value.setTextColor(ContextCompat.getColor(context,record.value.compareTo(BigDecimal.ZERO) < 0 ? R.color.recordNegativeValue : R.color.recordPositiveValue));
        //holder.currency.setText(record.currency.getSymbol());
        holder.item.setText(record.item);
        holder.item.setTextColor(ContextCompat.getColor(context,R.color.black));
        holder.categoryName.setText(record.category.name + ((record.category.details != null && !record.category.details.isEmpty()) ? " ("+record.category.details+")" : ""));
        //holder.categoryDetails.setText(record.category.details);
        return convertView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView date;
        public TextView value;
        //public TextView currency;
        public TextView item;
        public TextView categoryName;
        //public TextView categoryDetails;


        private ViewHolder(View view) {
            this.icon = (ImageView) view.findViewById(R.id.record_item_icon);
            this.date = (TextView) view.findViewById(R.id.record_item_date);
            this.value = (TextView) view.findViewById(R.id.record_item_value);
            //this.currency = (TextView) view.findViewById(R.id.record_item_currency);
            this.item = (TextView) view.findViewById(R.id.record_item_item);
            this.categoryName = (TextView) view.findViewById(R.id.record_item_category_name);
            //this.categoryDetails = (TextView) view.findViewById(R.id.record_item_category_details);

        }
    }
}
