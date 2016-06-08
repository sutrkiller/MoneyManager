package pv239.fi.muni.cz.moneymanager.TabFragments;

/**
 * Created by Klasovci on 6/3/2016.
 */
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbToStatsAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;

public class PageFragment extends Fragment {
    private int pageNumber;
    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page_number", page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return (fragment);
    }

    public PageFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt("page_number");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.tab_fragment, container, false);
        //Get all the views you need for your page, using
        //layout.findViewById();

        //Then you will need to call processChanges for that page
        processChanges(layout, pageNumber);
        return (layout);
    }

    public void processChanges(View tabView, int tabNum)
    {
        int d =7;
        int m =0;
        int y =0;
        MMDatabaseHelper sloh = MMDatabaseHelper.getInstance(getActivity());

        if (tabNum == 1)
        {
            d =0;
            m =1;
            y =0;
        }
        else if (tabNum == 2)
        {
            d =0;
            m =0;
            y =1;
        }
        createGraph(tabView, d, m, y, sloh);
        setBalances(tabView, d, m, y, sloh);
        setListValues(tabView, d, m, y, sloh);
    }

    private void setListValues(View tabView, int d, int m, int y,MMDatabaseHelper sloh) {
        // Creating incomes list
        ListView incomeListView = (ListView) tabView.findViewById(R.id.listViewIncomeStats);
        Cursor incomeRecords = sloh.getRecordsInRange(">",d,m,y);
        RecordsDbToStatsAdapter incomeAdapter = new RecordsDbToStatsAdapter(this.getContext(), incomeRecords, 0);
        incomeListView.setAdapter(incomeAdapter);

        // Creating expenses list
        ListView expensesListView = (ListView) tabView.findViewById(R.id.listViewExpences);
        Cursor expensesRecords = sloh.getRecordsInRange("<",d,m,y);
        RecordsDbToStatsAdapter expensesAdapter = new RecordsDbToStatsAdapter(this.getContext(), expensesRecords, 0);
        expensesListView.setAdapter(expensesAdapter);
    }


    private Date numbersToDate(int daysBack, int monthsBack, int yearsBack)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysBack *(-1));
        cal.add(Calendar.MONTH, monthsBack *(-1));
        cal.add(Calendar.YEAR, yearsBack *(-1));
        return  cal.getTime();
    }

    private void setBalances(View tabView, int d, int m, int y, MMDatabaseHelper sloh) {
        //Fetching Values of incomes and expenses and balances
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);

        TextView startBal = (TextView) tabView.findViewById(R.id.startBalance);
        BigDecimal startValue = new BigDecimal(sloh.getStartingBal(numbersToDate(d,m,y)).toString());
        startBal.setText(String.valueOf(startValue.setScale(2).doubleValue()));

        TextView endBal = (TextView) tabView.findViewById(R.id.endBalance);
        BigDecimal endValue = new BigDecimal(sloh.getEndingBal().toString());
        endBal.setText(String.valueOf(endValue.setScale(2).doubleValue()));

        TextView incSum = (TextView) tabView.findViewById(R.id.incomeSumStats);
        Integer helpInc = sloh.getSumRecordsInRange(">",d,m,y);
        BigDecimal incValue = new BigDecimal(helpInc.toString());
        incSum.setText(String.valueOf(incValue.setScale(2).doubleValue()));

        TextView expSum = (TextView) tabView.findViewById(R.id.expenseSumStats);
        Integer helpExp = sloh.getSumRecordsInRange("<",d,m,y);
        BigDecimal expValue = new BigDecimal(helpExp.toString());
        expSum.setText(String.valueOf(expValue.setScale(2).doubleValue()));
    }

    private void createGraph(View tabView, int d, int m, int y, MMDatabaseHelper sloh) {
        // Graph rendering section
        // Cursor graphCursor = sloh.getRecordsInRange(null,d,m,y);
        // graphCursor.moveToFirst();
        int days=0;
        if (d == 7) { days = d;}
        if (m == 1) { days = 28;}
        if (y == 1) { days = 365;}

        ArrayList numList = new ArrayList();
        GraphView graph = (GraphView) tabView.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] { });

        Calendar day = Calendar.getInstance();
        day.set(Calendar.HOUR_OF_DAY,0);
        day.set(Calendar.MINUTE,0);
        day.set(Calendar.SECOND,0);

        for(int i=days; i>0;i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR,-i);
            day.setTime(cal.getTime());
            Log.i("TIME",cal.getTime().toString());
            numList.add(sloh.getCurrentBalanceForOneDay(cal.getTime()));
            series.appendData(new DataPoint(day.getTime(),sloh.getCurrentBalanceForOneDay(cal.getTime())),true,days);
        }

        Log.i("Pole", numList.toString());

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

        graph.addSeries(series);
    }

}
