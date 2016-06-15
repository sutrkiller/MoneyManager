package pv239.fi.muni.cz.moneymanager.db;

import android.annotation.SuppressLint;
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
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * This class is sole access for our SQLLite instance. It creates tables and provides all necessary queries methods.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 4/29/2016
 */
public class MMDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_RECORD = "record";
    public static final String KEY_CAT_ID = "_id";
    public static final String KEY_CAT_NAME = "name";
    public static final String KEY_CAT_DET = "details";
    public static final String KEY_REC_ID = "_id";
    public static final String KEY_REC_VAL = "value";
    public static final String KEY_REC_VALEUR = "value_in_eur";
    public static final String KEY_REC_CURR = "currency";
    public static final String KEY_REC_CAT_ID_FK = "categoryId";
    public static final String KEY_REC_DATE = "date";
    public static final String KEY_REC_ITEM = "item";
    public static final String DB_NAME = "money_manager";
    public static final int DB_VERSION = 2;
    private static MMDatabaseHelper sInstance;
    private static Context mContext;

    public static final String ALL_COLUMNS =
            TABLE_RECORD + "." + KEY_REC_ID + ", "
            + TABLE_RECORD + "." + KEY_REC_VAL + ", "
            + TABLE_RECORD + "." + KEY_REC_CURR + ", "
            + TABLE_RECORD + "." + KEY_REC_DATE + ", "
            + TABLE_RECORD + "." + KEY_REC_ITEM + ", "
            + TABLE_CATEGORY + "." + KEY_CAT_ID + ", "
            + TABLE_CATEGORY + "." + KEY_CAT_NAME + ", "
            + TABLE_CATEGORY + "." + KEY_CAT_DET + ", "
            + TABLE_RECORD + "." + KEY_REC_VALEUR;


    private MMDatabaseHelper(Context context) {
        this(context, null);
    }

    private MMDatabaseHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    public static synchronized MMDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MMDatabaseHelper(context.getApplicationContext());
            mContext = context;
        }
        return sInstance;
    }

    private static void insertCategory(SQLiteDatabase db, Category category) {
        ContentValues catValues = new ContentValues();
        catValues.put(KEY_CAT_NAME, category.name);
        catValues.put(KEY_CAT_DET, category.details);
        db.insert(TABLE_CATEGORY, null, catValues);
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

    private void updateMyDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion ==2) {
            String DROP_TABLE_RECORD = "DROP TABLE IF EXISTS "+TABLE_RECORD;
            String DROP_TABLE_CAT = "DROP TABLE IF EXISTS "+TABLE_CATEGORY;

            db.execSQL(DROP_TABLE_RECORD);
            db.execSQL(DROP_TABLE_CAT);
        }
        if (oldVersion < 2) {
            String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                    KEY_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_CAT_NAME + " TEXT," +
                    KEY_CAT_DET + " TEXT);";

            String CREATE_RECORD_TABLE = "CREATE TABLE " + TABLE_RECORD + " (" +
                    KEY_REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_REC_VAL + " REAL," +
                    KEY_REC_VALEUR + " REAL, "+
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
        }
    }

    public Record getRecordById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Record record = null;
        Category category;
        db.beginTransaction();
        try {
            String cols =  ALL_COLUMNS;
            String from = TABLE_RECORD+", "+TABLE_CATEGORY;
            String where = TABLE_RECORD+"."+KEY_REC_ID;
            String firstId = TABLE_RECORD+"."+KEY_REC_CAT_ID_FK;
            String secondId = TABLE_CATEGORY+"."+KEY_CAT_ID;

            String recSelect = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = %s", cols, from, where,firstId, secondId);
            Cursor cursor = db.rawQuery(recSelect,new String[] {String.valueOf(id)});
            try {
                if (cursor.moveToFirst()) {
                    category = new Category(cursor.getInt(5), cursor.getString(6), cursor.getString(7));
                    record = new Record(id, BigDecimal.valueOf(cursor.getDouble(1)),BigDecimal.valueOf(cursor.getDouble(8)), Currency.getInstance(cursor.getString(2)),cursor.getString(4),cursor.getString(3),category);
                    db.setTransactionSuccessful();
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception ignored) {
        } finally {
            db.endTransaction();
        }
        return record;
    }

    public Cursor getAllRecordsWithCategories() {
        return getAllRecordsWithCategories(null,null,null,null);
    }

    /**
     * Get financial balance at specified date
     *
     * @param date date of balance
     * @return sum of all records from previous dates
     */
    @SuppressLint("Recycle")
    public BigDecimal getStartingBal(Date date)
    {
        String col = TABLE_RECORD + "." + KEY_REC_VALEUR;
        String whereClause = " WHERE " +TABLE_RECORD+"."+KEY_REC_DATE+" < '"+convertDateForDb(date)+"'";
        String query = "SELECT SUM(" + col + ") FROM " + TABLE_RECORD+ whereClause;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()) {
                return new BigDecimal(cursor.getDouble(0));
            }
        } catch (SQLiteException ignored) {
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get date of first record in DB
     *
     * @return date of first record in format "yyyy-MM-dd HH:mm:ss"
     */
    @SuppressLint("Recycle")
    public String getFirstRecordDate()
    {
        String col = TABLE_RECORD+"."+KEY_REC_DATE;
        String query = "SELECT MIN(" + col + ") FROM "+TABLE_RECORD;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (SQLiteException ignored) {
        }
        return null;
    }

    public Cursor getAllRecordsWithCategories(String dateFrom, String dateTo, String orderBy, String orderDir) {
        String cols = MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME+", "
                +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_DET+", "
                +TABLE_RECORD+"."+KEY_REC_VALEUR;

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
        String query = "SELECT "+cols+" FROM "+TABLE_RECORD+", "+TABLE_CATEGORY + whereClause + orderByClause;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException ignored) {
        }
        return cursor;
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
            recValues.put(KEY_REC_VALEUR, String.valueOf(record.valueInEur));

            insertedId = db.insertOrThrow(TABLE_RECORD,null,recValues);
            db.setTransactionSuccessful();
        } catch (Exception ignored) {

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
            ret = db.delete(TABLE_RECORD,KEY_REC_ID +" = ?",new String[]{String.valueOf(id)});
            if (ret>0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception ignored) {
        } finally {
            db.endTransaction();
        }
        return ret;
    }

    public long deleteAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        long ret = 0;
        db.beginTransaction();
        try {
            ret = db.delete(TABLE_RECORD,null,null);
            if (ret>= 0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {

        } finally {
            db.endTransaction();
        }
        return category;
    }

    //assume that name and details of category are unique across the db
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
        } catch (Exception ignored) {
        } finally {
            db.endTransaction();
        }
        return ret;
    }

    public Cursor getAllCategories() {
        return getAllCategories(null,null);
    }

    public Cursor getAllCategories(String orderBy, String orderDir) {
        String cols = TABLE_CATEGORY+"."+KEY_CAT_ID+", "
                +TABLE_CATEGORY+"."+KEY_CAT_NAME+", "
                +TABLE_CATEGORY+"."+KEY_CAT_DET;

        String orderByClause = " ORDER BY "+isValidOrderForDb(orderBy,TABLE_CATEGORY+"."+KEY_CAT_NAME) + isValidDirectionForDb(orderDir," ASC");
        String query = "SELECT "+cols+
                " FROM "+TABLE_CATEGORY +
                orderByClause;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException ignored) {

        }
        return cursor;
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
        } catch (Exception ignored) {

        } finally {
            db.endTransaction();
        }
        return categories;
    }

    private static boolean isValidDateForDb(String date) {
        if (date == null) return false;
        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.db_date_format), Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    private static String isValidOrderForDb(String orderBy, String defaultOrder) {
        if (orderBy == null) {
            return defaultOrder;
        }
        switch (orderBy) {
            case KEY_REC_VAL:
            case KEY_REC_DATE:
            case KEY_REC_ITEM:
                return TABLE_RECORD + "." + orderBy;
            case KEY_CAT_NAME:
                return TABLE_CATEGORY + "." + orderBy;
            case KEY_CAT_DET:
                return TABLE_CATEGORY + "." + orderBy;
            default:
                return defaultOrder;
        }
    }

    private static String isValidDirectionForDb(String direction, String defaultDir) {
        if (direction == null) {
            return defaultDir;
        }
        switch (direction) {
            case "ASC":
            case "DESC":
                return " " + direction;
            default:
                return defaultDir;
        }
    }

    public static String convertDateForDb(Date date) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(mContext.getString(R.string.db_date_format),Locale.getDefault());
        return iso8601Format.format(date);
    }

}
