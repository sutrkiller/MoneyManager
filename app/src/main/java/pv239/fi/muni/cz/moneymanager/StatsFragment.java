package pv239.fi.muni.cz.moneymanager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pv239.fi.muni.cz.moneymanager.TabFragments.RecyclePage;
import pv239.fi.muni.cz.moneymanager.adapter.ViewPagerAdapter;

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

        final CustomViewPager viewPager = (CustomViewPager) layout.findViewById(R.id.pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        /*pageOne = RecyclePage.newInstance(0,7);
        pageTwo = RecyclePage.newInstance(1,28);
        pageThree = RecyclePage.newInstance(2, 365);
        adapter.addFragment(pageOne,"7 days");
        adapter.addFragment(pageTwo,"1 month");
        adapter.addFragment(pageThree,"1 year");
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/
        update(viewPager,tabLayout,adapter);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private void update(final CustomViewPager viewPager, TabLayout tabLayout, ViewPagerAdapter adapter) {
        pageOne = RecyclePage.newInstance(0,7);
        pageTwo = RecyclePage.newInstance(1,28);
        pageThree = RecyclePage.newInstance(2, 365);
        adapter.addFragment(pageOne,"7 days");
        adapter.addFragment(pageTwo,"1 month");
        adapter.addFragment(pageThree,"1 year");

        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final CustomViewPager viewPager = (CustomViewPager) mLayout.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout)mLayout.findViewById(R.id.tab_layout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        update(viewPager,tabLayout,adapter);


    }

    @Override
    public void onDetach() {
        super.onDetach();
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
