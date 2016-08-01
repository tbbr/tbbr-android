package me.tbbr.tbbr.api;

import com.gustavofao.jsonapi.Models.JSONApiObject;

import me.tbbr.tbbr.models.DeviceToken;
import me.tbbr.tbbr.models.Token;
import me.tbbr.tbbr.models.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Maaz on 2016-04-18.
 */
public interface APIService {
    @FormUrlEncoded
    @POST("tokens/oauth/grant")
    Call<Token> grantToken(@Field("grant_type") String grantType, @Field("access_token") String accessToken);

    @POST("device-tokens")
    Call<JSONApiObject> registerDevice(@Body DeviceToken deviceToken);

    @GET("friendships")
    Call<JSONApiObject> getFriendships();
    @GET("friendships/{id}")
    Call<JSONApiObject> getFriendship(@Path("id") String friendshipId);

    @GET("transactions")
    Call<JSONApiObject> getTransactions(@Query("relatedObjectId") int relatedObjectId, @Query("relatedObjectType") String relatedObjectType);
    @POST("transactions")
    Call<JSONApiObject> createTransaction(@Body Transaction transaction);
}
