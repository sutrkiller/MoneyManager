package pv239.fi.muni.cz.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.util.HashMap;

import pv239.fi.muni.cz.moneymanager.adapter.CategoriesDbAdapter;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;
import pv239.fi.muni.cz.moneymanager.helper.BackgroundContainer;
import pv239.fi.muni.cz.moneymanager.helper.SwipeDetector;
import pv239.fi.muni.cz.moneymanager.model.Category;


/**
 * Fragment holding list of categories
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public class CategoriesFragment extends Fragment {

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    ListView listView;
    CategoriesDbAdapter adapter;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<>();


    private OnCategoriesInteractionListener mListener;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop<0) {
                mSwipeSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
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
                    float deltaX = x-mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs>mSwipeSlop) {
                            mSwiping = true;
                            listView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX(x-mDownX);
//                        Log.i("x: ", String.valueOf(x));
//                        Log.i("mDownX: ", String.valueOf(mDownX));
//                        Log.i("translationX: ", String.valueOf((int)v.getTranslationX()));
                        mBackgroundContainer.showBackground(v.getTop(),v.getHeight(),(int)(v.getTranslationX()), v.getWidth(), v.getWidth());
                        mBackgroundContainer.invalidate();
                        v.setAlpha(1-deltaXAbs/v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    if (mSwiping) {
                        float x = event.getX()+v.getTranslationX();float deltaX = x - mDownX;
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

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoriesFragment.
     */
    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        // mParam1 = getArguments().getString(ARG_PARAM1);
        // mParam2 = getArguments().getString(ARG_PARAM2);
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBackgroundContainer = (BackgroundContainer) getView().findViewById(R.id.listViewBackgroundCategories);
        listView = (ListView) getView().findViewById(R.id.categories_list_view);
        MMDatabaseHelper sloh = MMDatabaseHelper.getInstance(getActivity());
        Cursor allCategories = sloh.getAllCategories();
        adapter = new CategoriesDbAdapter(getActivity(), allCategories, 0, mTouchListener);
        ImageView view = new ImageView(getActivity());
        view.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.overscoller_drawable) /*getResources().getDrawable(R.drawable.overscoller_drawable)*/);
        listView.addFooterView(view);
        listView.setAdapter(adapter);
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        Log.i("Swipe", "left-right");
                    }
                }
            }
        });
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fabAddCategory);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabButtonPressed();
            }
        });
    }

    public void onFabButtonPressed() {
        if (mListener != null) {
            mListener.onCategoriesAddCategory();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoriesInteractionListener) {
            mListener = (OnCategoriesInteractionListener) context;
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

        final Category category = helper.getCategoryById(id);
        long result = helper.deleteCategory(id);
        adapter.swapCursor(helper.getAllCategories());


        if (result > 0)  {
            Snackbar.make(getView(),"Category deleted",Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            helper.addOrUpdateCategory(category);
                            adapter.swapCursor(helper.getAllCategories());
                        }
                    }).show();
        } else {
            Snackbar.make(getView(),"Category in use, delete the records first, please.",Snackbar.LENGTH_LONG).show();
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
    public interface OnCategoriesInteractionListener {
        // TODO: Update argument type and name
        void onCategoriesAddCategory();
    }
}
