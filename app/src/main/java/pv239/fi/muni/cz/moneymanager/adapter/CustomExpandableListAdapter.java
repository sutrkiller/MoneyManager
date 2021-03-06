package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.helper.GraphCreator;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Adapter for expandable list on each statistics page
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 06/10/2016
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mHeaders;
    private Map<String, List<Record>> mDataList;
    private StatPage mPage;
    private boolean mEnabledGraph;

    public CustomExpandableListAdapter(Context context, List<String> headers, Map<String,List<Record>> dataList,boolean enableGraph, StatPage page) {
        mContext = context;
        mHeaders = headers;
        mDataList = dataList;
        mPage = page;
        mEnabledGraph = enableGraph;
        if (mEnabledGraph) {
            mHeaders.add(0,"Graph");
        }
    }

    @Override
    public int getGroupCount() {
        return mHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mEnabledGraph && groupPosition ==0) {
            return 1;
        }
        return mDataList.get(mHeaders.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mHeaders.get(groupPosition);
    }

    @Override
    public Record getChild(int groupPosition, int childPosition) {
        return mEnabledGraph && groupPosition==0 ? null : mDataList.get(mHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return  childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stats_group_header,parent,false);
        }
        TextView lblHeader = (TextView) convertView.findViewById(R.id.lblGroup_header);
        TextView lblSum = (TextView) convertView.findViewById(R.id.lblGroup_sum);

        if (mEnabledGraph && groupPosition==0) {
            lblHeader.setText(getGroup(groupPosition));
            lblSum.setText("");
            return convertView;
        } else {
            String header = getGroup(groupPosition) + "   (" + getChildrenCount(groupPosition) + ")";
            lblHeader.setText(header);
            BigDecimal sum = BigDecimal.ZERO;
            for (Record r : mDataList.get(getGroup(groupPosition))) {
                sum = sum.add(r.getValueInEur());
            }
            NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            curFor.setMaximumFractionDigits(2);
            lblSum.setText(curFor.format(Double.valueOf(String.valueOf(sum))));
            if (sum.compareTo(BigDecimal.ZERO) <= 0) {
                lblSum.setTextColor(ContextCompat.getColor(mContext, R.color.recordNegativeValue));
            } else {
                lblSum.setTextColor(ContextCompat.getColor(mContext, R.color.recordPositiveValue));
            }
            return convertView;
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (mEnabledGraph && groupPosition==0) {
            return getGraphView(convertView, parent);
        } else {
            final Record record = getChild(groupPosition, childPosition);
            return getRecordView(parent, record);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @NonNull
    private View getGraphView(View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mPage.getVersion()==0) {
            convertView = inflater.inflate(R.layout.line_graph_layout,parent,false);
            LineChart chart = (LineChart) convertView.findViewById(R.id.graph_new);
            GraphCreator.createNewLineChart(mContext, chart,mPage);
        } else if (mPage.getVersion()==1) {
            convertView = inflater.inflate(R.layout.bar_graph_layout,parent,false);
            BarChart chart = (BarChart) convertView.findViewById(R.id.graph_new);
            GraphCreator.createNewBarChart(mContext, chart,mPage);
        }

        TextView start = (TextView) convertView.findViewById(R.id.graph_start);
        TextView end = (TextView) convertView.findViewById(R.id.graph_end);
        //Chart chart = (Chart) convertView.findViewById(R.id.graph_new);

        NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        curFor.setMaximumFractionDigits(2);
        end.setText(curFor.format(Double.valueOf(mPage.getEndBalance())));
        start.setText(curFor.format(Double.valueOf(mPage.getStartBalance())));
        return convertView;
    }

    @NonNull
    private View getRecordView(ViewGroup parent, Record record) {
        View convertView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.stats_group_item, parent, false);
        TextView vdate = (TextView) convertView.findViewById(R.id.stats_item_date);
        TextView vvalue = (TextView) convertView.findViewById(R.id.stats_item_value);
        TextView vitem = (TextView) convertView.findViewById(R.id.stats_item_item);
        TextView vcategoryName = (TextView) convertView.findViewById(R.id.stats_item_category_name);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("EUR"));

        vdate.setText(Record.formatDateTime(mContext, record.dateTime));
        vvalue.setText(format.format(record.valueInEur.abs().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        vvalue.setTextColor(ContextCompat.getColor(mContext, record.valueInEur.compareTo(BigDecimal.ZERO) < 0 ? R.color.recordNegativeValue : R.color.recordPositiveValue));
        vitem.setText(record.item);
        vitem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        String name = record.category.name + ((record.category.details != null && !record.category.details.isEmpty()) ?
                " (" + record.category.details + ")" : "");
        vcategoryName.setText(name);

        return convertView;
    }
}
