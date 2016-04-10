package pv239.fi.muni.cz.moneymanager;

import android.net.Uri;
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

import pv239.fi.muni.cz.moneymanager.crypto.ALockingClass;

public class MainActivity extends ALockingClass
        implements NavigationView.OnNavigationItemSelectedListener, RecordsFragment.OnRecordsInteractionListener, CategoriesFragment.OnCategoriesInteractionListener,StatsFragment.OnStatsInteractionListener {

    private int currentPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deletePin();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



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
                if (fragment instanceof RecordsFragment){
                    currentPosition = R.id.nav_records;
                }
                if (fragment instanceof CategoriesFragment){
                    currentPosition = R.id.nav_categories;
                }
                if (fragment instanceof StatsFragment){
                    currentPosition = R.id.nav_stats;
                }
                setActionBarTitle(currentPosition);
                navigationView.setCheckedItem(currentPosition);
                //drawerList.setItemChecked(currentPosition, true);
            }
        });

        setActionBarTitle(-1);
        int position=-1;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("currentPosition");
            //setActionBarTitle(currentPosition)
        }
        //setActionBarTitle(currentPosition);
        onNavigationItemSelected((MenuItem)findViewById(position));
        //getSupportActionBar().setTitle("Records");

    }

   /* private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment;
        switch (position) {
            case 1: fragment = CategoriesFragment.newInstance("sdas","sdjas"); break;
            case 2: fragment = StatsFragment.newInstance("sdal","sda"); break;
            default: fragment = RecordsFragment.newInstance("bal","nlas");
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        //setActionBarTitle(position);

        //drawerLayout.closeDrawer(drawerList);
    }*/


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
            //boolean addToStack= false;
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
            //setActionBarTitle(id);

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
        //RecordsFragment frag = RecordsFragment.newInstance("ahoj", "balbla");
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
            //setActionBarTitle(-1);
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
        //Log.e("WITHOFJ", String.valueOf(id));
    }

    @Override
    public void onRecordsInteraction(Uri uri) {
        //JUST TESTING
    }

    @Override
    public void onCategoriesInteraction(Uri uri) {
        //JST TESTING
    }

    @Override
    public void onStatsInteraction(Uri uri) {
        //JUST TESTING
    }


}
