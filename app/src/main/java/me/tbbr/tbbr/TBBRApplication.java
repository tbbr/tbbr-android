package me.tbbr.tbbr;

import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.gustavofao.jsonapi.Models.Resource;
import com.gustavofao.jsonapi.Retrofit.JSONConverterFactory;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.Friendship;
import me.tbbr.tbbr.models.Token;
import me.tbbr.tbbr.models.Transaction;
import me.tbbr.tbbr.models.User;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maaz on 2016-04-18.
 */
public class TBBRApplication extends Application {

    APIService apiService;
    APIService unauthenticatedApiService;

    Token loggedInUsersToken;

    // TODO: Use a hashmap of <friendshipId => Friendship> to make it easier
    // to potentially update friendship cache at a later date.
    List<Resource> friendships;

    public void onCreate() {
        super.onCreate();

        Iconify.with(new MaterialModule());

        Stetho.initializeWithDefaults(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Gson converter = new Gson();
        SharedPreferences preferences = getSharedPreferences("AUTH_PREFERENCES", MODE_PRIVATE);

        Token savedToken = converter.fromJson(preferences.getString("token", ""), Token.class);

        loggedInUsersToken = savedToken;

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        unauthenticatedApiService = retrofit.create(APIService.class);

        if (savedToken != null) {
            logUserIn(savedToken);
        } else {
            apiService = unauthenticatedApiService;
        }
    }

    public void setUserLoggedIn(Token token) {
        this.loggedInUsersToken = token;

        if (token != null) {
            logUserIn(token);
        }
    }

    private void logUserIn(Token token) {
        Interceptor authorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer " + loggedInUsersToken.getAccessToken()).build();
                return chain.proceed(newRequest);
            }
        };


        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(authorizationInterceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.base_url))
                .addConverterFactory(JSONConverterFactory.create(User.class, Friendship.class, Transaction.class))
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);

        SharedPreferences.Editor editor = getSharedPreferences("AUTH_PREFERENCES", MODE_PRIVATE).edit();
        Gson converter = new Gson();

        editor.putString("token", converter.toJson(token));
        editor.apply();
    }

    public boolean getIsUserLoggedIn() {
        return loggedInUsersToken != null;
    }

    public Token getLoggedInUsersToken() {
        return loggedInUsersToken;
    }

    public APIService getAPIService() {
        return apiService;
    }

    public APIService getUnauthenticatedApiService() {
        return unauthenticatedApiService;
    }


    public void setFriendships(List<Resource> friendships) {
        this.friendships = friendships;
    }

    public List<Resource> getFriendships() {
        return friendships;
    }
}
