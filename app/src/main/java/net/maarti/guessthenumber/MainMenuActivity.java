package net.maarti.guessthenumber;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.maarti.guessthenumber.game.Difficulty;

public class MainMenuActivity extends AppCompatActivity {

    public static final String USERNAME_LABEL = "username";
    private EditText wName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        wName = (EditText) findViewById(R.id.ediTextName);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(MainMenuActivity.USERNAME_LABEL,"");
        wName.setText(username);
    }

    public void onClickNewGame(View view) {
        // En fonction de la vue qui appelle la méthode, on change la difficulté
        int difficulty = Difficulty.getIdFromView(view);

        // On stock la difficulté dans les param de l'appli
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Difficulty.DIFFICULTY_LABEL, difficulty);
        editor.apply();

        // On stock le username
        saveUsername();

        // On lance la GameActivity
        Intent intent = new Intent(MainMenuActivity.this,GameActivity.class);
        startActivity(intent);
    }

    public void onClickScores(View view) {
        Intent intent = new Intent(MainMenuActivity.this,ScoreActivity.class);
        startActivity(intent);
    }

    public void onClickCredits(View view) {
        Intent intent = new Intent(MainMenuActivity.this,CreditActivity.class);
        startActivity(intent);
    }

    public void onClickQuit(View view) {
        this.finish();
        System.exit(0);
    }

    private void saveUsername(){
        String username = wName.getText().toString();

        // On stock le nom saisi dans les param de l'appli
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_LABEL, username);
        editor.apply();
    }

    public void onClickQuickGame(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.quick_game_alert, null));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}