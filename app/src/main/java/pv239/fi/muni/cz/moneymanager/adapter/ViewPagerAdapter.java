package pv239.fi.muni.cz.moneymanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import pv239.fi.muni.cz.moneymanager.TabFragments.RecyclePage;
import pv239.fi.muni.cz.moneymanager.model.AdapterParameterObject;

/**
 * Adapter for single pages on statistics page
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 06/10/2016
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<AdapterParameterObject> mObjectsList;

    public ViewPagerAdapter(FragmentManager fm,ArrayList<AdapterParameterObject> objectsList) {
        super(fm);
        this.mObjectsList = objectsList;
    }

    @Override
    public Fragment getItem(int position) {
        AdapterParameterObject parameterObject = mObjectsList.get(position);
        RecyclePage recyclePage = RecyclePage.newInstance();
        recyclePage.setParameterObject(parameterObject);
        return (recyclePage);
    }

    @Override
    public int getItemPosition(Object object) {
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
