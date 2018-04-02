package me.tbbr.tbbr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Retrofit.JSONConverterFactory;

import java.io.IOException;

import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.DeviceToken;
import me.tbbr.tbbr.models.Friendship;
import me.tbbr.tbbr.models.Token;
import me.tbbr.tbbr.models.Transaction;
import me.tbbr.tbbr.models.User;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "DID WE GET HERE?");
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Token token = getLocalToken();

        if (token == null) {
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(loginIntent);
            SplashActivity.this.finish();
        } else {
            makeCurrentUserRequest(token);
        }
    }


    private Token getLocalToken() {
        Gson converter = new Gson();
        SharedPreferences preferences = getSharedPreferences("AUTH_PREFERENCES", MODE_PRIVATE);

        Token savedToken = converter.fromJson(preferences.getString("token", ""), Token.class);
        Log.e("Splash", "Our saved token is: " + savedToken.getAccessToken());
        return savedToken;
    }

    private void makeCurrentUserRequest(Token token) {
        APIService apiService = TBBRApplication.createAuthenticatedService(token);
        TBBRApplication app = (TBBRApplication) getApplication();
        Call<JSONApiObject> curUserReq = apiService.getUser(token.getUserId());

        try {
            curUserReq.enqueue(new Callback<JSONApiObject>() {
                @Override
                public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {
                    if (response.body() == null) {
                        Toast.makeText(getApplicationContext(), "Failed to get current user, try logging in again!" + String.valueOf(response.code()), Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(loginIntent);
                        SplashActivity.this.finish();
                    } else {
                        app.setCurrentUser(response.body().getData(0));
                        Log.d("Splash", "We've got the user set, transitioning to FriendshipListActivity");
                        Intent friendshipList = new Intent(SplashActivity.this, FriendshipListActivity.class);
                        SplashActivity.this.startActivity(friendshipList);
                        SplashActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<JSONApiObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed to respond", Toast.LENGTH_LONG).show();
                    Log.e("API", t.getMessage());
                }
            });
        } catch (Exception ex) {
            Log.e("API", ex.getMessage());
        }
    }


}
