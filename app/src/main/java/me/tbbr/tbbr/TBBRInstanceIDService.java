package me.tbbr.tbbr;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

/**
 * Created by maazali on 2016-07-25.
 */
public class TBBRInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "TBBRInstanceIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveTokenToPreferences(refreshedToken);
    }

    private void saveTokenToPreferences(String token) {
        Log.d("TRACE", "Saving Firebase InstanceID to shared preferences");
        SharedPreferences.Editor editor = getSharedPreferences("FIREBASE_CLOUD_MESSAGING_TOKEN", MODE_PRIVATE).edit();
        editor.putString("token", token);

        // Since the token has been refreshed, it is not registered on the tbbr server
        // so we'll set is_registered to false, to have the app register the token at a later time
        editor.putBoolean("is_registered", false);
        editor.apply();

    }
}
