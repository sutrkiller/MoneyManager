package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Stat page encapsulates all data needed for single RecycleView item
 *
 * @author Tobias Kamenicky
 * @date 6/9/2016.
 */
public class StatPage {
    private String startBalance;
    private String endBalance;
    private String incomes;
    private String expenses;
    private String date;
    private Date start;
    private Date end;
    private List<Record> incomesList;
    private List<Record> expensesList;
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

    public List<Record> getIncomesList() {
        return incomesList;
    }

    public List<Record> getExpensesList() {
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

        if (cursor != null && cursor.moveToFirst())
        {
            BigDecimal tmpEndValue = BigDecimal.ZERO;
            BigDecimal tmpIncValue = BigDecimal.ZERO;
            BigDecimal tmpExpValue = BigDecimal.ZERO;
            do
            {
                Long id = cursor.getLong(0);
                BigDecimal value = new BigDecimal(cursor.getDouble(1));
                BigDecimal valueInEur = new BigDecimal(cursor.getDouble(7));
                Currency currency = Currency.getInstance(cursor.getString(2));
                String dateTime = cursor.getString(3);
                String item = cursor.getString(4);
                String catName = cursor.getString(5);
                String catDet = cursor.getString(6);
                Record record = new Record(id, value,valueInEur,currency,item,dateTime,new Category(0, catName,catDet));

                if (record.getValue().compareTo(BigDecimal.ZERO) <0)
                {
                    expensesList.add(record);
                    tmpExpValue = tmpExpValue.add(record.getValueInEur());
                } else
                {
                    incomesList.add(record);
                    tmpIncValue = tmpIncValue.add(record.getValueInEur());
                }

                tmpEndValue = tmpEndValue.add(record.getValueInEur());

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

        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy",Locale.getDefault()); //datepicker_date_format
        switch (tabNum) {
            case 0:
                date = dateFormat.format(fromDate)+ " - " + dateFormat.format(toDate);
                break;
            case 1:
                dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.getDefault());
                date = dateFormat.format(fromDate);
                break;
            case 2:
                dateFormat = new SimpleDateFormat("yyyy",Locale.getDefault());
                date = dateFormat.format(fromDate);
                break;
        }
    }
}
