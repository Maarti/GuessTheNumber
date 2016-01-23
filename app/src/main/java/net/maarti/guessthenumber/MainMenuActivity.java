package net.maarti.guessthenumber;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import net.maarti.guessthenumber.game.Difficulty;

public class MainMenuActivity extends AppCompatActivity {

    //public static final String USERNAME_LABEL = "username";
    public static final String USERNAME_LABEL = "pref_username";
    private EditText wName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_v02);
        //wName = (EditText) findViewById(R.id.ediTextName);

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(MainMenuActivity.USERNAME_LABEL,"");
        wName.setText(username);*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        //saveUsername();

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

    public void onClickRate(View view) {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        Toast.makeText(MainMenuActivity.this, R.string.toats_rate_app, Toast.LENGTH_LONG).show();
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    public void onClickShare(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.shareSubject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareText));
        Toast.makeText(MainMenuActivity.this, R.string.toast_share_app, Toast.LENGTH_LONG).show();
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareVia)));
    }

    public void onClickSettings(View view) {
        Intent intent = new Intent(MainMenuActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickInstruction(View view) {
        Intent intent = new Intent(MainMenuActivity.this,InstructionActivity.class);
        startActivity(intent);
    }
}