package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

import pv239.fi.muni.cz.moneymanager.adapter.RecordsDbAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.helper.BackgroundContainer;
import pv239.fi.muni.cz.moneymanager.helper.SwipeDetector;
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
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
  //  private String mParam1;
   // private String mParam2;


    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    ListView listView;
    RecordsDbAdapter adapter;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

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
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
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

        mBackgroundContainer = (BackgroundContainer) getView().findViewById(R.id.listViewBackgroundRecords);
        listView = (ListView)getView().findViewById(R.id.records_list_view);
        //listView.setAdapter(new RecordsAdapter(this.getActivity(), Record.getTestingData()));
        MMDatabaseHelper sloh = MMDatabaseHelper.getInstance(getActivity());
        Cursor allRecords = sloh.getAllRecordsWithCategories();
        adapter = new RecordsDbAdapter(getActivity(),allRecords,0, mTouchListener);

//        TextView footer = new TextView(getActivity());
//        int height = (int) (5*72 * getResources().getDisplayMetrics().density);
//        footer.setHeight(height);
//        listView.addFooterView(new TextView(getActivity()));

        ImageView view = new ImageView(getActivity());
        view.setImageDrawable(getResources().getDrawable(R.drawable.overscoller_drawable));
        listView.addFooterView(view);
        listView.setAdapter(adapter);


        final SwipeDetector swipeDetector = new SwipeDetector();
        //listView.setOnTouchListener(mTouchListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Record record = (Record) parent.getItemAtPosition(position);
//                String value = record.dateTime + " " + record.value + record.currency;
//                Toast.makeText(getActivity(), value + " clicked!", Toast.LENGTH_SHORT).show();
                if (swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction()== SwipeDetector.Action.RL) {
                        Log.i("SWIIIIPE","OH YEEEAH");
                    }
                }

            }
        });


        FloatingActionButton button = (FloatingActionButton)getView().findViewById(R.id.fabAddRecord);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabButtonPressed();
            }
        });
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onFabButtonPressed() {
        if (mListener != null) {
            mListener.onRecordsAddRecord();
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






    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(getActivity()).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            listView.requestDisallowInterceptTouchEvent(true);
                            //mBackgroundContainer.showBackground(v.getTop(), v.getHeight(),  (int)v.,(int)deltaX, v.getWidth());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                    //  Log.i("x: ", String.valueOf(x));
                    //  Log.i("mDownX: ", String.valueOf(mDownX));
                    //  Log.i("translationX: ", String.valueOf((int)v.getTranslationX()));
                        mBackgroundContainer.showBackground(v.getTop(), v.getHeight(),  (int)(v.getTranslationX()), v.getWidth(), v.getWidth());
                        mBackgroundContainer.invalidate();
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        listView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(listView, v);
                                        } else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            listView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = adapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = listView.getPositionForView(viewToRemove);
        //mAdapter.remove(mAdapter.getItem(position));
        //Log.i(String.valueOf(adapter.getItemId(position)),"blsbal");
        final MMDatabaseHelper helper = MMDatabaseHelper.getInstance(getActivity());
        long id = adapter.getItemId(position);
        final Record record = helper.getRecordById(id);
        long result = helper.deleteRecord(id);
        adapter.swapCursor(helper.getAllRecordsWithCategories());
        if (result > 0)  {
            Snackbar.make(getView(),"Record deleted",Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            helper.addRecord(record);
                            adapter.swapCursor(helper.getAllRecordsWithCategories());
                        }
                    }).show();
        }

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        listView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    listView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
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
        void onRecordsAddRecord();
    }
}
