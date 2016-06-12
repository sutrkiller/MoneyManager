package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.series.LineGraphSeries;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

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


        final RecyclerViewPager mRecyclerView = (RecyclerViewPager) layout.findViewById(R.id.fragRecycleView);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int mScreenWidth = displaymetrics.widthPixels;
        final int[] lastPosition = {0};

        Log.i("Screen width", String.valueOf(mScreenWidth));
        final int[] overallXScroll = {0};
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll[0] = overallXScroll[0] + dx;
                int position = mRecyclerView.getCurrentPosition();
                int scroll = (overallXScroll[0] + (lastPosition[0])*mScreenWidth) % mScreenWidth;
                if (scroll==0) lastPosition[0] = position;
                int fromPos = lastPosition[0];
                int toPos = scroll<0 ? lastPosition[0]+1 : ( scroll>0 ? lastPosition[0]-1 : lastPosition[0]);
                int scrollTo = (int) (scroll +(-Math.signum(scroll))* mScreenWidth);
                //if (fromPos!=toPos) {
//                    Log.i("check","overall X  = " + scroll);
//                    Log.i("check","position  = " + position);
//                    Log.i("check","lastPosition  = " + lastPosition[0]);
//                    Log.i("check","fromPos  = " + fromPos);
//                    Log.i("check","toPos  = " + toPos);
//                    Log.i("check","scrollTo  = "+  scrollTo);
                    //Log.i("check","testScroll  = " );
                    float minAlpha = 1F;
                    float aTrans = minAlpha/(float)mScreenWidth * Math.abs(scroll);
                    float bTrans = (1-minAlpha) + aTrans;



                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                View a =  manager.findViewByPosition(fromPos);
                View b =  manager.findViewByPosition(toPos);
                View aD = a.findViewById(R.id.tab_divider_bottom);
                View bD = b.findViewById(R.id.tab_divider_bottom);

                if (fromPos!=toPos) {
                    if (aD != null) {
                        aD.setAlpha(1 - aTrans);
                        //Log.i("check","alphaSetA  = "+ (1-aTrans));
                    }
                    if (bD != null)  {
                        bD.setAlpha(bTrans);
                        //Log.i("check","alphaSetB  = "+ (bTrans));
                    }

                } else {
                    if (aD!=null) {
                        aD.setAlpha(1);
                        //Log.i("check","alphaSetAOne  = "+ (1));
                    }
                }
                    //Log.i("check","position  = " + mRecyclerView.getCurrentPosition());
                //}

                //Log.i("check","current scroll  = " + ((-mRecyclerView.getCurrentPosition()+1)*mScreenWidth - mScreenWidth/2));

                //Log.i("Translation", String.valueOf(a.getTranslationX()));
            }
        });
    if (mObject==null) return layout;
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

            pages.clear();
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
