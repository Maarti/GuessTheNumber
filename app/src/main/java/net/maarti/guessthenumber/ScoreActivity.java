package net.maarti.guessthenumber;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.maarti.guessthenumber.model.DatabaseHandler;
import net.maarti.guessthenumber.model.Score;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private ListView vScores10;
    private ListView vScores20;
    private ListView vScores30;
    private ViewFlipper vFlipper;
    private Button vButtonRight;
    private Button vButtonLeft;
    private final int SCORE_COUNT = 20;
    private MediaPlayer mpClic1;
    private MediaPlayer mpClic2;

    DatabaseHandler db = new DatabaseHandler(this);
    private float lastX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        vScores10 = (ListView) findViewById(R.id.listViewScores10);
        vScores20 = (ListView) findViewById(R.id.listViewScores20);
        vScores30 = (ListView) findViewById(R.id.listViewScores30);
        vFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        vButtonRight = (Button) findViewById(R.id.buttonRight);
        vButtonLeft = (Button) findViewById(R.id.buttonLeft);
        AdView wBanner = (AdView) findViewById(R.id.bannerScore);
        mpClic1 = MediaPlayer.create(getApplicationContext(),R.raw.clic);
        mpClic2 = MediaPlayer.create(getApplicationContext(),R.raw.clic);

        // Remplissage des listes des scores
        List<Score> listScores10 = db.getTopScores(10,SCORE_COUNT);
        List<Score> listScores20 = db.getTopScores(20,SCORE_COUNT);
        List<Score> listScores30 = db.getTopScores(30,SCORE_COUNT);

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

        vScores10.setAdapter(adapter10);
        vScores20.setAdapter(adapter20);
        vScores30.setAdapter(adapter30);

        // Gestion des boutons "suivant/précédent"
        vButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.setInAnimation(ScoreActivity.this, R.anim.slide_in_from_right);
                vFlipper.setOutAnimation(ScoreActivity.this, R.anim.slide_out_to_left);
                vFlipper.showNext();

                // On joue le son du clic
                if(mpClic1.isPlaying())
                    mpClic2.start();
                else
                    mpClic1.start();
            }
        });

        vButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.setInAnimation(ScoreActivity.this, R.anim.slide_in_from_left);
                vFlipper.setOutAnimation(ScoreActivity.this, R.anim.slide_out_to_right);
                vFlipper.showPrevious();

                // On joue le son du clic
                if(mpClic1.isPlaying())
                    mpClic2.start();
                else
                    mpClic1.start();
            }
        });

        // Chargement de la bannière pub
        wBanner.loadAd(new AdRequest.Builder().build());
    }
}
