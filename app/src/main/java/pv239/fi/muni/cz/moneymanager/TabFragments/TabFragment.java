package pv239.fi.muni.cz.moneymanager.TabFragments;

/**
 * Created by Klasovci on 6/3/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pv239.fi.muni.cz.moneymanager.R;

public class TabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment, container, false);
        //TextView text = (TextView) v.findViewById(R.id.startMonthStats);
        //text.setText("Initial settings");
        return v;
    }
}