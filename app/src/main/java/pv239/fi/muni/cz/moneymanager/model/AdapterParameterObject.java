package pv239.fi.muni.cz.moneymanager.model;

/**
 * Created by Tobias on 6/11/2016.
 */
public class AdapterParameterObject {
    private int pageNumber;
    //private int daysBack;
    private int version;

    public AdapterParameterObject(int pageNumber,int daysBack,int version) {
        this.pageNumber = pageNumber;
        //this.daysBack = daysBack;
        this.version = version;
    }

//    public int getDaysBack() {
//        return daysBack;
//    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdapterParameterObject that = (AdapterParameterObject) o;

        if (pageNumber != that.pageNumber) return false;
        //if (daysBack != that.daysBack) return false;
        return version == that.version;

    }

    @Override
    public int hashCode() {
        int result = pageNumber;
        //result = 31 * result + daysBack;
        result = 31 * result + version;
        return result;
    }
}
