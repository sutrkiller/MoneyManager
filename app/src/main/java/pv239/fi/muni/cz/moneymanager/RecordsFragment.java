package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import pv239.fi.muni.cz.moneymanager.adapter.RecordsAdapter;
import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.model.Category;
import pv239.fi.muni.cz.moneymanager.model.Record;


/**
 * Fragment holding list of records.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class RecordsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private SQLiteDatabase readableDb;
    //private Cursor allRecords;

    private OnRecordsInteractionListener mListener;

    public RecordsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordsFragment newInstance(String param1, String param2) {
        RecordsFragment fragment = new RecordsFragment();
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
/*
        try {
            SQLiteOpenHelper sloh = MMDatabaseHelper.getInstance(getActivity());
            readableDb = sloh.getReadableDatabase();
            String cols =MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ID+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_VAL+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CURR+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+", "
                    +MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_ITEM+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_NAME+", "
                    +MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_DET;
            allRecords = readableDb.rawQuery("SELECT "+cols+
                    " FROM "+MMDatabaseHelper.TABLE_RECORD+", "+MMDatabaseHelper.TABLE_CATEGORY+
                    " WHERE "+MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_CAT_ID_FK+"="+MMDatabaseHelper.TABLE_CATEGORY+"."+MMDatabaseHelper.KEY_CAT_ID+
                    " ORDER BY "+MMDatabaseHelper.TABLE_RECORD+"."+MMDatabaseHelper.KEY_REC_DATE+" DESC",null);
        } catch (SQLiteException ex) {
            throw ex;
        }
*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView)getView().findViewById(R.id.records_list_view);
        //listView.setAdapter(new RecordsAdapter(this.getActivity(), Record.getTestingData()));
        MMDatabaseHelper sloh = MMDatabaseHelper.getInstance(getActivity());
        Cursor allRecords = sloh.getAllRecordsWithCategories();
        RecordsDbAdapter adapter = new RecordsDbAdapter(getActivity(),allRecords,0);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Record record = (Record) parent.getItemAtPosition(position);
//                String value = record.dateTime + " " + record.value + record.currency;
//                Toast.makeText(getActivity(), value + " clicked!", Toast.LENGTH_SHORT).show();

            }
        });

        FloatingActionButton button = (FloatingActionButton)getView().findViewById(R.id.fabAddRecord);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(null);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRecordsInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecordsInteractionListener) {
            mListener = (OnRecordsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //allRecords.close();
        //readableDb.close();

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
    public interface OnRecordsInteractionListener {
        // TODO: Update argument type and name
        void onRecordsInteraction(Uri uri);
    }
}
