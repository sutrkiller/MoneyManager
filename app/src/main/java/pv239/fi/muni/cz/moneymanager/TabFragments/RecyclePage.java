package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by Klasovci on 6/8/2016.
 */
public class RecyclePage extends Fragment {
    private int pageNumber;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<StatPage> pages = new ArrayList<>();
    private int daysBack;

    public static RecyclePage newInstance(int page, int days) {
        Bundle args = new Bundle();
        args.putInt("page_number", page);
        args.putInt("daysBack",days);
        RecyclePage fragment = new RecyclePage();
        fragment.setArguments(args);
        return (fragment);

    }

    public RecyclePage(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt("page_number");
            daysBack = getArguments().getInt("daysBack");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.recycle_view_layout, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.fragRecycleView);

        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(layout.getContext(), LinearLayoutManager.HORIZONTAL, true);

        mRecyclerView.setLayoutManager(mLayoutManager);
       // mRecyclerView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        MMDatabaseHelper db = MMDatabaseHelper.getInstance(getContext());
        String firstDate = db.getFirstRecordDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(firstDate,new ParsePosition(0));

        Calendar lateCal = Calendar.getInstance();
        Calendar earlyCal = Calendar.getInstance();
        Calendar firstCal = Calendar.getInstance();
        earlyCal.add(Calendar.DAY_OF_YEAR, -daysBack);
        firstCal.setTime(date);


        
        while(firstCal.compareTo(lateCal) < 0)
        {


            StatPage page = new StatPage(getContext(),earlyCal.getTime(),lateCal.getTime());
            pages.add(page);
            earlyCal.add(Calendar.DAY_OF_YEAR,-daysBack);
            lateCal.add(Calendar.DAY_OF_YEAR,-daysBack);
        }

        mAdapter = new RecycleAdapter(layout.getContext(),pages);

        mRecyclerView.setAdapter(mAdapter);


        return (layout);
    }

}
