package net.maarti.guessthenumber.utility;

import com.google.android.gms.ads.AdRequest;

import net.maarti.guessthenumber.BuildConfig;

/**
 * Class with utilities function for the app
 * Created by Bryan MARTINET on 27/01/2016.
 */
public class Utility {

    public static final String DEVICE_ID_LGG3 = "81C55CEACBF852C2B5D230A251B889FA";
    public static final String DEVICE_ID_GALAXYS = "2611C41137470FF51DA956478F70B0B8";

    //public static final String DEVICE_ID_TABLET = "";

    /**
     * Generate the right AdRequest whether it's in debug or release
     */
    public static AdRequest buildAdRequest() {
       /* if (BuildConfig.DEBUG)
        */    return new AdRequest.Builder()
                  //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice(DEVICE_ID_LGG3)
                    .addTestDevice(DEVICE_ID_GALAXYS)
                    .build();
        //else
         //   return new AdRequest.Builder().build();
    }

}
