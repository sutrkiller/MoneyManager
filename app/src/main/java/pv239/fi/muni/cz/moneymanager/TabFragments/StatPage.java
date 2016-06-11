package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.StatRecord;

/**
 * Stat page encapsulates all data needed for single RecycleView item
 *
 * Created by Klasovci on 6/9/2016.
 */
public class StatPage {
    private String startBalance;
    private String endBalance;
    private String incomes;
    private String expenses;
    private String date;
    private Date start;
    private Date end;
    private List<StatRecord> incomesList;
    private List<StatRecord> expensesList;
    private int tabNum;
    private int version;

    public StatPage(Context context, Date fromDate, Date toDate, int tab,int ver) {
        incomesList = new ArrayList<>();
        expensesList = new ArrayList<>();
        start = fromDate;
        end = toDate;
        tabNum = tab;
        version = ver;
        MMDatabaseHelper db = MMDatabaseHelper.getInstance(context);
        getData(db, fromDate, toDate);
    }

    public Date getEnd() {
        return end;
    }

    public Date getStart() {
        return start;
    }

    public String getStartBalance() {
        return startBalance;
    }

    public String getEndBalance() {
        return endBalance;
    }

    public String getDate() {
        return date;
    }

    public String getIncomes() {
        return incomes;
    }

    public String getExpenses() {
        return expenses;
    }

    public List<StatRecord> getIncomesList() {
        return incomesList;
    }

    public List<StatRecord> getExpensesList() {
        return expensesList;
    }

    public int getTabNum() {
        return tabNum;
    }

    public int getVersion() {
        return version;
    }

    public void getData(MMDatabaseHelper db, Date fromDate, Date toDate)
    {
        switch (version) {
            case 0:
            case 1: getDataForFirst(db,fromDate,toDate); break;
        }
    }

    private void getDataForFirst(MMDatabaseHelper db, Date fromDate, Date toDate) {
        String from = MMDatabaseHelper.convertDateForDb(fromDate);
        String to = MMDatabaseHelper.convertDateForDb(toDate);
        Cursor cursor = db.getAllRecordsWithCategories(from, to, MMDatabaseHelper.KEY_REC_DATE, "ASC");
        BigDecimal currentBal = db.getStartingBal(fromDate);

        if (cursor.moveToFirst())
        {
            BigDecimal tmpEndValue = BigDecimal.ZERO;
            BigDecimal tmpIncValue = BigDecimal.ZERO;
            BigDecimal tmpExpValue = BigDecimal.ZERO;
            do
            {
                String stringDate = cursor.getString(3);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(stringDate,new ParsePosition(0));
                StatRecord record = new StatRecord(new BigDecimal(cursor.getDouble(7)), cursor.getString(4),cursor.getLong(0),date);
                if (record.getValue().compareTo(BigDecimal.ZERO) <0)
                {
                    expensesList.add(record);
                    tmpExpValue = tmpExpValue.add(record.getValue());
                } else
                {
                    incomesList.add(record);
                    tmpIncValue = tmpIncValue.add(record.getValue());
                }

                tmpEndValue = tmpEndValue.add(record.getValue());

            }while(cursor.moveToNext());

            startBalance = String.valueOf(currentBal);
            endBalance = String.valueOf(tmpEndValue.add(currentBal));
            incomes = String.valueOf(tmpIncValue);
            expenses = String.valueOf(tmpExpValue);

        } else {
            startBalance = String.valueOf(currentBal);
            endBalance = String.valueOf(currentBal);
            incomes = String.valueOf(BigDecimal.ZERO);
            expenses = String.valueOf(BigDecimal.ZERO);
        }
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        date = dateFormat.format(fromDate)+ " - " + dateFormat.format(toDate);
    }

    private void getDataForSecond(MMDatabaseHelper db, Date fromDate, Date toDate) {

    }
}
