package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import pv239.fi.muni.cz.moneymanager.adapter.ExpensesSumStatsAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbToStatsAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.IncomeSumStatsAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;


/**
 * Fragment holding all statistics about data.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView incomeListView;
    private ListView expensesListView;

    private OnStatsInteractionListener mListener;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        /* TO DO LIST:
           - aktivovat buttony   ???Swipe efektom???
           - fragment pre detail Incomes a Expenses
           - ak budu buttony tak prepracovat kod, aby filtroval podla mesiacov (nateray nastavene umelo)
           - doriesit preco nechce fungovat pre 1. sekciu match parent width
           - relativnu height pre 2. a 3. sekciu
         */
        //Setting range dates
        TextView start = (TextView) getView().findViewById(R.id.startMonthStats);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        start.setText(cal.getActualMinimum(Calendar.DAY_OF_MONTH)+"."+cal.get(Calendar.MONTH)+"."+cal.get(Calendar.YEAR));

        TextView end = (TextView) getView().findViewById(R.id.endMonthStats);
        end.setText(cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"."+cal.get(Calendar.MONTH)+"."+cal.get(Calendar.YEAR));

        // Setting Starting balance. Ending balance is set as - for current month because its never
        // closed as default.
        //Creating income list
        incomeListView = (ListView) getView().findViewById(R.id.listViewIncomeStats);
        MMDatabaseHelper sloh = MMDatabaseHelper.getInstance(getActivity());
        Cursor incomeRecords = sloh.getAllIncomesRecords();
        RecordsDbToStatsAdapter incomeAdapter = new RecordsDbToStatsAdapter(this.getContext(), incomeRecords, 0);
        incomeListView.setAdapter(incomeAdapter);

        expensesListView = (ListView) getView().findViewById(R.id.listViewExpences);
        //Creating expenses list
        Cursor expensesRecords = sloh.getAllExpensesRecords();
        RecordsDbToStatsAdapter expensesAdapter = new RecordsDbToStatsAdapter(this.getContext(), expensesRecords, 0);
        expensesListView.setAdapter(expensesAdapter);

        //Fetching Summaries of incomes and expenses
        TextView incSum = (TextView) getView().findViewById(R.id.incomeSumStats);
        TextView expSum = (TextView) getView().findViewById(R.id.expenseSumStats);
        BigDecimal incValue = new BigDecimal(sloh.getIncomesSum().toString());
        BigDecimal expValue = new BigDecimal(sloh.getExpensesSum().toString());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        incSum.setText(format.format(incValue.abs().setScale(2).doubleValue()));
        expSum.setText(format.format(expValue.abs().setScale(2).doubleValue()));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onStatsInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStatsInteractionListener) {
            mListener = (OnStatsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        void onStatsInteraction(Uri uri);
    }
}
