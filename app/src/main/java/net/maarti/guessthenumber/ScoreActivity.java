package net.maarti.guessthenumber;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import net.maarti.guessthenumber.model.ScoreDbHelper;
import net.maarti.guessthenumber.utility.MultimediaManager;
import net.maarti.guessthenumber.utility.Utility;

public class ScoreActivity extends AppCompatActivity {

    private ViewFlipper vFlipper;
    private MediaPlayer mpClic1;
    private MediaPlayer mpClic2;
    private SharedPreferences preferences;

    ScoreDbHelper db = new ScoreDbHelper(this);
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

        // Remplissage des listes des scores
        //int SCORE_COUNT = 20;
        String sortOrder = ScoreContract.ScoreEntry.COLUMN_CHRONO + " ASC, "+ ScoreContract.ScoreEntry.COLUMN_TRIES+" ASC";
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
                sortOrder);

        ScoreAdapter scoreAdapterEasy = new ScoreAdapter(this,scoreEasyCursor,0);
        ScoreAdapter scoreAdapterMedium = new ScoreAdapter(this,scoreMediumCursor,0);
        ScoreAdapter scoreAdapterHard = new ScoreAdapter(this,scoreHardCursor,0);

        vScoresEasy.setAdapter(scoreAdapterEasy);
        vScoresMedium.setAdapter(scoreAdapterMedium);
        vScoresHard.setAdapter(scoreAdapterHard);
       /* List<Score> listScores10 = db.getTopScores(10, SCORE_COUNT);
        List<Score> listScores20 = db.getTopScores(20, SCORE_COUNT);
        List<Score> listScores30 = db.getTopScores(30, SCORE_COUNT);

        List<HashMap<String, Score>> liste10 = new ArrayList<>();
        List<HashMap<String, Score>> liste20 = new ArrayList<>();
        List<HashMap<String, Score>> liste30 = new ArrayList<>();

        HashMap<String, Score> element;

        for (Score s : listScores10) {
            element = new HashMap<>();
            element.put("score", s);
            liste10.add(element);
        }
        for (Score s : listScores20) {
            element = new HashMap<>();
            element.put("score", s);
            liste20.add(element);
        }
        for (Score s : listScores30) {
            element = new HashMap<>();
            element.put("score", s);
            liste30.add(element);
        }

        ListAdapter adapter10 = new SimpleAdapter(
                this,
                liste10,
                android.R.layout.activity_list_item,
                new String[] {"score"},
                new int[] {android.R.id.text1, android.R.id.text2 }
        );
        ListAdapter adapter20 = new SimpleAdapter(
                this,
                liste20,
                android.R.layout.activity_list_item,
                new String[] {"score"},
                new int[] {android.R.id.text1, android.R.id.text2 }
        );
        ListAdapter adapter30 = new SimpleAdapter(
                this,
                liste30,
                android.R.layout.activity_list_item,
                new String[] {"score"},
                new int[] {android.R.id.text1, android.R.id.text2 }
        );

        vScoresEasy.setAdapter(adapter10);
        vScoresMedium.setAdapter(adapter20);
        vScores30.setAdapter(adapter30);*/

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
}
