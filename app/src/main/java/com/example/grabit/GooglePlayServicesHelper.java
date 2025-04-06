package com.example.grabit;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

public class GooglePlayServicesHelper {
    private static final String TAG = "GooglePlayServicesHelper";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkGooglePlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Log.e(TAG, "Google Play Services is not available: " + apiAvailability.getErrorString(resultCode));
            } else {
                Log.e(TAG, "This device is not supported by Google Play Services");
            }
            return false;
        }
        return true;
    }

    public static void installProvider(Context context) {
        try {
            ProviderInstaller.installIfNeeded(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to install security provider: " + e.getMessage());
        }
    }
} 