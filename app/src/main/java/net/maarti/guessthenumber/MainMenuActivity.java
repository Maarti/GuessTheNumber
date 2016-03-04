package net.maarti.guessthenumber;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.example.games.basegameutils.BaseGameUtils;

import net.maarti.guessthenumber.game.Difficulty;
import net.maarti.guessthenumber.utility.MultimediaManager;

public class MainMenuActivity extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String USERNAME_LABEL = "pref_username";
    public static final String PREF_AUTO_SIGNIN_LABEL = "pref_auto_signin";
    public static final String EXPLICIT_USERNAME_CHANGE_LABEL = "pref_explicit_username_change" ;
    private MediaPlayer mpClic;
    private static int RC_SIGN_IN = 9001;
    private SharedPreferences preferences;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    // sign in flow, to know you should not attempt
    // to connect in onStart()
    GoogleApiClient mGoogleApiClient;  // initialized in onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // On écrit le numéro de version de l'app
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version!=null)
            ((TextView) findViewById(R.id.textViewAppVersion)).setText(getResources().getString(R.string.version,version));

        // Google API build
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the asynchronous sign in flow
                mSignInClicked = true;
                mGoogleApiClient.connect();
                preferences.edit().putBoolean(PREF_AUTO_SIGNIN_LABEL, true).apply();
                preferences.edit().putBoolean(MainMenuActivity.EXPLICIT_USERNAME_CHANGE_LABEL, false).apply();
            }
        });
        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign out.
                mSignInClicked = false;
                // user explicitly signed out, so turn off auto sign in
                mExplicitSignOut = true;
                preferences.edit().putBoolean(PREF_AUTO_SIGNIN_LABEL,false).apply();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }

                // show sign-in button, hide the sign-out button
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            }
        });
        mpClic = MediaPlayer.create(getApplicationContext(),R.raw.clic);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && preferences.getBoolean(PREF_AUTO_SIGNIN_LABEL,true)) {
            // auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }



    public void onClickNewGame(View view) {
        // En fonction de la vue qui appelle la méthode, on change la difficulté
        int difficulty = Difficulty.getIdFromView(view);

        // On stock la difficulté dans les param de l'appli
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Difficulty.DIFFICULTY_LABEL, difficulty);
        editor.apply();*/

        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        // On lance la GameActivity en stockant la difficulté dans l'intent
        Intent intent = new Intent(MainMenuActivity.this,GameActivity.class);
        intent.putExtra(Difficulty.DIFFICULTY_LABEL, difficulty);
        startActivity(intent);
    }

    public void onClickScores(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

      /*  if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.score_alert, null));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {*/
            Intent intent = new Intent(MainMenuActivity.this, ScoreActivity.class);
            startActivity(intent);
       // }
    }

    public void onClickCredits(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        Intent intent = new Intent(MainMenuActivity.this,CreditActivity.class);
        startActivity(intent);
    }

  /*  public void onClickQuit(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }else{
            this.finish();
            System.exit(0);
        }
    }*/

    public void onClickQuickGame(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.quick_game_alert, null));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);
    }

    public void onClickRate(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }else{
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        Toast.makeText(MainMenuActivity.this, R.string.toats_rate_app, Toast.LENGTH_LONG).show();
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    public void onClickShare(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.shareSubject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareText));
        Toast.makeText(MainMenuActivity.this, R.string.toast_share_app, Toast.LENGTH_LONG).show();
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareVia)));
    }

    public void onClickSettings(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        Intent intent = new Intent(MainMenuActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickInstruction(View view) {
        // On joue le son du clic
        MultimediaManager.play(getApplicationContext(), mpClic);

        Intent intent = new Intent(MainMenuActivity.this,InstructionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        preferences.edit().putBoolean(PREF_AUTO_SIGNIN_LABEL, true).apply();
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        //TODO afficher l'icone achievements normale, sinon afficher une version grisée + button disabled

        // Si l'utilisateur n'a pas explicitement changé son nom dans les préférences, on l'initialise avec le nom de son compte Google
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null && !preferences.getBoolean(EXPLICIT_USERNAME_CHANGE_LABEL,false)) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            if (!personName.equals(""))
                preferences.edit().putString(MainMenuActivity.USERNAME_LABEL,personName).apply();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,requestCode, resultCode, R.string.signin_failure);
                //mExplicitSignOut = true;
                preferences.edit().putBoolean(PREF_AUTO_SIGNIN_LABEL, false).apply();
            }
        }
    }

    public void onClickAchievements(View view) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
            //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,getString(R.string.leaderboard_easy)), 2);
        else {
            Toast.makeText(getApplicationContext(), R.string.toast_connect_for_achievements, Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            findViewById(R.id.imageButtonAchievements).startAnimation(shake);
        }
        MultimediaManager.play(getApplicationContext(), mpClic);
    }

  /*  public void onClickScoresEasy(View view) {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_easy)), 2);
        MultimediaManager.play(getApplicationContext(), mpClic);
    }

    public void onClickScoresMedium(View view) {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_medium)), 3);
        MultimediaManager.play(getApplicationContext(), mpClic);
    }

    public void onClickScoresHard(View view) {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_hard)), 4);
        MultimediaManager.play(getApplicationContext(), mpClic);
    }*/
}