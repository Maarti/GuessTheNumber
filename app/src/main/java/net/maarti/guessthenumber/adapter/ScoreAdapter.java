package net.maarti.guessthenumber.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.maarti.guessthenumber.R;
import net.maarti.guessthenumber.model.ScoreContract;

/**
 * Created by Bryan MARTINET on 27/02/2016.
 */
public class ScoreAdapter extends CursorAdapter {

    public ScoreAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_score;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
       /* ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);*/
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView vUsername = (TextView) view.findViewById(R.id.textViewUsername);
        String username = cursor.getString(ScoreContract.ScoreEntry.NUM_COL_USERNAME);
        vUsername.setText(username);
    }
}
