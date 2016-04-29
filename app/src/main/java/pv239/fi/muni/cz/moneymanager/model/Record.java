package pv239.fi.muni.cz.moneymanager.model;

import android.content.Context;
import android.widget.DatePicker;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public Record(long id, BigDecimal value, Currency currency, String item,  String dateTime, Category category) {
        this.category = category;
        this.currency = currency;
        this.dateTime = dateTime;
        this.id = id;
        this.value = value;
        this.item = item;
    }

    public static List<Record> getTestingData() {
        ArrayList<Record> records = new ArrayList<>();

        Category cardKb  = new Category(0,"Card","KB");
        Category cardCSOB  = new Category(1,"Card","CSOB");
        Category cash  = new Category(2,"Cash","");

//        records.add(new Record(0,cardKb,Currency.getInstance(Locale.US),"2016-05-17 12:12:12",0,BigDecimal.valueOf(-500.5F),"Shoes"));
//        records.add(new Record(1,cardCSOB,Currency.getInstance(Locale.GERMANY),"2016-05-18 12:12:12",0,BigDecimal.valueOf(-50.5F),"Baker"));
//        records.add(new Record(2,cash,Currency.getInstance(Locale.UK),"2016-05-20 12:12:12",0,BigDecimal.valueOf(-5000.5F),"Smartphone"));
//        records.add(new Record(3,cash,Currency.getInstance(Locale.US),"2016-06-17 12:12:12",0,BigDecimal.valueOf(500.5F),"Salary"));
//        records.add(new Record(4,cardKb,Currency.getInstance(Locale.UK),"2016-10-17 12:12:12",0,BigDecimal.valueOf(1000.5F),"Bonus"));
//        records.add(new Record(5,cardCSOB,Currency.getInstance(Locale.FRANCE),"2017-02-17 12:12:12",0,BigDecimal.valueOf(-49256.5F),"New car"));

        return records;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
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

    public static String formatDateTime(Context context, Date date) {

        String finalDateTime = "";


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

        return finalDateTime;
    }
}
