package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.StatRecord;

/**
 * Created by Klasovci on 6/9/2016.
 */
public class StatPage {
    private String startBalance, endBalance, incomes, expenses,date;
    private Date start;
    private Date end;
    private List<StatRecord> incomesList;
    private List<StatRecord> expensesList;
    private Context context;

    public StatPage(Context context, Date fromDate, Date toDate) {
        this.context = context;
        incomesList = new ArrayList<>();
        expensesList = new ArrayList<>();
        start = fromDate;
        end = toDate;
        MMDatabaseHelper db = MMDatabaseHelper.getInstance(context);
        getData(db, fromDate, toDate);
    }
/*
    public StatPage(String startBalance, String endBalance, String incomes) {
        this.startBalance = startBalance;
        this.endBalance = endBalance;
        this.incomes = incomes;
        incomesList = new ArrayList<>();
        expensesList = new ArrayList<>();
    }
*/

    public Date getEnd() {
        return end;
    }

    public Date getStart() {
        return start;
    }

    public String getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(String startBalance) {
        this.startBalance = startBalance;
    }

    public String getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(String endBalance) {
        this.endBalance = endBalance;
    }

    public String getDate() {
        return date;
    }

    public String getIncomes() {
        return incomes;
    }

    public void setIncomes(String incomes) {
        this.incomes = incomes;

    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;

    }

    public List<StatRecord> getIncomesList() {
        return incomesList;
    }

    public void setIncomesList(List<StatRecord> incomesList) {
        this.incomesList = incomesList;

    }

    public List<StatRecord> getExpensesList() {
        return expensesList;
    }

    public void setExpensesList(List<StatRecord> expensesList) {
        this.expensesList = expensesList;

    }

    public void getData(MMDatabaseHelper db, Date fromDate, Date toDate)
    {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
        date = dateFormat.format(fromDate)+ " - " + dateFormat.format(toDate);
    }
}
