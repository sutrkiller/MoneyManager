package pv239.fi.muni.cz.moneymanager.model;

/**
 * Parameters to object list for FilterCategories
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 5/27/2016.
 */
public class FilterCategoriesArgs {
    private int orderBy;
    private int orderDir;

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
