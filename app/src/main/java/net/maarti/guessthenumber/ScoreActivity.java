package net.maarti.guessthenumber;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import net.maarti.guessthenumber.adapter.ScoreAdapter;
import net.maarti.guessthenumber.game.Difficulty;
import net.maarti.guessthenumber.model.ScoreContract;
import net.maarti.guessthenumber.utility.MultimediaManager;
import net.maarti.guessthenumber.utility.Utility;

public class ScoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SCORE_LOADER_EASY_ID = 0;
    private static final int SCORE_LOADER_MEDIUM_ID = 1;
    private static final int SCORE_LOADER_HARD_ID = 2;

    private ViewFlipper vFlipper;
    private MediaPlayer mpClic1;
    private MediaPlayer mpClic2;
    private SharedPreferences preferences;
    private  ScoreAdapter scoreAdapterEasy, scoreAdapterMedium, scoreAdapterHard;

  //  ScoreDbHelper db = new ScoreDbHelper(this);
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        ListView vScoresEasy = (ListView) findViewById(R.id.listViewScores10);
        ListView vScoresMedium = (ListView) findViewById(R.id.listViewScores20);
        ListView vScoresHard = (ListView) findViewById(R.id.listViewScores30);
        vFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        Button vButtonRight = (Button) findViewById(R.id.buttonRight);
        Button vButtonLeft = (Button) findViewById(R.id.buttonLeft);
        AdView wBanner = (AdView) findViewById(R.id.bannerScore);
        mpClic1 = MediaPlayer.create(getApplicationContext(),R.raw.clic);
        mpClic2 = MediaPlayer.create(getApplicationContext(), R.raw.clic);

        getLoaderManager().initLoader(SCORE_LOADER_EASY_ID, null, this);
        getLoaderManager().initLoader(SCORE_LOADER_MEDIUM_ID, null, this);
        getLoaderManager().initLoader(SCORE_LOADER_HARD_ID, null, this);

        // Remplissage des listes des scores
       /* String sortOrder = ScoreContract.ScoreEntry.COLUMN_CHRONO + " ASC, "+ ScoreContract.ScoreEntry.COLUMN_TRIES+" ASC";
        Cursor scoreEasyCursor = getContentResolver().query(
                ScoreContract.ScoreEntry.CONTENT_URI,
                null,
                ScoreContract.ScoreEntry.COLUMN_DIFFICULTY + " = "+Difficulty.EASY,
                null,
                sortOrder);
        Cursor scoreMediumCursor = getContentResolver().query(
                ScoreContract.ScoreEntry.CONTENT_URI,
                null,
                ScoreContract.ScoreEntry.COLUMN_DIFFICULTY + " = "+Difficulty.MEDIUM,
                null,
                sortOrder);
        Cursor scoreHardCursor = getContentResolver().query(
                ScoreContract.ScoreEntry.CONTENT_URI,
                null,
                ScoreContract.ScoreEntry.COLUMN_DIFFICULTY + " = "+Difficulty.HARD,
                null,
                sortOrder);*/

       /* ScoreAdapter scoreAdapterEasy = new ScoreAdapter(this,scoreEasyCursor,0);
        ScoreAdapter scoreAdapterMedium = new ScoreAdapter(this,scoreMediumCursor,0);
        ScoreAdapter scoreAdapterHard = new ScoreAdapter(this,scoreHardCursor,0);*/
        scoreAdapterEasy = new ScoreAdapter(this,null,0);
        scoreAdapterMedium = new ScoreAdapter(this,null,0);
        scoreAdapterHard = new ScoreAdapter(this,null,0);


        vScoresEasy.setAdapter(scoreAdapterEasy);
        vScoresMedium.setAdapter(scoreAdapterMedium);
        vScoresHard.setAdapter(scoreAdapterHard);

        // Gestion des boutons "suivant/précédent"
        vButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.setInAnimation(ScoreActivity.this, R.anim.slide_in_from_right);
                vFlipper.setOutAnimation(ScoreActivity.this, R.anim.slide_out_to_left);
                vFlipper.showNext();

                // On joue le son du clic
                if (mpClic1.isPlaying())
                    MultimediaManager.play(getApplicationContext(), mpClic2);
                else
                    MultimediaManager.play(getApplicationContext(), mpClic1);
            }
        });

        vButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.setInAnimation(ScoreActivity.this, R.anim.slide_in_from_left);
                vFlipper.setOutAnimation(ScoreActivity.this, R.anim.slide_out_to_right);
                vFlipper.showPrevious();

                // On joue le son du clic
                if (mpClic1.isPlaying())
                    MultimediaManager.play(getApplicationContext(), mpClic2);
                else
                    MultimediaManager.play(getApplicationContext(), mpClic1);
            }
        });

        // Chargement de la bannière pub
        wBanner.loadAd(Utility.buildAdRequest());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preferences.getBoolean(MainMenuActivity.PREF_AUTO_SIGNIN_LABEL,false))
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void onClickGoogleScore(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic1);

        // Si on est connecté avec google on affiche le Social Leaderboard
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            String scoreId = getString(R.string.leaderboard_easy);
            int activityId = 1;
            switch(view.getId()){
                case R.id.googleScoreEasy :
                    scoreId = getString(R.string.leaderboard_easy);
                    activityId = 1;
                    break;
                case R.id.googleScoreMedium :
                    scoreId = getString(R.string.leaderboard_medium);
                    activityId = 2;
                    break;
                case R.id.googleScoreHard :
                    scoreId = getString(R.string.leaderboard_hard);
                    activityId = 3;
                    break;
            }
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, scoreId), activityId);

        // Sinon on affiche un toast
        } else {
            Toast.makeText(getApplicationContext(),"Connect with Google+ to see world and social leaderboards", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ScoreContract.ScoreEntry.COLUMN_CHRONO + " ASC, "+ ScoreContract.ScoreEntry.COLUMN_TRIES+" ASC";
        int difficultyColumn = Difficulty.EASY;
        switch (id){
            case SCORE_LOADER_EASY_ID:
                difficultyColumn = Difficulty.EASY;
                break;
            case SCORE_LOADER_MEDIUM_ID:
                difficultyColumn = Difficulty.MEDIUM;
                break;
            case SCORE_LOADER_HARD_ID:
                difficultyColumn = Difficulty.HARD;
                break;
        }
        return new CursorLoader(this,
                ScoreContract.ScoreEntry.CONTENT_URI,
                null,
                ScoreContract.ScoreEntry.COLUMN_DIFFICULTY + " = "+difficultyColumn,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId())
        {
            case SCORE_LOADER_EASY_ID:
                scoreAdapterEasy.swapCursor(data);
                break;
            case SCORE_LOADER_MEDIUM_ID:
                scoreAdapterMedium.swapCursor(data);
                break;
            case SCORE_LOADER_HARD_ID:
                scoreAdapterHard.swapCursor(data);
                break;
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId())
        {
            case SCORE_LOADER_EASY_ID:
                scoreAdapterEasy.swapCursor(null);
                break;
            case SCORE_LOADER_MEDIUM_ID:
                scoreAdapterMedium.swapCursor(null);
                break;
            case SCORE_LOADER_HARD_ID:
                scoreAdapterHard.swapCursor(null);
                break;
        }
    }
}
