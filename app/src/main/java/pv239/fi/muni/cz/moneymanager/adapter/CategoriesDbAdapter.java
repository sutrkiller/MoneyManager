package pv239.fi.muni.cz.moneymanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 5/10/2016
 *
 * This class serves as adapter for ListView of categories loaded from DB.
 */
public class CategoriesDbAdapter extends CursorAdapter {
    private LayoutInflater inflater;
    private View.OnTouchListener mListener;


    public CategoriesDbAdapter(Context context, Cursor c, int flags, View.OnTouchListener mListenr) {
        super(context, c, flags);
        mListener = mListenr;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rView = inflater.inflate(R.layout.list_item_category,parent,false);
        rView.setOnTouchListener(mListener);
        return rView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(1);
        String details = cursor.getString(2);
        ImageView vicon = (ImageView) view.findViewById(R.id.category_item_icon);
        TextView vname = (TextView) view.findViewById(R.id.category_item_name);
        TextView vdetails = (TextView) view.findViewById(R.id.category_item_details);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color2 = generator.getColor(name);
        TextDrawable draw = TextDrawable.builder().buildRound(String.valueOf(name.charAt(0)).toUpperCase(),color2);

        vname.setText(name);
        vdetails.setText(details==null || details.isEmpty() ? "" : "("+ details+")");
        vicon.setImageDrawable(draw);
    }

}
