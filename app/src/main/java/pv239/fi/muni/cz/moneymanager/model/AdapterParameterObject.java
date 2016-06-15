package pv239.fi.muni.cz.moneymanager.model;

/**
 * Parameter object for RecyclePage
 * @author Tobias Kamenicky
 * @date 6/11/2016.
 */
public class AdapterParameterObject {
    private int pageNumber;
    private int version;

    public AdapterParameterObject(int pageNumber, int version) {
        this.pageNumber = pageNumber;
        this.version = version;
    }

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
        return version == that.version;
    }

    @Override
    public int hashCode() {
        int result = pageNumber;
        result = 31 * result + version;
        return result;
    }
}
