package pv239.fi.muni.cz.moneymanager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import pv239.fi.muni.cz.moneymanager.adapter.CategoriesDbAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbAdapter;
import pv239.fi.muni.cz.moneymanager.crypto.ALockingClass;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.helper.DatePickerFragment;
import pv239.fi.muni.cz.moneymanager.model.FilterCategoriesArgs;
import pv239.fi.muni.cz.moneymanager.model.FilterRecordsArgs;


/**
 * Main activity holding all other fragments. Contains navigation drawer.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */

public class MainActivity extends ALockingClass
        implements NavigationView.OnNavigationItemSelectedListener, RecordsFragment.OnRecordsInteractionListener, CategoriesFragment.OnCategoriesInteractionListener,StatsFragment.OnStatsInteractionListener, DatePickerFragment.OnDateInteractionListener, AddRecordDialog.AddRecordDialogFinishedListener, AddCategoryDialog.AddCategoryDialogFinishedListener, FilterRecordsDialog.FilterRecordsDialogFinishedListener, FilterCategoriesDialog.FilterCategoriesDialogFinishedListener {

    private int currentPosition=-1;
    private boolean hideFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deletePin();
        //this.deleteDatabase("money_manager");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragMan = getSupportFragmentManager();
                Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                invalidateOptionsMenu();
                if (fragment instanceof RecordsFragment){
                    currentPosition = R.id.nav_records;
                    hideFilter = false;
                }
                if (fragment instanceof CategoriesFragment){
                    currentPosition = R.id.nav_categories;
                    hideFilter = false;
                }
                if (fragment instanceof StatsFragment){
                    currentPosition = R.id.nav_stats;
                    hideFilter=true;
                }
                setActionBarTitle(currentPosition);
                navigationView.setCheckedItem(currentPosition);
            }
        });

        setActionBarTitle(-1);
        int position=-1;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("currentPosition");
        }
        Log.i("Pos: ", String.valueOf(position));
        Log.i("CurPos: ", String.valueOf(currentPosition));
        onNavigationItemSelected((MenuItem)findViewById(position));



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

            menu.findItem(R.id.action_filter).setVisible(!hideFilter);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = R.id.nav_records;
        if (item != null) {
            id = item.getItemId();
        }
        if (id != currentPosition) {
            switch (id) {
                case R.id.nav_records:
                    onMenuFragmentSelect(RecordsFragment.newInstance(null, null));
                    currentPosition = id;
            break;
            case R.id.nav_categories:
                    onMenuFragmentSelect(CategoriesFragment.newInstance(null, null));
                currentPosition = id;
                break;
            case R.id.nav_stats:
                    onMenuFragmentSelect(StatsFragment.newInstance(null, null));
                currentPosition = id;
                break;
            default:

        }
    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition",currentPosition);
    }

    public void onMenuFragmentSelect(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment,"visible_fragment");
        if (getSupportFragmentManager().getBackStackEntryCount()>0) getSupportFragmentManager().popBackStackImmediate();
        if (currentPosition!=-1 && fragment instanceof RecordsFragment) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        else if(currentPosition!=-1) {
            ft.addToBackStack(null);
        } else {
            ((NavigationView)findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_records);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setActionBarTitle(int id) {
        String title;
        switch (id) {
            case -1:
            case R.id.nav_records: title = "Records"; break;
            case R.id.nav_categories: title = "Categories"; break;
            case R.id.nav_stats: title = "Statistics"; break;
                default: title = "MoneyManager";
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRecordsAddRecord() {
        AddRecordDialog dialog = new AddRecordDialog();
        dialog.show(getSupportFragmentManager(),"add_record");


    }

    @Override
    public void onCategoriesAddCategory() {
        AddCategoryDialog dialog = new AddCategoryDialog();
        dialog.show(getSupportFragmentManager(),"add_category");
    }

    @Override
    public void onStatsInteraction(Uri uri) {
        //TODO: edit this method
    }

    @Override
    public void onDateInteraction(DatePicker datePicker, int caller) {
        if (caller == DatePickerFragment.ADD_RECORDS) {
            AddRecordDialog fr = (AddRecordDialog) getSupportFragmentManager().findFragmentByTag("add_record");
            if (fr == null) return;
            fr.setDateButtonTag(datePicker);
        } else if (caller == DatePickerFragment.ORDER_RECORDS) {
            FilterRecordsDialog fr = (FilterRecordsDialog) getSupportFragmentManager().findFragmentByTag("filter_records");
            if (fr==null) return;
            fr.setDateButtonTag(datePicker);
        }

    }

    @Override
    public void onAddRecordFinishedDialog(boolean result) {
        if (result) {
            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories());
        }
    }

    public void onAddCategoryFinishedDialog(boolean result) {
        if (result) {

            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((CategoriesDbAdapter)view.getAdapter()).swapCursor(help.getAllCategories());
            ((CategoriesDbAdapter)((HeaderViewListAdapter)view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories());
        }
    }


    FilterRecordsArgs recordsArgs;
    FilterCategoriesArgs categoriesArgs;
    public void onFilterClick(MenuItem item) {
                switch (currentPosition) {
                    case R.id.nav_records: {
                        FilterRecordsDialog filterDialog = new FilterRecordsDialog();
                        Bundle bundle;
                        if (recordsArgs!=null) {
                            bundle = new Bundle();
                            bundle.putInt("records_order_by",recordsArgs.getOrderBy());
                            bundle.putInt("records_direction",recordsArgs.getOrderDir());
                            if (recordsArgs.getDateFrom()!=null)
                            bundle.putString("records_date_from",DatePickerFragment.dateToString(recordsArgs.getDateFrom()));
                            if (recordsArgs.getDateTo()!=null)
                            bundle.putString("records_date_to",DatePickerFragment.dateToString(recordsArgs.getDateTo()));
                            filterDialog.setArguments(bundle);
                        }
                        filterDialog.show(getSupportFragmentManager(),"filter_records");
                        break;
                    }
                    case R.id.nav_categories: {
                        FilterCategoriesDialog filterDialog = new FilterCategoriesDialog();
                        Bundle bundle;
                        if (categoriesArgs!=null) {
                            bundle = new Bundle();
                            bundle.putInt("categories_order_by",categoriesArgs.getOrderBy());
                            bundle.putInt("categories_direction",categoriesArgs.getOrderDir());
                            filterDialog.setArguments(bundle);
                        }
                        filterDialog.show(getSupportFragmentManager(),"filter_categories");
                        break;
                    }
                    default: break;
                }
    }

    public void  onExportClick(MenuItem item) {
        ExportDatabaseCSVTask task=new ExportDatabaseCSVTask(this); task.execute();

    }

    @Override
    public void onFilterRecordsFinishedDialog(boolean result, int orderPos, int directionPos, Date from, Date to) {
        if (result) {
            recordsArgs = new FilterRecordsArgs();
            recordsArgs.setOrderBy(orderPos);
            recordsArgs.setOrderDir(directionPos);
            recordsArgs.setDateFrom(from);
            recordsArgs.setDateTo(to);

            String ordeyBy = null;
            String ordeyDir = null;
            String dateFrom = null;
            String dateTo = null;

            switch (orderPos) {
                case FilterRecordsDialog.ORDER_AMOUNT: ordeyBy = MMDatabaseHelper.KEY_REC_VAL; break;
                case FilterRecordsDialog.ORDER_DATE: ordeyBy = MMDatabaseHelper.KEY_REC_DATE; break;
                case FilterRecordsDialog.ORDER_NAME: ordeyBy = MMDatabaseHelper.KEY_REC_ITEM; break;
                case FilterRecordsDialog.ORDER_CATEGORY: ordeyBy = MMDatabaseHelper.KEY_CAT_NAME; break;
            }

            switch (directionPos) {
                case FilterRecordsDialog.DIRECTION_ASC: ordeyDir = "ASC"; break;
                case FilterRecordsDialog.DIRECTION_DESC: ordeyDir = "DESC"; break;
            }

            if (from!= null) {
                dateFrom = MMDatabaseHelper.convertDateForDb(from);
            }
            if (to!= null) {
                dateTo = MMDatabaseHelper.convertDateForDb(to);
            }

            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories(dateFrom,dateTo,ordeyBy,ordeyDir));
        } else {
            recordsArgs = null;
            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories());
        }
    }

    @Override
    public void onFilterCategoriesFinishedDialog(boolean result, int orderPos, int directionPos) {
        if (result) {
            categoriesArgs = new FilterCategoriesArgs();
            categoriesArgs.setOrderBy(orderPos);
            categoriesArgs.setOrderDir(directionPos);

            String ordeyBy = null;
            String ordeyDir = null;

            switch (orderPos) {
                case FilterCategoriesDialog.ORDER_NAME: ordeyBy = MMDatabaseHelper.KEY_CAT_NAME; break;
                case FilterCategoriesDialog.ORDER_DETAILS: ordeyBy = MMDatabaseHelper.KEY_CAT_DET; break;
            }

            switch (directionPos) {
                case FilterCategoriesDialog.DIRECTION_ASC: ordeyDir = "ASC"; break;
                case FilterCategoriesDialog.DIRECTION_DESC: ordeyDir = "DESC"; break;
            }

            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((CategoriesDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories(ordeyBy, ordeyDir));
        } else {
            categoriesArgs = null;
            ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((CategoriesDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories());
        }
    }

    private class ExportDatabaseCSVTask extends AsyncTask { private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        Context context;

        private ExportDatabaseCSVTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            /*File exportDir = Environment.getExternalStorageDirectory();
            if (!exportDir.exists()) {
                exportDir.getParentFile().mkdirs();
            }

            File file = new File(exportDir, "Hovno.csv");


            try {

                //file.getParentFile().mkdirs();
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                //data
                ArrayList<String> listdata= new ArrayList<String>();
                listdata.add("Aniket");
                listdata.add("Shinde");
                listdata.add("pune");
                listdata.add("anything@anything");
                //Headers
                String arrStr1[] ={"First Name", "Last Name", "Address", "Email"};
                csvWrite.writeNext(arrStr1);

                String arrStr[] ={listdata.get(0), listdata.get(1), listdata.get(2), listdata.get(3)};
                csvWrite.writeNext(arrStr);

                csvWrite.close();
                return "";
            }
            catch (IOException e){
                Log.e("MainActivity", e.getMessage(), e);
                return "";
            }*//*
            File dbFile=getDatabasePath("MyDBName.db");
            DBHelper dbhelper = new DBHelper(getApplicationContext());*/

            MMDatabaseHelper help = MMDatabaseHelper.getInstance(context);

            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, "csvname.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                //SQLiteDatabase db = dbhelper.getReadableDatabase();
                Cursor curCSV = help.getAllRecordsWithCategories();//db.rawQuery("SELECT * FROM contacts",null);
                csvWrite.writeNext(curCSV.getColumnNames());
                //List<String[]> database = new ArrayList<String[]>();
                while(curCSV.moveToNext())
                {
                    //database.add(new String[] {curCSV.getString(0),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)});
                    //Which column you want to exprort
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)};
                    csvWrite.writeNext(arrStr);
                }
                //csvWrite.writeAll(database);
                csvWrite.close();
                curCSV.close();
                return "";
            }
            catch(Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return "b";
            }
        }

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting database...");
            this.dialog.show();

        }


        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final Object success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.toString().isEmpty()){
                Toast.makeText(MainActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
