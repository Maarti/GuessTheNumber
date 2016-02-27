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
import net.maarti.guessthenumber.utility.Utility;

/**
 * Created by Bryan MARTINET on 27/02/2016.
 */
public class ScoreAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 4;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private static final int VIEW_TYPE_THIRD = 2;
    private static final int VIEW_TYPE_OTHER = 3;

    public ScoreAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_FIRST :
                layoutId = R.layout.list_item_score_first;
                break;
            case VIEW_TYPE_SECOND :
                layoutId = R.layout.list_item_score_second;
                break;
            case VIEW_TYPE_THIRD :
                layoutId = R.layout.list_item_score_third;
                break;
            case VIEW_TYPE_OTHER :
                layoutId = R.layout.list_item_score;
                break;
            }

        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView vNumber = (TextView) view.findViewById(R.id.scoreNumber);
        TextView vUsername = (TextView) view.findViewById(R.id.scoreUsername);
        TextView vChrono = (TextView) view.findViewById(R.id.scoreChrono);
        TextView vNbTry = (TextView) view.findViewById(R.id.scoreNbTry);

        int position = cursor.getPosition()+1;
        String username = cursor.getString(ScoreContract.ScoreEntry.NUM_COL_USERNAME);
        String chrono = cursor.getString(ScoreContract.ScoreEntry.NUM_COL_CHRONO);
        String formattedChrono = Utility.formatTime(chrono);
        String numberOfTries = cursor.getString(ScoreContract.ScoreEntry.NUM_COL_TRIES);
        String tryNumberTxt = context.getResources().getQuantityString((R.plurals.tryNumber), Integer.parseInt(numberOfTries), Integer.parseInt(numberOfTries));

        vNumber.setText("#"+String.valueOf(position));
        vUsername.setText(username);
        vChrono.setText(formattedChrono);
        vNbTry.setText(tryNumberTxt);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return VIEW_TYPE_FIRST;
            case 1:
                return VIEW_TYPE_SECOND;
            case 2:
                return VIEW_TYPE_THIRD;
            default:
                return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
