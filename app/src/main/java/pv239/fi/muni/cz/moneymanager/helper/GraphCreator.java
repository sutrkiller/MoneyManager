package pv239.fi.muni.cz.moneymanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.ss.usermodel.charts.ChartData;
import org.joda.time.DateTime;
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



    private static class MyYAxisFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            if ((long)value<0) return "-"+getFormattedValue(-value,yAxis);
            LargeValueFormatter formatter = new LargeValueFormatter();
            return formatter.getFormattedValue((long)value,yAxis);
        }
    }
    public static void createNewBarChart(Context context, BarChart chart, StatPage page) {
        chart.setDescription("");
        chart.setBackgroundColor(Color.WHITE);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);
        leftAxis.setValueFormatter(new MyYAxisFormatter());
        leftAxis.setZeroLineWidth(2f);
        leftAxis.setDrawZeroLine(true);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        BarData data = getBarChartData(context,page);
        chart.setData(data);

        List<Integer> colors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        final int red = ContextCompat.getColor(context,R.color.recordNegativeValue);
        final int green = ContextCompat.getColor(context,R.color.recordPositiveValue);
        colors.add(red);
        colors.add(green);
        labels.add("Expenses");
        labels.add("Incomes");
        chart.getLegend().setCustom(colors,labels);
    }

    private static BarData getBarChartData(Context context, StatPage page) {
        List<Record> inc = page.getIncomesList();
        List<Record> exp = page.getExpensesList();
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (page.getTabNum()==2) {
            inFormat = new SimpleDateFormat("yyyy-MM");
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("MM/dd");
        if (page.getTabNum()==2) {
            outFormat = new SimpleDateFormat("MM/yy");
        }
        List<List<Record>> lists = new ArrayList<>();
        lists.add(inc);
        lists.add(exp);
        SortedMap<String, BigDecimal> map = prepareData(page,lists, inFormat);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        DateTime time = new DateTime(page.getStart());
        DateTime end = new DateTime(page.getEnd());
        do {
            xVals.add(outFormat.format(time.toDate()));
            switch (page.getTabNum()) {
                case 0:
                case 1: time = time.plusDays(1); break;
                case 2: time = time.plusMonths(1); break;
            }
        } while (time.isBefore(end));

        List<Integer> colors = new ArrayList<>();
        int red = ContextCompat.getColor(context,R.color.recordNegativeValue);
        int green = ContextCompat.getColor(context,R.color.recordPositiveValue);
        try {

            for(Map.Entry<String,BigDecimal> entry : map.entrySet()) {
                String formatedDate = outFormat.format(inFormat.parse(entry.getKey()));
                Float tmpBalance = Float.parseFloat(String.valueOf(entry.getValue()));
                BarEntry e = new BarEntry(tmpBalance,xVals.indexOf(formatedDate));
                if (tmpBalance<=0) colors.add(red);
                else colors.add(green);
                entries.add(e);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BarDataSet set = new BarDataSet(entries,"Activity in €");
        set.setValueTextSize(9f);
        set.setColors(colors);
        set.setValueFormatter(new ValueFormatter() {
            StackedValueFormatter formatter = new StackedValueFormatter(true,"",0);
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if ((long)value == 0) {
                    return "";
                }
                return formatter.getFormattedValue(value,entry,dataSetIndex,viewPortHandler);
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        BarData data = new BarData(xVals,dataSets);
        return data;
    }

    public static void createNewLineChart(Context context, LineChart chart, StatPage page) {
        chart.setDescription("");
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);
        leftAxis.setValueFormatter(new MyYAxisFormatter());

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(true);
        //xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineData data = getLineChartData(context, page);
        chart.setData(data);
        float min = data.getYMin();
        float max = data.getYMax();
        if (Math.signum(max)*Math.signum(min) > 0) {
            if(Math.signum(max) > 0) {
                leftAxis.setAxisMinValue(0);
                leftAxis.setAxisMaxValue(max*2);
            }
            else if (Math.signum(max)<0){
                leftAxis.setAxisMaxValue(0);
                leftAxis.setAxisMinValue(min*2);
            }
        }

        //chart.animateX(3000);
        //chart.invalidate();

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

    private static LineData getLineChartData(Context context, StatPage page) {
        List<Record> inc = page.getIncomesList();
        List<Record> exp = page.getExpensesList();
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (page.getTabNum()==2) {
            inFormat = new SimpleDateFormat("yyyy-MM");
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("MM/dd");
        if (page.getTabNum()==2) {
            outFormat = new SimpleDateFormat("MM/yy");
        }
        List<List<Record>> lists = new ArrayList<>();
        lists.add(inc);
        lists.add(exp);
        SortedMap<String, BigDecimal> map = prepareData(page,lists, inFormat);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        DateTime time = new DateTime(page.getStart());
        DateTime end = new DateTime(page.getEnd());
        do {
            xVals.add(outFormat.format(time.toDate()));
            switch (page.getTabNum()) {
                case 0:
                case 1: time = time.plusDays(1); break;
                case 2: time = time.plusMonths(1); break;
            }
        } while (time.isBefore(end));

        try {
            Float tmpBalance = Float.valueOf(page.getStartBalance());
            for(Map.Entry<String,BigDecimal> entry : map.entrySet()) {
                String formatedDate = outFormat.format(inFormat.parse(entry.getKey()));
                tmpBalance += Float.parseFloat(String.valueOf(entry.getValue()));
                Entry e = new Entry(tmpBalance,xVals.indexOf(formatedDate));
                entries.add(e);
            }
        } catch (ParseException e) {
                e.printStackTrace();
        }

        LineDataSet set = new LineDataSet(entries,"Balance in €");
        set.setColor(ContextCompat.getColor(context,R.color.graphLineColor));
        set.setCircleColor(ContextCompat.getColor(context,R.color.graphCircleColor));
        set.setLineWidth(3f);
        set.setCircleRadius(4f);
        set.setCircleHoleRadius(2f);
        set.setCircleColorHole(ContextCompat.getColor(context,R.color.graphCircleHoleColor));
        set.setDrawCircleHole(true);
        set.setValueTextSize(9f);
        //set.setDrawFilled(true);
        set.setFillColor(ContextCompat.getColor(context,R.color.graphFill));
        //set.setFillDrawable(ContextCompat.getDrawable(context,R.drawable.black_box));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(xVals,dataSets);

        return data;
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
            private boolean firstSet = false;
            private boolean lastSet = false;
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd", Locale.getDefault());
                    if (tab==2) {
                        format = new SimpleDateFormat("MMM",Locale.getDefault());
                    }
                    String strVal = format.format(new Date((long) value));
                    String strFir = format.format(new Date((long) series.getLowestValueX()));
                    String strLas = format.format(new Date((long) series.getHighestValueX()));
                    if (strVal.equals(strFir) && !firstSet) {
                        firstSet = true;
                        return strVal;
                    } else if (strVal.equals(strLas) && !lastSet) {
                        lastSet = true;
                        return strVal;
                    }
                    return "";
                } else {
                    return super.formatLabel(value, false) + " €";
                }
            }
        };

        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        int horizontalLabels =7;
        switch (tab) {
            case  0: horizontalLabels = 7; break;
            case 1: horizontalLabels = 4; break;
            case 2: horizontalLabels = 12; break;
        }
        graph.getGridLabelRenderer().setNumHorizontalLabels(horizontalLabels+1);

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);
        series.setThickness(6);

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
            private boolean firstSet = false;
            private boolean lastSet = false;
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd",Locale.getDefault());
                    if (tab==2) {
                        format = new SimpleDateFormat("MMM",Locale.getDefault());
                    }
                    String strVal = format.format(new Date((long) value));
                    String strFir = format.format(new Date((long) series.get(0).getLowestValueX()));
                    String strLas = format.format(new Date((long) series.get(0).getHighestValueX()));
                    if (strVal.equals(strFir) && !firstSet) {
                        firstSet = true;
                        return strVal;
                    } else if (strVal.equals(strLas) && !lastSet) {
                        lastSet = true;
                        return strVal;
                    }
                    return "";
                } else {
                    return super.formatLabel(value, false) + " €";
                }
            }
        };

        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        graph.getGridLabelRenderer().setNumHorizontalLabels(horizontalLabels+1); // only 3 because of the space


        graph.removeAllSeries();
        double minY =0;
        double maxY =0;
        for (BarGraphSeries<DataPoint> serie : series) {
            graph.addSeries(serie);
            minY = Math.min(minY,serie.getLowestValueY());
            maxY = Math.max(maxY,serie.getHighestValueY());
        }
        graph.getViewport().setXAxisBoundsManual(true);
        long twelveHInMs = 12*60*60*1000;
        Date start = new Date((long) series.get(0).getLowestValueX()-twelveHInMs);
        Date end = new Date((long) series.get(0).getHighestValueX()+twelveHInMs-1);
        if (tab ==2 ) {
            DateTime time = new DateTime(end.getTime());
            time = time.plusMonths(1);
//            Calendar calTmp = Calendar.getInstance();
//            calTmp.setTime(end);
//            calTmp.add(Calendar.MONTH,1);
//            calTmp.add(Calendar.DAY_OF_YEAR,-1);
            end = time.toDate();
        }
        Log.i("LowestX", String.valueOf(start.getTime()));
        Log.i("HieghestX", String.valueOf(end.getTime()));

        graph.getViewport().setMinX(start.getTime());
        graph.getViewport().setMaxX(end.getTime());


        Log.i("ALowestX", String.valueOf( (long)graph.getViewport().getMinX(false)));
        Log.i("AHieghestX", String.valueOf((long)graph.getViewport().getMaxX(false)));

        double max = Math.max(Math.abs(minY),Math.abs(maxY));
        if (max<100) {
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(-100);
            graph.getViewport().setMaxY(100);
        }
        //}


    }
}
