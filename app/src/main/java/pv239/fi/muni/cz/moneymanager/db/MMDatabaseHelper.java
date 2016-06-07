package pv239.fi.muni.cz.moneymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Created by Tobias on 4/29/2016.
 */
public class MMDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_RECORD = "record";
    public static final String KEY_CAT_ID = "_id";
    public static final String KEY_CAT_NAME = "name";
    public static final String KEY_CAT_DET = "details";
    public static final String KEY_REC_ID = "_id";
    public static final String KEY_REC_VAL = "value";
    public static final String KEY_REC_CURR = "currency";
    public static final String KEY_REC_CAT_ID_FK = "categoryId";
    public static final String KEY_REC_DATE = "date";
    public static final String KEY_REC_ITEM = "item";
    private static final String DB_NAME = "money_manager";
    private static final int DB_VERSION = 1;
    private static MMDatabaseHelper sInstance;

    public MMDatabaseHelper(Context context) {
        this(context, null);
    }

    public MMDatabaseHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION); 
    }

    public static synchronized MMDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MMDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }



    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDb(db, 0, DB_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDb(db, oldVersion, newVersion);
    }

    public Record getRecordById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Record record = null;
        Category category = null;
        db.beginTransaction();
        try {
            String cols =MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_ID+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_DET;

            String recSelect = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = %s", cols, TABLE_RECORD+", "+TABLE_CATEGORY, MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID,MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CAT_ID_FK,MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_ID);
            Cursor cursor = db.rawQuery(recSelect,new String[] {String.valueOf(id)});
            try {
                if (cursor.moveToFirst()) {
                    category = new Category(cursor.getInt(5), cursor.getString(6), cursor.getString(7));
                    record = new Record(id, BigDecimal.valueOf(cursor.getDouble(1)), Currency.getInstance(cursor.getString(2)),cursor.getString(4),cursor.getString(3),category);
                    db.setTransactionSuccessful();
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        return record;
    }

    public Cursor getAllRecordsWithCategories() {
        return getAllRecordsWithCategories(null,null,null,null);
    }


    public Cursor getAllIncomesRecords()
    {
        SetDates setDates = new SetDates().invoke();
        Date startDate = setDates.getStartDate();
        Date endDate = setDates.getEndDate();

        String cols = MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME;
                ;

        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_VAL+">"+"0 AND "+TABLE_RECORD+"."+KEY_REC_CAT_ID_FK+"="+TABLE_CATEGORY+"."+KEY_CAT_ID+
                             " AND "+TABLE_RECORD+"."+KEY_REC_DATE+" BETWEEN '"+convertDateForDb(startDate)+ "' AND '"+ convertDateForDb(endDate)+"'";
        String query = "SELECT "+cols+
                " FROM "+TABLE_RECORD+ ", "+TABLE_CATEGORY+
                whereClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
        } catch (SQLiteException e) {

        } finally {

        } return cursor;

    }

    public Cursor getAllExpensesRecords()
    {
        SetDates setDates = new SetDates().invoke();
        Date startDate = setDates.getStartDate();
        Date endDate = setDates.getEndDate();

        String cols = MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME;

        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_VAL+" < "+"0 AND "+TABLE_RECORD+"."+KEY_REC_CAT_ID_FK+"="+TABLE_CATEGORY+"."+KEY_CAT_ID+
                             " AND "+TABLE_RECORD+"."+KEY_REC_DATE+" BETWEEN '"+convertDateForDb(startDate)+ "' AND '"+ convertDateForDb(endDate)+"'";
        String query = "SELECT "+cols+
                " FROM "+TABLE_RECORD+ ", "+TABLE_CATEGORY+
                whereClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
        } catch (SQLiteException e) {

        } finally {

        } return cursor;

    }

    public Integer getIncomesSum()
    {
        SetDates setDates = new SetDates().invoke();
        Date startDate = setDates.getStartDate();
        Date endDate = setDates.getEndDate();

       // String currentDate = convertDateForDb(new Date().setTime(22));
        String col = TABLE_RECORD+"."+KEY_REC_VAL;
        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_VAL+" >"+" 0 AND "+TABLE_RECORD+"."+KEY_REC_DATE+" BETWEEN '"+convertDateForDb(startDate)+ "' AND '"+ convertDateForDb(endDate)+"'";
        String query = "SELECT SUM("+col+
                ") FROM "+TABLE_RECORD+
                whereClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLiteException e) {
        } finally {
        } return 0;
    }

    public Integer getExpensesSum()
    {
        SetDates setDates = new SetDates().invoke();
        Date startDate = setDates.getStartDate();
        Date endDate = setDates.getEndDate();

        // String currentDate = convertDateForDb(new Date().setTime(22));
        String col = TABLE_RECORD+"."+KEY_REC_VAL;
        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_VAL+" <"+" 0 AND "+TABLE_RECORD+"."+KEY_REC_DATE+" BETWEEN '"+convertDateForDb(startDate)+ "' AND '"+ convertDateForDb(endDate)+"'";
        String query = "SELECT SUM("+col+
                ") FROM "+TABLE_RECORD+
                whereClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLiteException e) {
        } finally {
        } return 0;
    }

    public Cursor getAllRecordsWithCategories(String dateFrom, String dateTo, String orderBy, String orderDir) {
        String cols = MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_DET;


        String dateFromClause = "";
        if (isValidDateForDb(dateFrom))   {
            dateFromClause = TABLE_RECORD+"."+KEY_REC_DATE+" >= Datetime('"+dateFrom+"')";
        }
        String dateToClause = "";
        if (isValidDateForDb(dateTo)) {
            dateToClause = TABLE_RECORD+"."+KEY_REC_DATE+" <= Datetime('"+dateTo+"')";
        }
        String dateClause = dateFromClause.isEmpty() ? dateToClause : dateFromClause+ (dateToClause.isEmpty() ? "" : " AND "+dateToClause);
        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_CAT_ID_FK+"="+TABLE_CATEGORY+"."+KEY_CAT_ID+ (dateClause.isEmpty() ? "" : " AND "+dateClause);
        String orderByClause = " ORDER BY "+isValidOrderForDb(orderBy,TABLE_RECORD+"."+KEY_REC_DATE) + isValidDirectionForDb(orderDir," DESC");
        String query = "SELECT "+cols+
                        " FROM "+TABLE_RECORD+", "+TABLE_CATEGORY +
                        whereClause + orderByClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
        } catch (SQLiteException e) {

        } finally {

        } return cursor;

    }

    public static boolean isValidDateForDb(String date) {
        if (date==null) return false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static String isValidOrderForDb(String orderBy, String defaultOrder) {
        if (orderBy == null) {
            return defaultOrder;
        }
        switch (orderBy) {
            case KEY_REC_VAL:
            case KEY_REC_DATE:
            case KEY_REC_ITEM: return TABLE_RECORD+"."+orderBy;
            case KEY_CAT_NAME: return TABLE_CATEGORY+"."+orderBy;
            case KEY_CAT_DET: return TABLE_CATEGORY+"."+orderBy;
            default: return defaultOrder;
        }
    }

    public static String isValidDirectionForDb(String direction, String defaultDir) {
        if (direction ==null) {
            return defaultDir;
        }
        switch (direction) {
            case "ASC":
            case "DESC": return " "+direction;
            default: return defaultDir;
        }
    }

    public static String convertDateForDb(Date date) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return iso8601Format.format(date);
    }


    public long addRecord(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        long insertedId = -1;
        db.beginTransaction();
        try {
            long categoryId = addOrUpdateCategory(record.category);

            ContentValues recValues = new ContentValues();
            recValues.put(KEY_REC_VAL, String.valueOf(record.value));
            recValues.put(KEY_REC_CURR, record.currency.getCurrencyCode());
            recValues.put(KEY_REC_CAT_ID_FK, categoryId);
            recValues.put(KEY_REC_DATE, record.dateTime);
            recValues.put(KEY_REC_ITEM, record.item);

            insertedId = db.insertOrThrow(TABLE_RECORD,null,recValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        return insertedId;
    }

    public long deleteRecord(long id) {
        SQLiteDatabase db = getReadableDatabase();
        long ret = 0;
        db.beginTransaction();
        try {
           // String deleteRec = String.format("DELETE FROM %s WHERE %s = ?",TABLE_RECORD,KEY_REC_ID);
            ret = db.delete(TABLE_RECORD,KEY_REC_ID +" = ?",new String[]{String.valueOf(id)});
            if (ret>0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return ret;
    }

    public Category getCategoryById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Category category = null;
        db.beginTransaction();
        try {
            String catSelect = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_CAT_NAME+","+KEY_CAT_DET, TABLE_CATEGORY, KEY_CAT_ID);
            Cursor cursor = db.rawQuery(catSelect,new String[] {String.valueOf(id)});
            try {
                if (cursor.moveToFirst()) {
                    category = new Category(id, cursor.getString(0), cursor.getString(1));
                    db.setTransactionSuccessful();
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        return category;
    }

    //assume that name and details are unique across the db
    public long addOrUpdateCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        long categoryId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CAT_NAME, category.name);
            values.put(KEY_CAT_DET, category.details);

            int rows = db.update(TABLE_CATEGORY, values, KEY_CAT_NAME + "= ? AND " + KEY_CAT_DET + "= ?", new String[]{category.name, category.details});

            if (rows == 1) {
                String catSelect = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?", KEY_CAT_ID, TABLE_CATEGORY, KEY_CAT_NAME, KEY_CAT_DET);
                Cursor cursor = db.rawQuery(catSelect, new String[]{category.name, category.details});
                try {
                    if (cursor.moveToFirst()) {
                        categoryId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                categoryId = db.insertOrThrow(TABLE_CATEGORY,null,values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            //error while trying to update or add
        } finally {
            db.endTransaction();
        }
        return categoryId;
    }

    public long deleteCategory(long id) {
        SQLiteDatabase db = getReadableDatabase();
        long ret = 0;
        db.beginTransaction();
        try {
            ret = db.delete(TABLE_CATEGORY,KEY_CAT_ID+" = ?",new String[]{String.valueOf(id)});
            if (ret>0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return ret;
    }

    public Cursor getAllCategories() {
        /*SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            String cols =MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_ID+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_DET;
            cursor = db.rawQuery("SELECT "+cols+
                    " FROM "+MMDatabaseHelper.TABLE_CATEGORY+
                    " ORDER BY "+MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME,null);
        } catch (SQLiteException ex) {

        } finally {
        }
        return cursor;*/
        return getAllCategories(null,null);
    }

    public Cursor getAllCategories(String orderBy, String orderDir) {
        String cols = TABLE_CATEGORY+"."+KEY_CAT_ID+", "
                +TABLE_CATEGORY+"."+KEY_CAT_NAME+", "
                +TABLE_CATEGORY+"."+KEY_CAT_DET;

        Log.i("PARAMS",orderBy + "/"+orderDir);
        String orderByClause = " ORDER BY "+isValidOrderForDb(orderBy,TABLE_CATEGORY+"."+KEY_CAT_NAME) + isValidDirectionForDb(orderDir," ASC");
        String query = "SELECT "+cols+
                " FROM "+TABLE_CATEGORY +
                orderByClause;
        Log.i("SQL",query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query,null);
        } catch (SQLiteException e) {

        } finally {

        } return cursor;
    }

    public List<Category> getAllCategoriesAsList() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        categories.add(new Category(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
                    } while (cursor.moveToNext());
                    db.setTransactionSuccessful();
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        return categories;
    }

    private void updateMyDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                    KEY_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_CAT_NAME + " TEXT," +
                    KEY_CAT_DET + " TEXT);";

            String CREATE_RECORD_TABLE = "CREATE TABLE " + TABLE_RECORD + " (" +
                    KEY_REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_REC_VAL + " REAL," +
                    KEY_REC_CURR + " TEXT," +
                    KEY_REC_CAT_ID_FK + " INTEGER REFERENCES " + TABLE_CATEGORY + "," +
                    KEY_REC_DATE + " TEXT," +
                    KEY_REC_ITEM + " TEXT);";

            db.execSQL(CREATE_CATEGORY_TABLE);
            db.execSQL(CREATE_RECORD_TABLE);

            insertCategory(db, new Category(0, "Cash", ""));
            insertCategory(db, new Category(1, "Card", "KB"));
            insertCategory(db, new Category(2, "Card", "CSOB"));
            insertCategory(db, new Category(3, "Card", "Ceska Sporitelna"));

            Category cardKb = new Category(1, "Card", "KB");
            Category cardCSOB = new Category(2, "Card", "CSOB");
            Category cash = new Category(3, "Cash", "");
            Category cardCS = new Category(4, "Card", "Ceska Sporitelna");

            insertRecord(db, new Record(0,BigDecimal.valueOf(-500.5F),Currency.getInstance(Locale.US),"Shoes", "2016-05-17 12:12:12", cardKb));
            insertRecord(db, new Record(1,BigDecimal.valueOf(-50.5F),Currency.getInstance(Locale.GERMANY),"Baker", "2016-05-18 12:12:12", cardCSOB));
            insertRecord(db, new Record(2,BigDecimal.valueOf(-5000.5F),Currency.getInstance(Locale.UK),"Smartphone", "2016-05-20 12:12:12", cash));
            insertRecord(db, new Record(3,BigDecimal.valueOf(500.5F),Currency.getInstance(Locale.US),"Salary", "2016-06-17 12:12:12", cardCS));
            insertRecord(db, new Record(4,BigDecimal.valueOf(1000.5F),Currency.getInstance(Locale.UK),"Bonus", "2016-10-17 12:12:12", cardKb));
            insertRecord(db, new Record(5,BigDecimal.valueOf(-49256.5F),Currency.getInstance(Locale.FRANCE),"New car", "2017-05-17 12:12:12", cardCSOB));
        }
    }

    private static void insertCategory(SQLiteDatabase db, Category category) {
        ContentValues catValues = new ContentValues();
        catValues.put(KEY_CAT_NAME, category.name);
        catValues.put(KEY_CAT_DET, category.details);
        db.insert(TABLE_CATEGORY, null, catValues);
    }

    private static void insertRecord(SQLiteDatabase db, Record record) {
        ContentValues recValues = new ContentValues();
        recValues.put(KEY_REC_VAL, String.valueOf(record.value));
        recValues.put(KEY_REC_CURR, record.currency.getCurrencyCode());
        recValues.put(KEY_REC_CAT_ID_FK, record.category.id);
        recValues.put(KEY_REC_DATE, record.dateTime);
        recValues.put(KEY_REC_ITEM, record.item);
        db.insert(TABLE_RECORD, null, recValues);
    }


    private class SetDates {
        private Date startDate;
        private Date endDate;

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public SetDates invoke() {
            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.DATE, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
            startCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMinimum(Calendar.HOUR_OF_DAY));
            startCal.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));
            startCal.set(Calendar.SECOND, startCal.getActualMinimum(Calendar.SECOND));
            startCal.set(Calendar.MILLISECOND, startCal.getActualMinimum(Calendar.MILLISECOND));

            Calendar endCal = Calendar.getInstance();
            endCal.set(Calendar.DATE, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            endCal.set(Calendar.HOUR_OF_DAY, endCal.getActualMaximum(Calendar.HOUR_OF_DAY));
            endCal.set(Calendar.MINUTE, endCal.getActualMaximum(Calendar.MINUTE));
            endCal.set(Calendar.SECOND, endCal.getActualMaximum(Calendar.SECOND));
            endCal.set(Calendar.MILLISECOND, endCal.getActualMaximum(Calendar.MILLISECOND));

            startDate = startCal.getTime();
            endDate = endCal.getTime();
            return this;
        }
    }
}
