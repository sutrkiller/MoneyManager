package pv239.fi.muni.cz.moneymanager.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Created by Tobias on 6/12/2016.
 *
 */
public class GraphCreator {

    public static void createGraph(Context context, GraphView graph, StatPage page) {
        if (page.getVersion()==0) {
            LineGraphSeries<DataPoint> series = getDataPointLineGraphSeries(page);
            createLinerGraph(graph, series, page.getTabNum());
        } else  if (page.getVersion()==1) {
            BarGraphSeries<DataPoint> series1 = getDataPointBarGraphSeries(page,page.getIncomesList());
            BarGraphSeries<DataPoint> series2 = getDataPointBarGraphSeries(page,page.getExpensesList());
            series1.setColor(ContextCompat.getColor(context, R.color.recordPositiveValue));
            series2.setColor(ContextCompat.getColor(context,R.color.recordNegativeValue));
            List<BarGraphSeries<DataPoint>> list = new ArrayList<>();
            list.add(series1);
            list.add(series2);
            createBarGraph(graph,list,page.getTabNum());
        }
    }

    @NonNull
    private static BarGraphSeries<DataPoint> getDataPointBarGraphSeries(StatPage page, List<Record> list) {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (page.getTabNum()==2) {
            format = new SimpleDateFormat("yyyy-MM");
        }
        List<List<Record>> lists = new ArrayList<>();
        lists.add(list);
        SortedMap<String,BigDecimal> map = prepareData(page,lists,format);

        Calendar tmpCal = Calendar.getInstance();
        for (Map.Entry<String,BigDecimal> entry : map.entrySet()) {

            Date date = format.parse(entry.getKey(), new ParsePosition(0));
            tmpCal.setTime(date);
            tmpCal.add(Calendar.HOUR_OF_DAY,12);
            if (date!= null) {
                Double tmpBalance = Double.parseDouble(String.valueOf(entry.getValue()));
                DataPoint dataPoint = new DataPoint( tmpCal.getTime(), tmpBalance);
                series.appendData(dataPoint,true,map.size());
            }
        }
        return series;
    }

    @NonNull
    private static LineGraphSeries<DataPoint> getDataPointLineGraphSeries(StatPage page) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        List<Record> inc = page.getIncomesList();
        List<Record> exp = page.getExpensesList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (page.getTabNum()==2) {
            format = new SimpleDateFormat("yyyy-MM");
        }

        List<List<Record>> lists = new ArrayList<>();
        lists.add(inc);
        lists.add(exp);
        SortedMap<String, BigDecimal> map = prepareData(page,lists, format);

