package pv239.fi.muni.cz.moneymanager.adapter;

/**
 * Created by Klas on 6/3/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import pv239.fi.muni.cz.moneymanager.TabFragments.TabFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    SparseArray<TabFragment> registeredFragments;
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        registeredFragments =  new SparseArray<TabFragment>();
        for (int i= 0; i< NumOfTabs; i++)
        {
            registeredFragments.put(i,new TabFragment());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return registeredFragments.valueAt(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}