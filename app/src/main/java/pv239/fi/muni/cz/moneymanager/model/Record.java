package pv239.fi.muni.cz.moneymanager.model;

import android.content.Context;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pv239.fi.muni.cz.moneymanager.MainActivity;
import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.helper.ExchangeRateCalculator;

/**
 * Category model
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */

public class Record {
    public long id;
    public BigDecimal value;
    public Currency currency;
    public Category category;
    public String dateTime;
    public String item;
    public BigDecimal valueInEur;

//    public Record(long id, BigDecimal value, Currency currency, String item,  String dateTime, Category category) {
//
//        this(id,value,ExchangeRateCalculator.transferRate(currency, Currency.getInstance("EUR"),value),currency,item,dateTime,category);
//    }

    public Record(long id, BigDecimal value, BigDecimal valueInEur, Currency currency, String item,  String dateTime, Category category) {
        this.category = category;
        this.currency = currency;
        this.dateTime = dateTime;
        this.id = id;
        this.value = value;
        this.item = item;
        this.valueInEur =  valueInEur;
    }

    public long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValueInEur() {
        return valueInEur;
    }

    public void setValueInEur(BigDecimal valueInEur) {
        this.valueInEur = valueInEur;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(context.getString(R.string.db_date_format), Locale.getDefault());

        Date date;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()); //db_date_format
        try {
           return format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
