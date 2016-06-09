package pv239.fi.muni.cz.moneymanager.model;

import android.content.Context;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

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

    public Record(long id, BigDecimal value, Currency currency, String item,  String dateTime, Category category) {
        this(id,value,ExchangeRateCalculator.TransferRate(currency, Currency.getInstance("EUR"),value),currency,item,dateTime,category);
    }

    public Record(long id, BigDecimal value, BigDecimal valueInEur, Currency currency, String item,  String dateTime, Category category) {
        this.category = category;
        this.currency = currency;
        this.dateTime = dateTime;
        this.id = id;
        this.value = value;
        this.item = item;

        this.valueInEur =  valueInEur == null ? BigDecimal.ZERO : valueInEur;
    }


    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

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
                //flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

}
