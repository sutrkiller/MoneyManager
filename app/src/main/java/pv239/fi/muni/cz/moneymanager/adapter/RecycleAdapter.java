package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.TabFragments.StatPage;
import pv239.fi.muni.cz.moneymanager.model.Record;

/**
 * Adapter for RecycleView that holds each statistics page (swiping left and right)
 *
 * Created by Tobias Kamenicky 6/8/2016.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.CustomViewHolder> {
    private Context mContext;
    private List<StatPage> statPages;

    public RecycleAdapter(List<StatPage> list) {
        statPages = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup v,int i) {
        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.tab_fragment,v,false);
        mContext = v.getContext();
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        StatPage page = statPages.get(i);
        NumberFormat curFor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        curFor.setMaximumFractionDigits(2);

        customViewHolder.date.setText(page.getDate());

        List<String> headers = new ArrayList<>();
        Map<String,List<Record>> dataMap = new HashMap<>();
        headers.add("Incomes");
        headers.add("Expenses");
        dataMap.put(headers.get(0),page.getIncomesList());
        dataMap.put(headers.get(1),page.getExpensesList());

        CustomExpandableListAdapter adapter_main = new CustomExpandableListAdapter(mContext,headers,dataMap,true,page);
        customViewHolder.expandableListView.setAdapter(adapter_main);
        customViewHolder.expandableListView.expandGroup(0);
    }

    @Override
    public int getItemCount() {
        return statPages.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView date;
        protected ExpandableListView expandableListView;

        public CustomViewHolder(View view) {
            super(view);
            this.date = (TextView) view.findViewById(R.id.date);
            expandableListView = (ExpandableListView) view.findViewById(R.id.stats_expandableList);
        }
    }


}

