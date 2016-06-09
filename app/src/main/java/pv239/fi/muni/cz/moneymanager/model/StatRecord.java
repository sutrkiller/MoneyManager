package pv239.fi.muni.cz.moneymanager.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Simplified record model for use in statistics
 *
 * Created by Klasovci on 6/9/2016.
 */
public class StatRecord {
    public BigDecimal value;
    public long id;
    public String item;
    public Date date;

    public StatRecord(BigDecimal value, String item, long id, Date date) {
        this.item = item;
        this.value = value;
        this.id = id;
        this.date = date;
    }

    public Date getDate()
    {
        return date;
    }
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
