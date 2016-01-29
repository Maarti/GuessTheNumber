package net.maarti.guessthenumber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.maarti.guessthenumber.utility.Utility;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        // Chargement de la bannière pub
        AdView wBanner = (AdView) findViewById(R.id.bannerInstruction);
        wBanner.loadAd(Utility.buildAdRequest());
    }
}
