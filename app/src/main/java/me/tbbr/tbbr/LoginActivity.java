package me.tbbr.tbbr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gustavofao.jsonapi.Models.JSONApiObject;

import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.Token;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

/**
 * Created by Maaz on 2016-04-16.
 */
public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private ProgressBar loginProgressBar;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton)findViewById(R.id.fbLoginButton);
        loginProgressBar = (ProgressBar)findViewById(R.id.loginProgressBar);

        if (loginProgressBar != null) {
            loginProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginUserOnServer(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login attempt cancelled!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Login attempt failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginUserOnServer(String accessToken) {
        APIService service = ((TBBRApplication) getApplication()).getUnauthenticatedApiService();
        Call<Token> loginReq = service.grantToken("facebook_access_token", accessToken);
        loginButton.setVisibility(LoginButton.INVISIBLE);
        loginProgressBar.setVisibility(ProgressBar.VISIBLE);

        loginReq.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                } else {
                    // Register the device to the server, if we need to
                    registerDeviceToReceiveNotifications();
                    Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    ((TBBRApplication) getApplication()).setUserLoggedIn(response.body());
                    Intent intent = new Intent(LoginActivity.this, FriendshipListActivity.class);
                    LoginActivity.this.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                loginProgressBar.setVisibility(ProgressBar.INVISIBLE);
                loginButton.setVisibility(LoginButton.VISIBLE);
            }
        });
    }

    private void registerDeviceToReceiveNotifications() {
        SharedPreferences preferences = getSharedPreferences("FIREBASE_CLOUD_MESSAGING_TOKEN", MODE_PRIVATE);
        Boolean isRegistered = preferences.getBoolean("is_registered", false);
        if (isRegistered) {
            return;
        }

        String deviceToken = preferences.getString("token", "");

        if (deviceToken.equals("")) {
            return;
        }

        Log.d("TRACE", "Registering device to server with firebase token: " + deviceToken);

        // Device is not registered on the server
        APIService service = ((TBBRApplication) getApplication()).getAPIService();
        Call<JSONApiObject> registerDeviceReq = service.registerDevice(deviceToken);
        registerDeviceReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {
                if (response.isSuccessful()) {
                    Log.d("TRACE", "Device successfully registered to server with token:" + deviceToken);
                    // Device successfully registered
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("is_registered", true);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("ERROR", t.getMessage());
            }
        });
    }
}
