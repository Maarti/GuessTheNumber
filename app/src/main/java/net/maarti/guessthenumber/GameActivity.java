package net.maarti.guessthenumber;

import android.app.AlertDialog;
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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.maarti.guessthenumber.game.Difficulty;
import net.maarti.guessthenumber.game.Game;
import net.maarti.guessthenumber.model.DatabaseHandler;
import net.maarti.guessthenumber.model.Score;
import net.maarti.guessthenumber.view.RangeSeekBar;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getName()+"aze";    //Pour retrouver les logs facilement
    TextView wSign = null;
    TextView wIndication = null;
    TextView wNbTry = null;
    TextView wNumberIndication = null;
    EditText wNumber = null;
    Button wSubmit = null;
    Chronometer wChrono = null;
    RangeSeekBar<Integer> wRangeSeekBar = null;
    DatabaseHandler db = new DatabaseHandler(this);
    private MediaPlayer mpSubmitNumber1, mpSubmitNumber2, mpWin;

    Game game;
    int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Timer affiché dans l'écran de victoire
        // TODO tableau des scores
        // TODO Pub interstielle
        // TODO Keyboard view
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        // Recup la difficulté dans les préférences sauvegardées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        difficulty = preferences.getInt(Difficulty.DIFFICULTY_LABEL,Difficulty.DIFFICULTY_DEFAULT);

        // Inflate views
        setContentView(R.layout.activity_game);
        wSign = (TextView) findViewById(R.id.textViewSign);
        wNumberIndication = (TextView) findViewById(R.id.textViewNumberIndication);
        wIndication = (TextView) findViewById(R.id.textViewIndication);
        wNumber = (EditText) findViewById(R.id.editTextNumber);
        wSubmit = (Button) findViewById(R.id.buttonSubmit);
        wNbTry = (TextView) findViewById(R.id.nbtry);
        wChrono = (Chronometer) findViewById(R.id.chronometer);
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

        // Chargement de la bannière pub
        wBanner.loadAd(new AdRequest.Builder().build());
    }

        private void initGame(){
            game = new Game(difficulty);
            Log.d(TAG, "Nombre à trouver : " + game.getNumberToGuess());

            // Affichage des view qui ont été masquées en cas de victoire
            wNumber.setVisibility(View.VISIBLE);
            wSubmit.setVisibility(View.VISIBLE);
            wNbTry.setText(getResources().getQuantityString((R.plurals.nbTry), game.getNbStep(), game.getNbStep()));
            wChrono.start();

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
        }


    /**
     * Met à jour l'affichage de l'indicateur "0 < ? < 100"
     */
    private void updateNumberIndication(){
        wNumberIndication.setText(getResources().getString(R.string.numberIndication, game.getNearestMin(), game.getNearestMax()));
    }

    private void submitNumber(int nbSaisi) {
        // Valeur incorrecte
        if (nbSaisi < 1 || nbSaisi > game.getMax()) {
            Toast.makeText(GameActivity.this, getResources().getString(R.string.toast_valid_number_value,game.getMin()+1,game.getMax()), Toast.LENGTH_SHORT).show();

        // Nombre non-gagnant
        } else if(!game.submitNumber(nbSaisi)) {
            if(nbSaisi<game.getNumberToGuess()) {
                wSign.setText(R.string.signPlus);
                wIndication.setText(getResources().getString(R.string.more, nbSaisi));
            }else{
                wSign.setText(R.string.signMinus);
                wIndication.setText(getResources().getString(R.string.less, nbSaisi));
            }
            shake(R.id.textViewSign);
            wNumber.setText("");
            wRangeSeekBar.setSelectedMinValue(game.getNearestMin());
            wRangeSeekBar.setSelectedMaxValue(game.getNearestMax());
            updateNumberIndication();
            if(mpSubmitNumber1.isPlaying())
                mpSubmitNumber2.start();
            else
                mpSubmitNumber1.start();

        // Nombre gagnant
        }else{
            wChrono.stop();
            wSign.setText(R.string.signEqual);
            wIndication.setText(R.string.win);
            wNumber.clearFocus();
            wNumber.setVisibility(View.INVISIBLE);
            wSubmit.setVisibility(View.INVISIBLE);
            wNumberIndication.setText(String.format("%d", game.getNumberToGuess()));

            // Récupération du pseudo du joueur
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String username = preferences.getString(MainMenuActivity.USERNAME_LABEL, getString(R.string.defautUsername));
            if (username.equals("")) {
                username = getString(R.string.defautUsername);
            }

            db.addScore(new Score(username, String.valueOf(wChrono.getText()), game.getNbStep(), String.valueOf(difficulty)));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert
                    .setTitle(R.string.victory)
                    .setMessage(R.string.win)
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
                    startActivity(intent);
                }
            });

            AlertDialog a = alert.create();
            a.show();
            mpWin.start();
        }

        wNbTry.setText(getResources().getQuantityString((R.plurals.nbTry), game.getNbStep(), game.getNbStep()));
        Log.i(TAG, "Range=(" + wRangeSeekBar.getAbsoluteMinValue() + "-" + wRangeSeekBar.getAbsoluteMaxValue() + ") Select=(" + wRangeSeekBar.getSelectedMinValue() + "-" + wRangeSeekBar.getSelectedMaxValue() + ") Near=(" + game.getNearestMin() + "-" + game.getNearestMax() + ")");
    }

    private void shake(int viewId){
        Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        findViewById(viewId).setAnimation(shake);
    }

    public void onClickSubmitNumber(View view) {
        if (!wNumber.getText().toString().equals("")) {
            int nbSaisi = Integer.parseInt(wNumber.getText().toString());
            submitNumber(nbSaisi);
        }else{
            Toast.makeText(GameActivity.this, R.string.toast_valid_enter_number, Toast.LENGTH_SHORT).show();
        }
    }


}
