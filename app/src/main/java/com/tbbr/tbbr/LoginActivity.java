package com.tbbr.tbbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tbbr.tbbr.api.APIService;
import com.tbbr.tbbr.models.Token;

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
                String accessToken = loginResult.getAccessToken().getToken();
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
}
