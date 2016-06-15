package pv239.fi.muni.cz.moneymanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        ArrayList<BarEntry> entries = new ArrayList<>();

        List<Record> inc = page.getIncomesList();
        List<Record> exp = page.getExpensesList();
        List<List<Record>> lists = new ArrayList<>();
        lists.add(inc);
        lists.add(exp);

        SimpleDateFormat inFormat = new SimpleDateFormat(context.getString(R.string.yyyy_MM_dd),Locale.getDefault());
        if (page.getTabNum()==2) {
            inFormat = new SimpleDateFormat(context.getString(R.string.yyyy_MM),Locale.getDefault());
        }
        SimpleDateFormat outFormat = new SimpleDateFormat(context.getString(R.string.MM_dd),Locale.getDefault());
        if (page.getTabNum()==2) {
            outFormat = new SimpleDateFormat(context.getString(R.string.MM_yy),Locale.getDefault());
        }

        SortedMap<String, BigDecimal> map = prepareData(page,lists, inFormat);

        ArrayList<String> xVals = getXValues(page, outFormat);

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
        return new BarData(xVals,dataSets);
    }

    @NonNull
    private static ArrayList<String> getXValues(StatPage page, SimpleDateFormat outFormat) {
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
        return xVals;
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
    }

    private static LineData getLineChartData(Context context, StatPage page) {
        List<Record> inc = page.getIncomesList();
        List<Record> exp = page.getExpensesList();
        List<List<Record>> lists = new ArrayList<>();
        lists.add(inc);
        lists.add(exp);

        SimpleDateFormat inFormat = new SimpleDateFormat(context.getString(R.string.yyyy_MM_dd),Locale.getDefault());
        if (page.getTabNum()==2) {
            inFormat = new SimpleDateFormat(context.getString(R.string.yyyy_MM),Locale.getDefault());
        }


        SortedMap<String, BigDecimal> map = prepareData(page,lists, inFormat);
        SimpleDateFormat outFormat = new SimpleDateFormat(context.getString(R.string.MM_dd),Locale.getDefault());
        if (page.getTabNum()==2) {
            outFormat = new SimpleDateFormat(context.getString(R.string.MM_yy),Locale.getDefault());
        }
        ArrayList<String> xVals = getXValues(page, outFormat);

        ArrayList<Entry> entries = new ArrayList<>();
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
        set.setFillColor(ContextCompat.getColor(context,R.color.graphFill));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        return new LineData(xVals,dataSets);
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

    private static class MyYAxisFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            if ((long)value<0) return "-"+getFormattedValue(-value,yAxis);
            LargeValueFormatter formatter = new LargeValueFormatter();
            return formatter.getFormattedValue((long)value,yAxis);
        }
    }
}
