package pv239.fi.muni.cz.moneymanager;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pv239.fi.muni.cz.moneymanager.adapter.CategoriesDbAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbAdapter;
import pv239.fi.muni.cz.moneymanager.crypto.ALockingClass;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.helper.DatePickerFragment;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.FilterCategoriesArgs;
import pv239.fi.muni.cz.moneymanager.model.FilterRecordsArgs;
import pv239.fi.muni.cz.moneymanager.model.Record;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Main activity holding all other fragments. Contains navigation drawer.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */

public class MainActivity extends ALockingClass
        implements EasyPermissions.PermissionCallbacks,NavigationView.OnNavigationItemSelectedListener, RecordsFragment.OnRecordsInteractionListener, CategoriesFragment.OnCategoriesInteractionListener,StatsFragment.OnStatsInteractionListener, DatePickerFragment.OnDateInteractionListener, AddRecordDialog.AddRecordDialogFinishedListener, AddCategoryDialog.AddCategoryDialogFinishedListener, FilterRecordsDialog.FilterRecordsDialogFinishedListener, FilterCategoriesDialog.FilterCategoriesDialogFinishedListener {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_PERMISSION_WRITE_STORAGE = 1004;
    private static final long SYNC_TIME_MIN_DIF = MILLISECONDS.convert(5,SECONDS);
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String FILE_NAME = "MoneyManager";
    private static final String PREF_FILE_RES = "MoneyManagerSpreadsheet";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE_FILE};
    private static Drive mGOOSvc;
    GoogleAccountCredential mCredential;
    FilterRecordsArgs recordsArgs;
    FilterCategoriesArgs categoriesArgs;
    private int currentPosition = -1;
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
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

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



        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());



    }

    @Override
    protected void onResume() {
        super.onResume();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(currentPosition);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
            Toast.makeText(this, "No network connection.", Toast.LENGTH_SHORT).show();
        } else {
            mGOOSvc = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                    GoogleAccountCredential.usingOAuth2(this, Collections.singletonList(DriveScopes.DRIVE_FILE))
                        .setSelectedAccountName(mCredential.getSelectedAccountName())).build();


            new MakeRequestTask(mCredential, this).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                    Toast.makeText(this, "The app requires Google Play Services", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                ExportDatabaseCSVTask task = new ExportDatabaseCSVTask(this);
                task.execute();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

        }
        else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(
                    requestCode, permissions, grantResults, this);
        }
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
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
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

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
                    onMenuFragmentSelect(RecordsFragment.newInstance());
                    currentPosition = id;
                    break;
                case R.id.nav_categories:
                    onMenuFragmentSelect(CategoriesFragment.newInstance());
                    currentPosition = id;
                    break;
                case R.id.nav_stats:
                    onMenuFragmentSelect(StatsFragment.newInstance());
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
        outState.putInt("currentPosition", currentPosition);
    }

    public void onMenuFragmentSelect(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, "visible_fragment");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        if (currentPosition != -1 && fragment instanceof RecordsFragment) {
            getSupportFragmentManager().popBackStackImmediate();
        } else if (currentPosition != -1) {
            ft.addToBackStack(null);
        } else {
            ((NavigationView) findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_records);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setActionBarTitle(int id) {
        String title;
        switch (id) {
            case -1:
            case R.id.nav_records:
                title = "Records";
                break;
            case R.id.nav_categories:
                title = "Categories";
                break;
            case R.id.nav_stats:
                title = "Statistics";
                break;
            default:
                title = "MoneyManager";
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRecordsAddRecord() {
        AddRecordDialog dialog = new AddRecordDialog();
        dialog.show(getSupportFragmentManager(), "add_record");


    }

    @Override
    public void onCategoriesAddCategory() {
        AddCategoryDialog dialog = new AddCategoryDialog();
        dialog.show(getSupportFragmentManager(), "add_category");
    }

    @Override
    public void onDateInteraction(DatePicker datePicker, int caller) {
        if (caller == DatePickerFragment.ADD_RECORDS) {
            AddRecordDialog fr = (AddRecordDialog) getSupportFragmentManager().findFragmentByTag("add_record");
            if (fr == null) return;
            fr.setDateButtonTag(datePicker);
        } else if (caller == DatePickerFragment.ORDER_RECORDS) {
            FilterRecordsDialog fr = (FilterRecordsDialog) getSupportFragmentManager().findFragmentByTag("filter_records");
            if (fr == null) return;
            fr.setDateButtonTag(datePicker);
        }

    }

    @Override
    public void onAddRecordFinishedDialog(boolean result) {
        if (result) {
            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories());

            File dbpath = getDatabasePath(MMDatabaseHelper.DB_NAME);
            long dbModif = dbpath.lastModified();
            Log.i("DB Modified: ", String.valueOf(dbModif));
        }
    }

    public void onAddCategoryFinishedDialog(boolean result) {
        if (result) {

            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((CategoriesDbAdapter)view.getAdapter()).swapCursor(help.getAllCategories());
            ((CategoriesDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories());
        }
    }

    public void onFilterClick(MenuItem item) {
        switch (currentPosition) {
            case R.id.nav_records: {
                FilterRecordsDialog filterDialog = new FilterRecordsDialog();
                Bundle bundle;
                if (recordsArgs != null) {
                    bundle = new Bundle();
                    bundle.putInt("records_order_by", recordsArgs.getOrderBy());
                    bundle.putInt("records_direction", recordsArgs.getOrderDir());
                    if (recordsArgs.getDateFrom() != null)
                        bundle.putString("records_date_from", DatePickerFragment.dateToString(recordsArgs.getDateFrom()));
                    if (recordsArgs.getDateTo() != null)
                        bundle.putString("records_date_to", DatePickerFragment.dateToString(recordsArgs.getDateTo()));
                    filterDialog.setArguments(bundle);
                }
                filterDialog.show(getSupportFragmentManager(), "filter_records");
                break;
            }
            case R.id.nav_categories: {
                FilterCategoriesDialog filterDialog = new FilterCategoriesDialog();
                Bundle bundle;
                if (categoriesArgs != null) {
                    bundle = new Bundle();
                    bundle.putInt("categories_order_by", categoriesArgs.getOrderBy());
                    bundle.putInt("categories_direction", categoriesArgs.getOrderDir());
                    filterDialog.setArguments(bundle);
                }
                filterDialog.show(getSupportFragmentManager(), "filter_categories");
                break;
            }
            default:
                break;
        }
    }

    public void onExportClick(MenuItem item) {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_STORAGE);

        } else {

            ExportDatabaseCSVTask task = new ExportDatabaseCSVTask(this);
            task.execute();
        }
    }

    public void onSyncClick(MenuItem item) {
        getResultsFromApi();
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
                case FilterRecordsDialog.ORDER_AMOUNT:
                    ordeyBy = MMDatabaseHelper.KEY_REC_VAL;
                    break;
                case FilterRecordsDialog.ORDER_DATE:
                    ordeyBy = MMDatabaseHelper.KEY_REC_DATE;
                    break;
                case FilterRecordsDialog.ORDER_NAME:
                    ordeyBy = MMDatabaseHelper.KEY_REC_ITEM;
                    break;
                case FilterRecordsDialog.ORDER_CATEGORY:
                    ordeyBy = MMDatabaseHelper.KEY_CAT_NAME;
                    break;
            }

            switch (directionPos) {
                case FilterRecordsDialog.DIRECTION_ASC:
                    ordeyDir = "ASC";
                    break;
                case FilterRecordsDialog.DIRECTION_DESC:
                    ordeyDir = "DESC";
                    break;
            }

            if (from != null) {
                dateFrom = MMDatabaseHelper.convertDateForDb(from);
            }
            if (to != null) {
                dateTo = MMDatabaseHelper.convertDateForDb(to);
            }

            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories(dateFrom, dateTo, ordeyBy, ordeyDir));
        } else {
            recordsArgs = null;
            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
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
                case FilterCategoriesDialog.ORDER_NAME:
                    ordeyBy = MMDatabaseHelper.KEY_CAT_NAME;
                    break;
                case FilterCategoriesDialog.ORDER_DETAILS:
                    ordeyBy = MMDatabaseHelper.KEY_CAT_DET;
                    break;
            }

            switch (directionPos) {
                case FilterCategoriesDialog.DIRECTION_ASC:
                    ordeyDir = "ASC";
                    break;
                case FilterCategoriesDialog.DIRECTION_DESC:
                    ordeyDir = "DESC";
                    break;
            }

            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((CategoriesDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories(ordeyBy, ordeyDir));
        } else {
            categoriesArgs = null;
            ListView view = (ListView) getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.categories_list_view);
            MMDatabaseHelper help = MMDatabaseHelper.getInstance(this);
            //((RecordsDbAdapter) view.getAdapter()).swapCursor(help.getAllRecordsWithCategories());
            ((CategoriesDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllCategories());
        }
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Integer> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        private Context context;
        private int result = -1;

        public MakeRequestTask(GoogleAccountCredential credential, Context context) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API MoneyManager")
                    .build();
            this.context = context;
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                File dbpath = getDatabasePath(MMDatabaseHelper.DB_NAME);
                long dbModif = dbpath.lastModified();
                Log.i("DB Modified: ", String.valueOf(dbModif));
                long driveModif;
                String resId;
                getDataFromApi();
                if (mGOOSvc!= null && isDeviceOnline()) {
                    try {
                        FileList fileList = mGOOSvc.files().list().setQ("title='" + FILE_NAME + "' and trashed=false").execute();
                        if (fileList.getItems().isEmpty()) {
                            Log.i("Google Drive", "Creating file");
                            resId = testCreateSheet();

                            if (resId != null) {
                                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                                prefs.edit().putString(PREF_FILE_RES, resId).apply(); //save resId for further editing
                                testUpdateContent(resId);
                                result = 1;

                            }
                        } else {
                            Log.i("Google Drive", "File already exists");
                            driveModif = fileList.getItems().get(0).getModifiedDate().getValue();
                            resId = fileList.getItems().get(0).getId();
                            Log.i("Drive Modified: ", String.valueOf(driveModif));


                            if (Math.abs(dbModif - driveModif) >= SYNC_TIME_MIN_DIF) {  //if changes are at least SYNC_TIME_MIN_DIF apart
                                if (dbModif - driveModif <= SYNC_TIME_MIN_DIF) {
                                    //drive is newer -> download data
                                    testDownloadContent(resId);
                                    result = 0;
                                    Log.i("Drive: ", "Changes downloaded");
                                } else if (dbModif - driveModif >= SYNC_TIME_MIN_DIF) {
                                    //db is newer -> upload data
                                    testUpdateContent(resId);
                                    result = 1;
                                    Log.i("Drive: ", "Changes updated");
                                } else {
                                    result = 2;
                                    Log.i("Drive: ", "No changes");
                                }
                            }
                        }


                    } catch (Exception e) {
                        Log.e("Drive Error", Log.getStackTraceString(e));
                    }
                }
                return result;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                result = -1;
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {

            String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
            String range = "Class Data!A2:E";
            List<String> results = new ArrayList<>();
            try {
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    results.add(row.get(0) + ", " + row.get(4));
                }
            }
            //return results;
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }
            return results;
        }

        private String testCreateSheet() throws IOException {
            String rsId = null;
            if (mGOOSvc!= null && isDeviceOnline()) {
                try {
                        com.google.api.services.drive.model.File meta = new com.google.api.services.drive.model.File();
                        meta.setParents(Collections.singletonList(new ParentReference().setId("root")));
                        meta.setTitle(FILE_NAME);
                        meta.setMimeType("application/vnd.google-apps.spreadsheet");

                        com.google.api.services.drive.model.File gF1 = mGOOSvc.files().insert(meta).execute();
                        if (gF1 != null) {
                            rsId = gF1.getId();
                        }
                } catch (Exception e) {
                    Log.e("Drive Error", Log.getStackTraceString(e));
                }}
                return rsId;
            }



        private void testUpdateContent(String spreadsheetId) throws IOException {
            try {

            MMDatabaseHelper help = MMDatabaseHelper.getInstance(context);
            Cursor curCSV = help.getAllRecordsWithCategories();

            List<Request> requests = new ArrayList<>();

            List<CellData> values = new ArrayList<>();
            List<RowData> rows = new ArrayList<>();

            String headers[] = curCSV.getColumnNames();

            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[0]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[1]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[2]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[7]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[3]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[4]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[5]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(headers[6]))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(1f))));

            rows.add(new RowData().setValues(values));

            while(curCSV.moveToNext())

            {
                values = new ArrayList<>();
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue()
                                .setStringValue(curCSV.getString(0))));
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue()
                                .setStringValue(curCSV.getString(1))));
                values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(curCSV.getString(2))));
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue()
                                .setStringValue(curCSV.getString(7))));
                values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(curCSV.getString(3))));
                values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(curCSV.getString(4))));
                values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(curCSV.getString(5))));
                values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(curCSV.getString(6))));

                rows.add(new RowData().setValues(values));
            }

            List<List<Object>> list = this.mService.spreadsheets().values().get(spreadsheetId,"A:H").setMajorDimension("ROWS").execute().getValues();
            int end = list == null ? -2 : list.size();
            if (rows.size()-1 <end) {
                Request request = new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(new DimensionRange().setSheetId(0).setDimension("ROWS").setStartIndex(rows.size() - 1).setEndIndex(end)));
                Log.i("REQUEST: ",rows.size()-1+"  " +end);
                requests.add(request);
            }

            requests.add(new Request()
                    .setUpdateCells(new UpdateCellsRequest()
                            .setStart(new GridCoordinate()
                                    .setSheetId(0)
                                    .setRowIndex(0)
                                    .setColumnIndex(0))
                            .setRows(rows)
                            .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));


            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            this.mService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();
            } catch (UserRecoverableAuthIOException e) {
             startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }
        }


        private void testDownloadContent(String resId) {
            try {
                String range = "A:H";
                ValueRange response = this.mService.spreadsheets().values().get(resId,range).setMajorDimension("ROWS").execute();
                MMDatabaseHelper db = MMDatabaseHelper.getInstance(getApplicationContext());
                db.deleteAllRecords();
                List<List<Object>> values = response.getValues();
                if (values!= null && values.size()>1) {
                    values.remove(0);
                    for (List row : values) {
                        //long id = Long.parseLong(row.get(0).toString());
                        BigDecimal value = new BigDecimal(row.get(1).toString());
                        Currency currency = Currency.getInstance(row.get(2).toString());
                        BigDecimal valInEur = new BigDecimal(row.get(3).toString());
                        String date = row.get(4).toString();
                        String item = row.get(5).toString();
                        Category category = new Category(0,row.get(6).toString(),row.size()==7 ? "":row.get(7).toString());

                        Record record = new Record(0,value,valInEur,currency,item,date,category);
                        db.addRecord(record);
                    }
                }

        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        }
            catch (IOException e) {
                Log.e("Error read drive",Log.getStackTraceString(e));
            }
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(MainActivity.this, "Sync in progress!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Integer output) {
            if (output == null || output == 0) {
                Toast.makeText(MainActivity.this, "Changes downloaded!", Toast.LENGTH_SHORT).show();
            } else if (output == 1) {
                Toast.makeText(MainActivity.this, "Changes uploaded!", Toast.LENGTH_SHORT).show();
            } else if (output == 2) {
                Toast.makeText(MainActivity.this, "No pending changes!", Toast.LENGTH_SHORT).show();
            } else   {
                Toast.makeText(MainActivity.this, "Sync failed!", Toast.LENGTH_SHORT).show();
            }
                Log.i("RESULT:", String.valueOf(output));
                if (output != null)
                if (output == 0 && currentPosition == R.id.nav_records) {
                    ListView view = (ListView)getSupportFragmentManager().findFragmentByTag("visible_fragment").getActivity().findViewById(R.id.records_list_view);
                    MMDatabaseHelper help = MMDatabaseHelper.getInstance(MainActivity.this);
                    ((RecordsDbAdapter) ((HeaderViewListAdapter) view.getAdapter()).getWrappedAdapter()).swapCursor(help.getAllRecordsWithCategories());
                }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {

                    Log.i("Error", mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
                Toast.makeText(getApplicationContext(), "Request cancelled", Toast.LENGTH_LONG).show();
            }
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

            MMDatabaseHelper help = MMDatabaseHelper.getInstance(context);
            Cursor curCSV = help.getAllRecordsWithCategories();

            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFSheet sheet = workbook.createSheet("Test");

            String headers[] = curCSV.getColumnNames();

            HSSFRow row0 = sheet.createRow((short) 0);
            row0.createCell(0).setCellValue(headers[0]);
            row0.createCell(1).setCellValue(headers[1]);
            row0.createCell(2).setCellValue(headers[2]);
            row0.createCell(3).setCellValue(headers[7]);
            row0.createCell(4).setCellValue(headers[3]);
            row0.createCell(5).setCellValue(headers[4]);
            row0.createCell(6).setCellValue(headers[5]);
            row0.createCell(7).setCellValue(headers[6]);

            int counter = 1;

            while(curCSV.moveToNext())
            {
                HSSFRow row = sheet.createRow((short)counter);
                row.createCell(0).setCellValue(curCSV.getString(0));
                row.createCell(1).setCellValue(curCSV.getString(1));
                row.createCell(2).setCellValue(curCSV.getString(2));
                row.createCell(3).setCellValue(curCSV.getString(7));
                row.createCell(4).setCellValue(curCSV.getString(3));
                row.createCell(5).setCellValue(curCSV.getString(4));
                row.createCell(6).setCellValue(curCSV.getString(5));
                row.createCell(7).setCellValue(curCSV.getString(6));
                //String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)};
                //csvWrite.writeNext(arrStr);
                counter++;
            }
                    FileOutputStream fos = null;



            try {

                String str_path = Environment.getExternalStorageDirectory().toString();
                File file ;
                file = new File(str_path, getString(R.string.app_name) + ".xls");
                fos = new FileOutputStream(file);


                workbook.write(fos);
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "b";
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
