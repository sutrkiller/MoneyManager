package pv239.fi.muni.cz.moneymanager.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.RecyclePage;
import pv239.fi.muni.cz.moneymanager.model.AdapterParameterObject;

/**
 * Adapter for single pages on statistics page
 *
 * Created by Klasovci on 6/8/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    //private List<Fragment> mFragments = new ArrayList<>();
    //private List<String> mTitles = new ArrayList<>();

    private ArrayList<AdapterParameterObject> mObjectsList;
    private FragmentManager mFragmentsManager;


    public ViewPagerAdapter(FragmentManager fm,ArrayList<AdapterParameterObject> objectsList) {
        super(fm);
        this.mObjectsList = objectsList;
        this.mFragmentsManager = fm;
    }


    @Override
    public Fragment getItem(int position) {
        List<Fragment> fragments = mFragmentsManager.getFragments();
        int size = 0;
        if (fragments != null) {
            size = fragments.size();
        }
        AdapterParameterObject parameterObject = mObjectsList.get(position);
        RecyclePage recyclePage = RecyclePage.newInstance();
        recyclePage.setParameterObject(parameterObject);
        return (recyclePage);
    }

    @Override
    public int getItemPosition(Object object) {
        List<Fragment> fragmentList = mFragmentsManager.getFragments();
        RecyclePage fragment = (RecyclePage) object;
        AdapterParameterObject parameterObject = fragment.getParameterObject();
        int position = mObjectsList.indexOf(parameterObject);
        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }


    }

    @Override
    public int getCount() {
        return (mObjectsList.size());
    }



}
