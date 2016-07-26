package me.tbbr.tbbr.models;


import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

/**
 * Created by Maaz on 2016-04-19.
 */

@Type("users")
public class User extends Resource {
    private String name;
    private String externalId;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public String getAvatarUrl(String size) {
        String maxWidth = "1000";
        switch(size) {
            case "normal":
                return "https://graph.facebook.com/" + externalId + "/picture?type=large";
            case "large":
                return "https://graph.facebook.com/" + externalId + "/picture?width=" + maxWidth;
            default:
                return "https://graph.facebook.com/" + externalId + "/picture?type=large";
        }
    }


    @Override
    public String toString() {
        return name;
    }

}
