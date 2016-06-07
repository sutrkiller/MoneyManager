package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.DashPathEffect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.adapter.ExpensesSumStatsAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.PagerAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbToStatsAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.IncomeSumStatsAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;

import android.support.v7.app.AppCompatActivity;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import javax.sql.DataSource;

/**
 * Fragment holding all statistics about data.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class StatsFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView incomeListView;
    private ListView expensesListView;

    private OnStatsInteractionListener mListener;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        StatsFragment fragment = new StatsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("7 days"));
        tabLayout.addTab(tabLayout.newTab().setText("1 month"));
        tabLayout.addTab(tabLayout.newTab().setText("1 year"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final CustomViewPager viewPager = (CustomViewPager) getView().findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //   TextView txt = (TextView) (adapter.getItem(tab.getPosition())).getView().findViewById(R.id.startMonthStats);

                processChanges((adapter.getItem(tab.getPosition())).getView(),tab.getPosition());
               /* switch (tab.getPosition()){
                    case 0:
                        txt.setText("HELLO WORLD to 1");
                        break;
                    case 1:
                        txt.setText("HELLO WORLD to 2");
                        break;
                    case 2:
                        txt.setText("HELLO WORLD to 3");
                        break;
                    default:
                        Log.i("POS",String.valueOf(tab.getPosition()));
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
             /*   TextView txt = (TextView) (adapter.getItem(tab.getPosition())).getView().findViewById(R.id.startMonthStats);
                switch (tab.getPosition()) {
                    case 0:
                        txt.setText("Reselected on 1");
                        break;
                    case 1:
                        txt.setText("Reselected on 2");
                        break;
                    case 2:
                        txt.setText("Reselected on 3");
                        break;
                    default:
                        Log.i("POS", String.valueOf(tab.getPosition()));
                }*/
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onStatsInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStatsInteractionListener) {
            mListener = (OnStatsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStatsInteractionListener {
        // TODO: Update argument type and name
        void onStatsInteraction(Uri uri);
    }

    private Date numbersToDate(int daysBack, int monthsBack, int yearsBack)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysBack *(-1));
        cal.add(Calendar.MONTH, monthsBack *(-1));
        cal.add(Calendar.YEAR, yearsBack *(-1));
        return  cal.getTime();
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
        incomeListView = (ListView) tabView.findViewById(R.id.listViewIncomeStats);
        Cursor incomeRecords = sloh.getRecordsInRange(">",d,m,y);
        RecordsDbToStatsAdapter incomeAdapter = new RecordsDbToStatsAdapter(this.getContext(), incomeRecords, 0);
        incomeListView.setAdapter(incomeAdapter);

        // Creating expenses list
        expensesListView = (ListView) tabView.findViewById(R.id.listViewExpences);
        Cursor expensesRecords = sloh.getRecordsInRange("<",d,m,y);
        RecordsDbToStatsAdapter expensesAdapter = new RecordsDbToStatsAdapter(this.getContext(), expensesRecords, 0);
        expensesListView.setAdapter(expensesAdapter);
    }

    private void setBalances(View tabView, int d, int m, int y, MMDatabaseHelper sloh) {
        //Fetching Values of incomes and expenses and balances
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);

        TextView incSum = (TextView) tabView.findViewById(R.id.incomeSumStats);
        Integer helpInc = sloh.getSumRecordsInRange(">",d,m,y);
        BigDecimal incValue = new BigDecimal(helpInc.toString());
        incSum.setText(format.format(incValue.abs().setScale(2).doubleValue()));

        TextView expSum = (TextView) tabView.findViewById(R.id.expenseSumStats);
        Integer helpExp = sloh.getSumRecordsInRange("<",d,m,y);
        BigDecimal expValue = new BigDecimal(helpExp.toString());
        expSum.setText(format.format(expValue.abs().setScale(2).doubleValue()));

        TextView startBal = (TextView) tabView.findViewById(R.id.startBalance);
        BigDecimal startValue = new BigDecimal(sloh.getStartingBal(numbersToDate(d,m,y)).toString());
        startBal.setText(format.format(startValue.abs().setScale(2).doubleValue()));

        TextView endBal = (TextView) tabView.findViewById(R.id.endBalance);
        BigDecimal endValue = new BigDecimal(sloh.getEndingBal().toString());
        endBal.setText(format.format(endValue.abs().setScale(2).doubleValue()));


        TextView actState = (TextView) tabView.findViewById(R.id.actualSumStats);
        actState.setText(format.format(new BigDecimal(helpInc + helpExp).setScale(2).doubleValue()));
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
