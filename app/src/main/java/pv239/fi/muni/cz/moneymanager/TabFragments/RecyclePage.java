package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.adapter.RecycleAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.AdapterParameterObject;

/**
 * Page containing RecycleView in statistics
 *
 * Created by Klasovci on 6/8/2016.
 */
public class RecyclePage extends Fragment {
    //private int pageNumber;
    private List<StatPage> pages = new ArrayList<>();
    //private int daysBack;
    //private int version = 0;
    private AdapterParameterObject mObject;

    public RecyclePage() {
    }

    public static RecyclePage newInstance(/*int page, int days, int version*/) {
        /*
        Bundle args = new Bundle();
        args.putInt("page_number", page);
        args.putInt("daysBack",days);
        args.putInt("version",version);
        RecyclePage fragment = new RecyclePage();
        fragment.setArguments(args);
        */
        RecyclePage fragment = new RecyclePage();

        return (fragment);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            pageNumber = getArguments().getInt("page_number");
//            daysBack = getArguments().getInt("daysBack");
//            version = getArguments().getInt("version");
//        }
        //Log.i("RecyclePage onCreate ", String.valueOf(version));
    }

    public void setParameterObject(AdapterParameterObject object) {
        mObject = object;
    }

    public AdapterParameterObject getParameterObject() {
        return mObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.recycle_view_layout, container, false);
        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.fragRecycleView);

        int version = mObject.getVersion();
        int daysBack = mObject.getDaysBack();
        int tabNumber = mObject.getPageNumber();
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(layout.getContext(), LinearLayoutManager.HORIZONTAL, true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //if (version==1) mRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        MMDatabaseHelper db = MMDatabaseHelper.getInstance(getContext());


            // mRecyclerView.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            String firstDate = db.getFirstRecordDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (firstDate == null) firstDate = format.format(Calendar.getInstance().getTime());
            Date date = format.parse(firstDate, new ParsePosition(0));

            Calendar lateCal = Calendar.getInstance();
            Calendar earlyCal = Calendar.getInstance();
            Calendar firstCal = Calendar.getInstance();
            earlyCal.add(Calendar.DAY_OF_YEAR, -daysBack);
            firstCal.setTime(date);

            while (firstCal.compareTo(lateCal) < 0) {
                StatPage page = new StatPage(getContext(), earlyCal.getTime(), lateCal.getTime(),tabNumber,version);
                pages.add(page);
                earlyCal.add(Calendar.DAY_OF_YEAR, -daysBack);
                lateCal.add(Calendar.DAY_OF_YEAR, -daysBack);
            }

            RecyclerView.Adapter mAdapter = new RecycleAdapter(pages);

            mRecyclerView.setAdapter(mAdapter);


        return (layout);
    }


}
