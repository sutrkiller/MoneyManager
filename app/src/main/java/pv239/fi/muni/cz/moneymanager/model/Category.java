package pv239.fi.muni.cz.moneymanager.model;

/**
 * Created by Tobias on 4/7/2016.
 */
public class Category {
    public long id;
    public String name;
    public String details;

    public Category(String details, long id, String name) {
        this.details = details;
        this.id = id;
        this.name = name;
    }
}
