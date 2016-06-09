package pv239.fi.muni.cz.moneymanager.model;

/**
 * Category model
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class Category {
    public long id;
    public String name;
    public String details;

    public Category(long id, String name,String details) {
        this.details = details;
        this.id = id;
        this.name = name;
    }

}
