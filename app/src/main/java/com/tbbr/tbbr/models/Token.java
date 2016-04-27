package com.tbbr.tbbr.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Maaz on 2016-04-18.
 */

public class Token {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("refreshExpiration")
    @Expose
    private String refreshExpiration;
    @SerializedName("authExpiration")
    @Expose
    private String authExpiration;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @param accessToken
     * The accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     *
     * @return
     * The refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     *
     * @param refreshToken
     * The refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     *
     * @return
     * The refreshExpiration
     */
    public String getRefreshExpiration() {
        return refreshExpiration;
    }

    /**
     *
     * @param refreshExpiration
     * The refreshExpiration
     */
    public void setRefreshExpiration(String refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    /**
     *
     * @return
     * The authExpiration
     */
    public String getAuthExpiration() {
        return authExpiration;
    }

    /**
     *
     * @param authExpiration
     * The authExpiration
     */
    public void setAuthExpiration(String authExpiration) {
        this.authExpiration = authExpiration;
    }

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return Integer.toString(userId);
    }

    /**
     *
     * @param userId
     * The userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The deletedAt
     */
    public Object getDeletedAt() {
        return deletedAt;
    }

    /**
     *
     * @param deletedAt
     * The deletedAt
     */
    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

}