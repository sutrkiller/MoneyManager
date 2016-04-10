package pv239.fi.muni.cz.moneymanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobias on 4/7/2016.
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

    public static List<Category> getTestingCategories() {
        List<Category> list = new ArrayList<>();

        list.add(new Category(0, "Cash",""));
        list.add(new Category(1, "Card","KB"));
        list.add(new Category(2, "Card","CSOB"));
        list.add(new Category(3, "Card","Ceska Sporitelna"));

        return list;
    }
}
