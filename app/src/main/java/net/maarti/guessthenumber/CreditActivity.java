package net.maarti.guessthenumber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CreditActivity extends AppCompatActivity {

    //WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        AdView wBanner = (AdView) findViewById(R.id.bannerCredit);
        //webView = (WebView) findViewById(R.id.webViewCredit);
        //webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(getString(R.string.devWebSiteUrl));

        // Chargement de la bannière pub
        wBanner.loadAd(new AdRequest.Builder().build());
    }

    public void onClickDevMail(View view) {
        String devMail = getString(R.string.devMail);
        String objectMail = getString(R.string.contactMailObject);
        String appName = getString(R.string.app_name);
        Uri mail = Uri.parse("mailto:" + devMail + "?subject=" + Uri.encode(objectMail + " " + appName));
        Intent mailActivity = new Intent(Intent.ACTION_SENDTO, mail);
        startActivity(mailActivity);
    }


    public void onClickAppSite(View view) {
        String appSite = getString(R.string.appWebSiteUrl)+"#guessthenumber";
        Uri uri = Uri.parse(appSite);
        Intent webActivity = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(webActivity);
    }
}
