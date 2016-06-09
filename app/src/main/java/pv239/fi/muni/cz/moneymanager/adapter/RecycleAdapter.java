package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.model.StatRecord;

/**
 * Created by Klasovci on 6/8/2016.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.CustomViewHolder> {
    private Context mContext;
    private List<StatPage> statPages;

    public RecycleAdapter(Context context, List<StatPage> list) {
        //this.mContext = context;
        statPages = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup v,int i) {

       View view = LayoutInflater.from(v.getContext()).inflate(R.layout.tab_fragment,v,false);

       mContext = v.getContext();
       CustomViewHolder viewHolder = new CustomViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        StatPage page = statPages.get(i);
        NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        curFor.setMaximumFractionDigits(2);


        customViewHolder.date.setText(page.getDate());
        customViewHolder.endBalanceView.setText(curFor.format(Double.valueOf(page.getEndBalance())));
        customViewHolder.startBalanceView.setText(curFor.format(Double.valueOf(page.getStartBalance())));
        customViewHolder.incomesView.setText(curFor.format(Double.valueOf(page.getIncomes())));
        customViewHolder.expensesView.setText(curFor.format(Double.valueOf(page.getExpenses())));

        RecordsDbToStatsAdapter adapter1 = new RecordsDbToStatsAdapter(mContext,page.getIncomesList());
        customViewHolder.incomesListView.setAdapter(adapter1);

        RecordsDbToStatsAdapter adapter2 = new RecordsDbToStatsAdapter(mContext,page.getExpensesList());
        customViewHolder.expensesListView.setAdapter(adapter2);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        List<StatRecord> inc = page.getIncomesList();
        List<StatRecord> exp = page.getExpensesList();
        SortedMap<String,BigDecimal> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (int k = 0;k<inc.size();++k) {

            String a = format.format(inc.get(k).getDate());
            Log.i("Date: ", String.valueOf(a));
            if (map.containsKey(a)) {
                BigDecimal tmp = map.get(a);
                tmp = tmp.add(inc.get(k).getValue());
                map.put(a,tmp);
            } else {
                map.put(a,inc.get(k).getValue());
            }
        }
        for (int k = 0;k<exp.size();++k) {

            String a = format.format(exp.get(k).getDate());
            Log.i("Date: ", String.valueOf(a));
            if (map.containsKey(a)) {
                BigDecimal tmp = map.get(a);
                tmp = tmp.add(exp.get(k).getValue());
                map.put(a,tmp);
            } else {
                map.put(a,exp.get(k).getValue());
            }
        }


        String s = format.format(page.getStart());
        if (!map.containsKey(s)) {
            Date date = page.getStart();

            if (date!= null) {
                DataPoint dataPoint = new DataPoint(date, Double.parseDouble(page.getStartBalance()));
                series.appendData(dataPoint,true,map.size()+2);
            }
        }


        for (Map.Entry<String,BigDecimal> entry : map.entrySet()) {

            Date date = format.parse(entry.getKey(), new ParsePosition(0));

            if (date!= null) {
                DataPoint dataPoint = new DataPoint(date, Double.parseDouble(String.valueOf(entry.getValue()))+Double.valueOf(page.getStartBalance()));
                series.appendData(dataPoint,true,map.size()+2);
            }
        }

        String e = format.format(page.getEnd());
        if (!map.containsKey(e)) {
            Date date = page.getEnd();

            if (date!= null) {
                DataPoint dataPoint = new DataPoint(date, Double.parseDouble(page.getEndBalance()));
                series.appendData(dataPoint,true,map.size()+2);
            }
        }

        createGraph(customViewHolder.graphView,series);



    }

    private void createGraph(GraphView graph, LineGraphSeries<DataPoint> series) {


        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(mContext));
        graph.getGridLabelRenderer().setNumHorizontalLabels(2); // only 3 because of the space
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);
        series.setThickness(6);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(series.getLowestValueX());
        graph.getViewport().setMaxX(series.getHighestValueX());
        graph.addSeries(series);
    }

    @Override
    public int getItemCount() {
        return statPages.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected GraphView graphView;
        protected TextView startBalanceView;
        protected TextView endBalanceView;
        protected TextView incomesView;
        protected TextView expensesView;
        protected ListView incomesListView;
        protected ListView expensesListView;
        protected TextView date;


        public CustomViewHolder(View view) {
            super(view);
           // this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.graphView = (GraphView) view.findViewById(R.id.graph);
            this.startBalanceView = (TextView) view.findViewById(R.id.startBalance);
            this.endBalanceView = (TextView) view.findViewById(R.id.endBalance);
            this.incomesView = (TextView) view.findViewById(R.id.incomeSumStats);
            this.expensesView = (TextView) view.findViewById(R.id.expenseSumStats);
            this.incomesListView = (ListView) view.findViewById(R.id.listViewIncomeStats);
            this.expensesListView = (ListView) view.findViewById(R.id.listViewExpences);
            this.date = (TextView) view.findViewById(R.id.date);
        }
    }


}