        Calendar tmpCal = Calendar.getInstance();
        Double tmpBalance = Double.valueOf(page.getStartBalance());
        for (Map.Entry<String,BigDecimal> entry : map.entrySet()) {

            Date date = format.parse(entry.getKey(), new ParsePosition(0));
            tmpCal.setTime(date);
            tmpCal.add(Calendar.HOUR_OF_DAY,12);
            if (date!= null) {

                tmpBalance += Double.parseDouble(String.valueOf(entry.getValue()));
                DataPoint dataPoint = new DataPoint( tmpCal.getTime(), tmpBalance);
                series.appendData(dataPoint,true,map.size());
            }
        }
        return series;
    }

    @NonNull
    private static SortedMap<String, BigDecimal> prepareData(StatPage page,List<List<Record>> lists, SimpleDateFormat format) {
//        List<StatRecord> inc = page.getIncomesList();
//        List<StatRecord> exp = page.getExpensesList();
        SortedMap<String,BigDecimal> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        if (page.getVersion()==1) {
            Date st = page.getStart();
            Date en = page.getEnd();
            LocalDate sD = LocalDate.fromDateFields(st);
            LocalDate sE = LocalDate.fromDateFields(en);
            int days = Days.daysBetween(sD, sE).getDays();

            for (int i = 0; i <= days; ++i) {
                LocalDate d = sD.withFieldAdded(DurationFieldType.days(), i);
                map.put(format.format(d.toDate()), BigDecimal.ZERO);
            }
        } else if (page.getVersion()==0) {
            String s = format.format(page.getStart());
            map.put(s, BigDecimal.ZERO);
            String e = format.format(page.getEnd());
            map.put(e, BigDecimal.ZERO);
        }


        for(List<Record> list : lists) {
            for (int k = 0;k<list.size();++k) {

                String a = null;
                try {
                    a = format.format(format.parse(list.get(k).getDateTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (map.containsKey(a)) {
                    BigDecimal tmp = map.get(a);
                    tmp = tmp.add(list.get(k).getValueInEur());
                    map.put(a,tmp);
                } else {
                    map.put(a,list.get(k).getValueInEur());
                }
            }
        }
        return map;
    }

    private static void createLinerGraph(GraphView graph, final LineGraphSeries<DataPoint> series, final int tab) {
        LabelFormatter formatter = new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd", Locale.getDefault());
                    if (tab==2) {
                        format = new SimpleDateFormat("MM/yy",Locale.getDefault());
                    }
                    String strVal = format.format(new Date((long) value));
                    String strFir = format.format(new Date((long) series.getLowestValueX()));
                    String strLas = format.format(new Date((long) series.getHighestValueX()));
                    if (!strVal.equals(strFir) && !strVal.equals(strLas)) return "";
                    return strVal;
                } else {
                    return super.formatLabel(value, false) + " €";
                }
            }
        };

        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        int horizontalLabels =2;
        switch (tab) {
            case  0: horizontalLabels = 7; break;
            case 1: horizontalLabels = 4; break;
            case 2: horizontalLabels = 1; break;
        }
        graph.getGridLabelRenderer().setNumHorizontalLabels(horizontalLabels+1); // only 3 because of the space

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);
        series.setThickness(6);

//            graph.getViewport().setYAxisBoundsManual(true);
//            graph.getViewport().setMaxY(40);
//            graph.getViewport().setMinY(-40);
//        }

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(series.getLowestValueX());
        graph.getViewport().setMaxX(series.getHighestValueX());
        graph.addSeries(series);

        double minY = graph.getViewport().getMinY(true);
        double maxY = graph.getViewport().getMaxY(true);
        double max = Math.max(Math.abs(series.getLowestValueY()),Math.abs(series.getHighestValueY()));
        if (max<100) {
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(-100);
            graph.getViewport().setMaxY(100);
        }
    }

    private static void createBarGraph(GraphView graph, final List<BarGraphSeries<DataPoint>> series, final int tab) {
        //for(final BarGraphSeries<DataPoint> serie : series) {
        int horizontalLabels =7;
        switch (tab) {
            case  0: horizontalLabels = 7; break;
            case 1: horizontalLabels = 4; break;
            case 2: horizontalLabels = 12; break;
        }

        LabelFormatter formatter = new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd",Locale.getDefault());
                    if (tab==2) {
                        format = new SimpleDateFormat("MM/yy",Locale.getDefault());
                    }
                    String strVal = format.format(new Date((long) value));
                    String strFir = format.format(new Date((long) series.get(0).getLowestValueX()));
                    String strLas = format.format(new Date((long) series.get(0).getHighestValueX()));
                    if (!strVal.equals(strFir) && !strVal.equals(strLas)) return "";
                    return strVal;
                } else {
                    return super.formatLabel(value, false) + " €";
                }
            }
        };

        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        graph.getGridLabelRenderer().setNumHorizontalLabels(horizontalLabels+1); // only 3 because of the space

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(series.get(0).getLowestValueX());
        graph.getViewport().setMaxX(series.get(0).getHighestValueX());
        graph.removeAllSeries();
        for (BarGraphSeries<DataPoint> serie : series) {
            graph.addSeries(serie);
        }

        //}


    }
}
