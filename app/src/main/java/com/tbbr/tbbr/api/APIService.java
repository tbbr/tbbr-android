package com.tbbr.tbbr.api;

import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.tbbr.tbbr.models.Friendship;
import com.tbbr.tbbr.models.Token;
import com.tbbr.tbbr.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Maaz on 2016-04-18.
 */
public interface APIService {
    @FormUrlEncoded
    @POST("tokens/oauth/grant")
    Call<Token> grantToken(@Field("grant_type") String grantType, @Field("access_token") String accessToken);

    @GET("friendships")
    Call<JSONApiObject> getFriendships();

    @GET("transactions")
    Call<JSONApiObject> getTransactions(@Query("relatedObjectId") int relatedObjectId, @Query("relatedObjectType") String relatedObjectType);
    @POST("transactions")
    Call<JSONApiObject> createTransaction(@Body Transaction transaction);
}
