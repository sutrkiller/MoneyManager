package pv239.fi.muni.cz.moneymanager.TabFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.adapter.RecycleAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.AdapterParameterObject;

/**
 * Page containing RecycleView in statistics
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 6/8/2016.
 */
public class RecyclePage extends Fragment {

    private AdapterParameterObject mObject;

    public RecyclePage() {
    }

    public static RecyclePage newInstance() {
        return (new RecyclePage());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                //int scrollTo = (int) (scroll +(-Math.signum(scroll))* mScreenWidth);
                float minAlpha = 1F;
                float aTrans = minAlpha/(float)mScreenWidth * Math.abs(scroll);
                float bTrans = (1-minAlpha) + aTrans;

                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                View a =  manager.findViewByPosition(fromPos);
                View b =  manager.findViewByPosition(toPos);
                if (a==null) return;
                if (b==null) return;
                View aD = a.findViewById(R.id.tab_divider_bottom);
                View bD = b.findViewById(R.id.tab_divider_bottom);

                if (fromPos!=toPos) {
                    if (aD != null) {
                        aD.setAlpha(1 - aTrans);
                    }
                    if (bD != null)  {
                        bD.setAlpha(bTrans);
                    }
                } else {
                    if (aD!=null) {
                        aD.setAlpha(1);
                    }
                }
            }
        });
    if (mObject==null) return layout;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(layout.getContext(), LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new RecycleAdapter(preparePagesOther());
        mRecyclerView.setAdapter(mAdapter);

        return layout;
    }

    private List<StatPage> preparePagesOther() {
        List<StatPage> pages = new ArrayList<>();
        MMDatabaseHelper db = MMDatabaseHelper.getInstance(getContext());
        int version = mObject.getVersion();
        int tabNumber = mObject.getPageNumber();
        String firstDate = db.getFirstRecordDate();
        SimpleDateFormat format = new SimpleDateFormat(getActivity().getString(R.string.db_date_format), Locale.getDefault());
        if (firstDate == null) firstDate = format.format(Calendar.getInstance().getTime());
        Date date = format.parse(firstDate, new ParsePosition(0));

        int length = Calendar.WEEK_OF_YEAR;
        int coal = Calendar.DAY_OF_WEEK;
        int coalSet = 2;

        switch (tabNumber) {
            case 0:    break;
            case 1: length= Calendar.MONTH; coal=Calendar.DAY_OF_MONTH; coalSet=1; break;
            case 2: length= Calendar.YEAR; coal=Calendar.DAY_OF_YEAR; coalSet=1; break;
        }

        Calendar lastCal = Calendar.getInstance();
        lastCal.setFirstDayOfWeek(Calendar.MONDAY);
        lastCal.set(Calendar.HOUR_OF_DAY, 0);
        lastCal.set(Calendar.MINUTE, 0);
        lastCal.set(Calendar.SECOND, 0);
        lastCal.set(Calendar.MILLISECOND, 0);
        lastCal.set(coal, coalSet);
        lastCal.add(length,1);
        Calendar earlyCal = Calendar.getInstance();
        earlyCal.setFirstDayOfWeek(Calendar.MONDAY);
        earlyCal.setTime(lastCal.getTime());
        earlyCal.add(length,-1);
        lastCal.add(Calendar.MILLISECOND,-1);
        Calendar firstCal = Calendar.getInstance();
        firstCal.setTime(date);

        while (firstCal.compareTo(lastCal) < 0) {
            StatPage page = new StatPage(getContext(), earlyCal.getTime(), lastCal.getTime(),tabNumber,version);
            pages.add(page);
            earlyCal.add(length, -1);
            lastCal.add(length, -1);
        }
        return pages;
    }
}
