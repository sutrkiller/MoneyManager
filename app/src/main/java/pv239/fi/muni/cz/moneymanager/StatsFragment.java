package pv239.fi.muni.cz.moneymanager;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import pv239.fi.muni.cz.moneymanager.TabFragments.RecyclePage;
import pv239.fi.muni.cz.moneymanager.adapter.ViewPagerAdapter;
import pv239.fi.muni.cz.moneymanager.model.AdapterParameterObject;

/**
 * Fragment holding all statistics about data.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class StatsFragment extends Fragment  {
    LayoutInflater inflater;
    ViewGroup container;
    View mLayout;
    private RecyclePage pageOne;
    private RecyclePage pageTwo;
    private RecyclePage pageThree;
    //private ViewPagerAdapter mAdapter;
    private static int mCurrent=0;
    private ArrayList<AdapterParameterObject> mObjectsList;
    private ViewPagerAdapter mAdapter;
    private CustomViewPager mViewPager;

//    private static Field sChildFragmentManagerField;
//    static {
//        Field f = null;
//        try {
//            f= Fragment.class.getDeclaredField("mChildFragmentManager");
//            f.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        sChildFragmentManagerField = f;
//    }

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance() {

        Bundle args = new Bundle();
        StatsFragment fragment = new StatsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("JUST A TEST", "onCreate");
        //if (getArguments() != null) {
        //mParam1 = getArguments().getString(ARG_PARAM1);
        //mParam2 = getArguments().getString(ARG_PARAM2);
        //}

    }

//    private void onLeftSwipe() {
//        // Do something
//    }

//    private void onRightSwipe() {
//        // Do something
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("JUST A TEST", "onCreateView");
        this.inflater = inflater;
        this.container = container;

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_stats, container, false);
        this.mLayout =layout;


        TabLayout tabLayout = (TabLayout)layout.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("7 days"));
        tabLayout.addTab(tabLayout.newTab().setText("4 weeks"));
        tabLayout.addTab(tabLayout.newTab().setText("1 year"));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mObjectsList = new ArrayList<>();
        mObjectsList.add(new AdapterParameterObject(0,7,mCurrent));
        mObjectsList.add(new AdapterParameterObject(1,28,mCurrent));
        mObjectsList.add(new AdapterParameterObject(2,365,mCurrent));

        mAdapter = new ViewPagerAdapter(getChildFragmentManager(),mObjectsList);
        mViewPager = (CustomViewPager) layout.findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //mAdapter = new ViewPagerAdapter(getChildFragmentManager());
        //update(viewPager,tabLayout,adapter);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("JUST A TEST", "onActivityCreated");
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        View v = null;
        if (bar != null) {
            v = bar.getCustomView();
        }
        if (v != null) {
            Spinner spinner = ((Spinner) v.findViewById(R.id.spinner_action_title));
            if (spinner.getOnItemSelectedListener()==null) {
                spinner.setOnItemSelectedListener(mListener);
            }
        }

    }

    private static boolean isFirstCall = false;
    AdapterView.OnItemSelectedListener mListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!isFirstCall) {
                mCurrent = position;
                //onCreate(null);
                //onCreateView(inflater,container,null);
                //final CustomViewPager viewPager = (CustomViewPager) mLayout.findViewById(R.id.pager);
                //TabLayout tabLayout = (TabLayout)mLayout.findViewById(R.id.tab_layout);
                //ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),);
                //onResume();
                update();

            } else {
                isFirstCall = false;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void update() {
        Log.i("UPDATE", "UPDATE");
        /*try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
        mObjectsList.clear();
        //mAdapter.notifyDataSetChanged();
        mObjectsList.add(new AdapterParameterObject(0,7,mCurrent));
        mObjectsList.add(new AdapterParameterObject(1,28,mCurrent));
        mObjectsList.add(new AdapterParameterObject(2,365,mCurrent));

        mAdapter.notifyDataSetChanged();
        //pageOne = null;
//        pageOne = RecyclePage.newInstance(0,7,position);
//        pageTwo = RecyclePage.newInstance(1,28,position);
//        pageThree = RecyclePage.newInstance(2, 365,position);

//        ArrayList<AdapterParameterObject> parameterObjects = new ArrayList<>();
//        parameterObjects.add(new AdapterParameterObject(0,7,position));
//        parameterObjects.add(new AdapterParameterObject(1,28,position));
//        parameterObjects.add(new AdapterParameterObject(2,365,position));

        //ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),parameterObjects);

//        adapter.addFragment(pageOne,"7 days");
//        adapter.addFragment(pageTwo,"4 weeks");
//        adapter.addFragment(pageThree,"1 year");

//        viewPager.setAdapter(adapter);
//        adapter.notifyDataSetChanged();


       // Log.i("JUST A TEST", String.valueOf(position));

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("JUST A TEST", "onResume");
        //final CustomViewPager viewPager = (CustomViewPager) mLayout.findViewById(R.id.pager);
        //TabLayout tabLayout = (TabLayout)mLayout.findViewById(R.id.tab_layout);
        //ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        update();


    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Log.i("JUST A TEST", "onDetach");
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//        if (sChildFragmentManagerField!= null) {
//            try {
//                sChildFragmentManagerField.set(this,null);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStatsInteractionListener {
    }
}
