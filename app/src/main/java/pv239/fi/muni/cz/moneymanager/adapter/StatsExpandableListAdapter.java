package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.helper.GraphCreator;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Created by Tobias on 6/13/2016.
 */
public class StatsExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mMainHeaders = new ArrayList<>();
    private Map<String,List<String>> mSubHeaders = new HashMap<>();
    private StatPage mPage;
    private Map<String,Map<String,List<Record>>> mData = new HashMap<>();
    private boolean mCreateGraph;

    private Map<String,BigDecimal> _sums = new HashMap<>();
    private Map<String,Map<String,BigDecimal>> _subSums = new HashMap<>();

    public StatsExpandableListAdapter(Context mContext,StatPage page, boolean createGraph) {
        this.mContext = mContext;
        this.mPage = page;
        this.mCreateGraph = createGraph;

        if (page.getVersion()==0) prepareDataForBalance();
        else if (page.getVersion()==1) prepareDataForActivity();
    }

    private void prepareDataForBalance() {

        int intervals = 0;
        int groupBy = Calendar.DAY_OF_MONTH;
        switch (mPage.getTabNum()) {
            case 0: intervals = 7; groupBy=Calendar.DAY_OF_MONTH; break;
            case 1: intervals = 4; groupBy=Calendar.WEEK_OF_YEAR; break;
            case 2: intervals = 12; groupBy=Calendar.MONTH; break;
        }

        List<Date> intervalLimits = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(mPage.getStart());
        for (int i=0;i<intervals;++i) {
            Date before = cal.getTime();
            cal.add(groupBy,1);

            DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            String header = dateFormat.format(before)+ " - " + dateFormat.format(cal.getTime());
            mMainHeaders.add(header);
            List<String> subHeaders = new ArrayList<>();
            subHeaders.add("Incomes");
            subHeaders.add("Expenses");
            mSubHeaders.put(header,subHeaders);
            Log.i("StatsExpListAdapter","Header: "+header);

            intervalLimits.add(cal.getTime());
            Map<String, List<Record>> innerMap = new HashMap<>();
            innerMap.put("Incomes",new ArrayList<Record>());
            innerMap.put("Expenses",new ArrayList<Record>());
            mData.put(header,innerMap);
            //records.add(new ArrayList<Record>());
        }
//        List<Record> connected = mPage.getIncomesList();
//        connected.addAll(mPage.getExpensesList());
//        Collections.sort(connected, new Comparator<Record>() {
//            @Override
//            public int compare(Record lhs, Record rhs) {
//                return lhs.getDate().compareTo(rhs.getDate());
//            }
//        });
        for(Record rec : mPage.getIncomesList()) {
            for (int i=0;i<intervals;++i) {
                Date date = rec.getDate();
                if (date!= null && date.before(intervalLimits.get(i))) {
                    //records.get(i).add(rec);
                    mData.get(mMainHeaders.get(i)).get("Incomes").add(rec);
                    break;
                }
            }
        }
        for(Record rec : mPage.getExpensesList()) {
            for (int i=0;i<intervals;++i) {
                Date date = rec.getDate();
                if (date!= null && date.before(intervalLimits.get(i))) {
                    //records.get(i).add(rec);
                    mData.get(mMainHeaders.get(i)).get("Expenses").add(rec);
                    break;
                }
            }
        }

        //count sums of all underlying records
        BigDecimal sum = BigDecimal.ZERO;
        for (String header : mMainHeaders) {
            Map<String,BigDecimal> innerMap = new HashMap<String, BigDecimal>();
            for (String subHeader : mSubHeaders.get(header)) {
                for (Record rec : mData.get(header).get(subHeader)) {
                    sum = sum.add(rec.getValueInEur());
                }
                innerMap.put(subHeader,sum);
            }
            _subSums.put(header,innerMap);
            _sums.put(header, sum);
        }

        if (mCreateGraph) {
            mMainHeaders.add(0,"Graph");
        }
    }

    private void prepareDataForActivity() {

    }



    @Override
    public int getGroupCount() {
        return mMainHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (getGroup(groupPosition).equals("Graph")) return 1;
        return mSubHeaders.get(getGroup(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mMainHeaders.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mSubHeaders.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
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

        if (getGroup(groupPosition).equals("Graph")) {
            lblHeader.setText(getGroup(groupPosition));
            lblSum.setText("");
            return convertView;
        } else {
            int num = 0;
            for(String subHeader : mSubHeaders.get(getGroup(groupPosition))) {
                num+=mData.get(getGroup(groupPosition)).get(subHeader).size();
            }
            lblHeader.setText(getGroup(groupPosition) + "   (" + num + ")");

            NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            curFor.setMaximumFractionDigits(2);
            BigDecimal sum = _sums.get(getGroup(groupPosition));
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
        if (mMainHeaders.get(groupPosition).equals("Graph")) {
            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_graph_layout,parent,false);
            //}
            GraphView graph = (GraphView) convertView.findViewById(R.id.graph_main);
            TextView start = (TextView) convertView.findViewById(R.id.graph_start);
            TextView end = (TextView) convertView.findViewById(R.id.graph_end);
            GraphCreator.createGraph(mContext,graph,mPage);
            NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            curFor.setMaximumFractionDigits(2);
            end.setText(curFor.format(Double.valueOf(mPage.getEndBalance())));
            start.setText(curFor.format(Double.valueOf(mPage.getStartBalance())));

            return convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stats_group_header,parent,false);

            TextView lblHeader = (TextView) convertView.findViewById(R.id.lblGroup_header);
            TextView lblSum = (TextView) convertView.findViewById(R.id.lblGroup_sum);

            int num = mData.get(getGroup(groupPosition)).get(getChild(groupPosition,childPosition)).size();
            lblHeader.setText(getChild(groupPosition,childPosition) + "   (" + num + ")");
            NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            curFor.setMaximumFractionDigits(2);
            BigDecimal sum = _subSums.get(getGroup(groupPosition)).get(getChild(groupPosition,childPosition));
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
