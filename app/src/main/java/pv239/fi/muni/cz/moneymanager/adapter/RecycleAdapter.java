package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Adapter for RecycleView that holds each statistics page (swiping left and right)
 *
 * Created by Tobias Kamenicky 6/8/2016.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.CustomViewHolder> {
    private Context mContext;
    private List<StatPage> statPages;



    public RecycleAdapter(List<StatPage> list) {
        statPages = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup v,int i) {

       View view = LayoutInflater.from(v.getContext()).inflate(R.layout.tab_fragment,v,false);

       mContext = v.getContext();
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        StatPage page = statPages.get(i);
        NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        curFor.setMaximumFractionDigits(2);

        customViewHolder.date.setText(page.getDate());

        List<String> headers = new ArrayList<>();
        headers.add("Incomes");
        headers.add("Expenses");
        Map<String,List<Record>> dataMap = new HashMap<>();
        dataMap.put(headers.get(0),page.getIncomesList());
        dataMap.put(headers.get(1),page.getExpensesList());
        CustomExpandableListAdapter adapter_main = new CustomExpandableListAdapter(mContext,headers,dataMap,true,page);
        //StatsExpandableListAdapter adapter_main = new StatsExpandableListAdapter(mContext,page,true);
        customViewHolder.expandableListView.setAdapter(adapter_main);
        customViewHolder.expandableListView.expandGroup(0);

    }

    @Override
    public int getItemCount() {
        return statPages.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView date;
        protected ExpandableListView expandableListView;

        public CustomViewHolder(View view) {
            super(view);
            this.date = (TextView) view.findViewById(R.id.date);
            expandableListView = (ExpandableListView) view.findViewById(R.id.stats_expandableList);
        }
    }


}

