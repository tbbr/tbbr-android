package me.tbbr.tbbr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;

import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.DeviceToken;
import me.tbbr.tbbr.models.Token;

import me.tbbr.tbbr.models.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton loginButton;
    private AVLoadingIndicatorView  progressBar;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.fb_login_button);
        progressBar = findViewById(R.id.friendship_detail_progress_bar);
        if (progressBar != null) {
            progressBar.hide();
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("LoginActivity", "Im Running");
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
        progressBar.show();

        loginReq.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "Failed to login, try again!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    // Register the device to the server, if we need to
                    registerDeviceToReceiveNotifications();

                    ((TBBRApplication) getApplication()).setUserLoggedIn(response.body());
                    Intent intent = new Intent(LoginActivity.this, FriendshipListActivity.class);
                    LoginActivity.this.startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                progressBar.hide();
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

        String deviceTokenStr = preferences.getString("token", "");

        if (deviceTokenStr.equals("")) {
            return;
        }

        Log.d("TRACE", "Registering device to server with firebase token: " + deviceTokenStr);

        // Device is not registered on the server
        APIService service = ((TBBRApplication) getApplication()).getAPIService();
        Call<JSONApiObject> registerDeviceReq = service.registerDevice(new DeviceToken(deviceTokenStr));
        registerDeviceReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {
                if (response.isSuccessful()) {
                    Log.d("TRACE", "Device successfully registered to server with token:" + deviceTokenStr);
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
