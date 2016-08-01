package me.tbbr.tbbr.models;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

/**
 * Created by maazali on 2016-07-26.
 */
@Type("deviceTokens")
public class DeviceToken extends Resource {
    private String token;
    private String deviceType;
    private int userId;

    // For transforming to and from JSON
    public DeviceToken() {}

    public DeviceToken(String token) {
        this.token = token;
        this.deviceType = "Android";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String type) {
        this.deviceType = type;
    }
}
