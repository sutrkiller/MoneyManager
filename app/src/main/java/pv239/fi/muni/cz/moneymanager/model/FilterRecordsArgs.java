package pv239.fi.muni.cz.moneymanager.model;

import java.util.Date;

/**
 * Parameters to object list for FilterRecords
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 5/27/2016.
 */

public class FilterRecordsArgs {
    private int orderBy;
    private int orderDir;
    private Date dateFrom;
    private Date dateTo;

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public int getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(int orderBy) {
        this.orderBy = orderBy;
    }

    public int getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(int orderDir) {
        this.orderDir = orderDir;
    }
}
