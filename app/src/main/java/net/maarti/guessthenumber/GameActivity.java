package net.maarti.guessthenumber;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import net.maarti.guessthenumber.game.Difficulty;
import net.maarti.guessthenumber.game.Game;
import net.maarti.guessthenumber.model.ScoreContract;
import net.maarti.guessthenumber.utility.MultimediaManager;
import net.maarti.guessthenumber.utility.Utility;
import net.maarti.guessthenumber.view.ChronometerMilli;
import net.maarti.guessthenumber.view.RangeSeekBar;

import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getName()+"aze";    //Pour retrouver les logs facilement
    TextView wSign = null;
    TextView wIndication = null;
    TextView wNbTry = null;
    TextView wNumberIndication = null;
    EditText wNumber = null;
    Button wSubmit = null;
    //Chronometer wChrono = null;
    ChronometerMilli wChrono = null;
    RangeSeekBar<Integer> wRangeSeekBar = null;
    //ScoreDbHelper db = new ScoreDbHelper(this);
    private MediaPlayer mpSubmitNumber1, mpSubmitNumber2, mpWin;

    Game game;
    int difficulty = Difficulty.DIFFICULTY_DEFAULT;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences preferences;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Pub interstitielle
        // TODO Keyboard view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Recup la difficulté dans l'intent
        difficulty = getIntent().getIntExtra(Difficulty.DIFFICULTY_LABEL,Difficulty.DIFFICULTY_DEFAULT);

        // Inflate views

        wSign = (TextView) findViewById(R.id.textViewSign);
        wNumberIndication = (TextView) findViewById(R.id.textViewNumberIndication);
        wIndication = (TextView) findViewById(R.id.textViewIndication);
        wNumber = (EditText) findViewById(R.id.editTextNumber);
        wSubmit = (Button) findViewById(R.id.buttonSubmit);
        wNbTry = (TextView) findViewById(R.id.nbtry);
        //wChrono = (Chronometer) findViewById(R.id.chronometer);
        wChrono = (ChronometerMilli) findViewById(R.id.chronometerMilli);
        AdView wBanner = (AdView) findViewById(R.id.bannerGame);

        // Taille max du champ de saisi du nombre
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(Difficulty.getNumberLength(difficulty));
        wNumber.setFilters(filter);

        // On créer la jauge indicative et on l'injecte dans le layout
        wRangeSeekBar = new RangeSeekBar<>(Difficulty.getMin(difficulty), Difficulty.getMax(difficulty), getApplicationContext());
        wRangeSeekBar.setEnabled(false);
        ViewGroup linear2 = (ViewGroup) findViewById(R.id.linear2);
        linear2.addView(wRangeSeekBar);

        // Init le jeu
        initGame();

        // Chargement des sons
        mpSubmitNumber1 = MediaPlayer.create(getApplicationContext(), R.raw.submitnumber01);
        mpSubmitNumber2 = MediaPlayer.create(getApplicationContext(), R.raw.submitnumber01);
        mpWin = MediaPlayer.create(getApplicationContext(), R.raw.win02);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        // Chargement de la bannière pub
        wBanner.loadAd(Utility.buildAdRequest());
    }

    private void initGame(){
        game = new Game(difficulty);
        Log.d(TAG, "Nombre à trouver : " + game.getNumberToGuess());

        // Affichage des view qui ont été masquées en cas de victoire
        wNumber.setVisibility(View.VISIBLE);
        wSubmit.setVisibility(View.VISIBLE);
        wNbTry.setText(getResources().getQuantityString((R.plurals.nbTry), game.getNbStep(), game.getNbStep()));


        // Création des listeners
        TextWatcher lNumber = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // On vérifie que le nombre saisi est inférieur au max.
                if (!s.toString().equals(""))
                {
                    int nbSaisi = Integer.parseInt(s.toString());
                    if (nbSaisi>game.getMax()){
                        Toast.makeText(GameActivity.this, getResources().getString(R.string.toast_valid_number_value, game.getMin()+1, game.getMax()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        View.OnFocusChangeListener listnerOnChangeNumber = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Ferme le clavier
                if (v.getId() == R.id.editTextNumber && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        };

        // Ajout des listeners aux widgets
        wNumber.addTextChangedListener(lNumber);
        wNumber.setOnFocusChangeListener(listnerOnChangeNumber);

        updateNumberIndication();

        // Démarrage des chrono (chrono affiché et chrono interne => pour les dizièmes de seconde))
        wChrono.start();
        startTime = System.currentTimeMillis();
    }


    /**
     * Met à jour l'affichage de l'indicateur "0 < ? < 100"
     */
    private void updateNumberIndication(){
        wNumberIndication.setText(getResources().getString(R.string.numberIndication, game.getNearestMin(), game.getNearestMax()));
    }

    /**
     * Called when a number is submited to update the UI
     */
    private void submitNumber(int nbSaisi) {
        // Valeur incorrecte
        if (nbSaisi < 1 || nbSaisi > game.getMax()) {
            Toast.makeText(GameActivity.this, getResources().getString(R.string.toast_valid_number_value, game.getMin() + 1, game.getMax()), Toast.LENGTH_SHORT).show();

            // Nombre non-gagnant
        } else if (!game.submitNumber(nbSaisi)) {
            if (nbSaisi < game.getNumberToGuess()) {
                wSign.setText(R.string.signPlus);
                wIndication.setText(getResources().getString(R.string.more, nbSaisi));
            } else {
                wSign.setText(R.string.signMinus);
                wIndication.setText(getResources().getString(R.string.less, nbSaisi));
            }
            shake(R.id.textViewSign);
            wNumber.setText("");
            wRangeSeekBar.setSelectedMinValue(game.getNearestMin());
            wRangeSeekBar.setSelectedMaxValue(game.getNearestMax());
            updateNumberIndication();
            if (mpSubmitNumber1.isPlaying())
                MultimediaManager.play(getApplicationContext(), mpSubmitNumber2);
            else
                MultimediaManager.play(getApplicationContext(), mpSubmitNumber1);

            // Nombre gagnant
        } else {
            wChrono.stop();
            String totalTime = game.setTotalTime(System.currentTimeMillis()-startTime);

            wChrono.setText(totalTime);
            wSign.setText(R.string.signEqual);
            // TODO a corriger
            wIndication.setText("");

            wNumber.clearFocus();
            wNumber.setVisibility(View.INVISIBLE);
            wSubmit.setVisibility(View.INVISIBLE);
            wNumberIndication.setText(String.format("%d", game.getNumberToGuess()));

            onWin();
        }
        wNbTry.setText(getResources().getQuantityString((R.plurals.nbTry), game.getNbStep(), game.getNbStep()));
        Log.i(TAG, "Range=(" + wRangeSeekBar.getAbsoluteMinValue() + "-" + wRangeSeekBar.getAbsoluteMaxValue() + ") Select=(" + wRangeSeekBar.getSelectedMinValue() + "-" + wRangeSeekBar.getSelectedMaxValue() + ") Near=(" + game.getNearestMin() + "-" + game.getNearestMax() + ")");
    }

    /**
     * When the right number is guessed
     */
    private void onWin(){
        // Récupération du pseudo du joueur
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(MainMenuActivity.USERNAME_LABEL, getString(R.string.defautUsername));

        // Ajout du score dans la BDD
        ContentValues scoreValues = new ContentValues();
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_USERNAME,username);
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_CHRONO,String.valueOf(wChrono.getText()));
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_TRIES,game.getNbStep());
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_DIFFICULTY,String.valueOf(difficulty));
        getContentResolver().insert(ScoreContract.ScoreEntry.CONTENT_URI,scoreValues);
        //db.addScore(new Score(username, String.valueOf(wChrono.getText()), game.getNbStep(), String.valueOf(difficulty)));

        // Affichage de la popup de victoire
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert   .setTitle(R.string.victory)
                .setMessage(String.format(getResources().getString(R.string.win), username, wChrono.getText()))
                .setCancelable(false);
        alert.setNeutralButton(R.string.home, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
        });

        alert.setNegativeButton(R.string.scores, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });
        alert.setPositiveButton(R.string.playAgain, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // On lance la GameActivity
                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                intent.putExtra(Difficulty.DIFFICULTY_LABEL, difficulty);
                startActivity(intent);
            }
        });
        alert.create().show();

        // On joue le son de la victoire et émet une vibration
        MultimediaManager.play(getApplicationContext(), mpWin);
        MultimediaManager.vibrate(getApplicationContext(), 400);

        handleAchievements();
    }
    /**
     * When a game is finished, we call this method to check whether achievements has been earned or not.
     */
    private void handleAchievements(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            int second = (int) TimeUnit.MILLISECONDS.toSeconds(game.getTotalTime());

            if (this.difficulty == Difficulty.EASY) {
                Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_easy), game.getTotalTime());
                Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_easy_peasy));
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_easy_peasy_20), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_easy_peasy_100), 1);
                if (second < 10)
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_not_so_easy___));
            } else if (difficulty == Difficulty.MEDIUM) {
                Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_medium), game.getTotalTime());
                Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_medium));
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_medium_20), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_medium_100), 1);
                if (second < 15)
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_lightning_fast));
            } else if (difficulty == Difficulty.HARD) {
                Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_hard), game.getTotalTime());
                Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_hard_day));
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_hard_day_20), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_hard_day_100), 1);
                if (second < 15)
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_hard_as_hell_));
            }
            if (game.getNbStep() <= 3)
                Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_have_you_considered_playing_the_lottery_));
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_earn_achievements_by_connecting,Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void handleScores(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_best_times_easy), game.getTotalTime());
        }
    }*/

    /**
     * Shake the view quickly right/left, when number is submited
     */
    private void shake(int viewId){
        Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        findViewById(viewId).startAnimation(shake);
    }

    public void onClickSubmitNumber(View view) {
        if (!wNumber.getText().toString().equals("")) {
            int nbSaisi = Integer.parseInt(wNumber.getText().toString());
            submitNumber(nbSaisi);
        }else{
            Toast.makeText(GameActivity.this, R.string.toast_valid_enter_number, Toast.LENGTH_SHORT).show();
        }
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

}
