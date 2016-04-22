package com.tbbr.tbbr.models;


import com.gustavofao.jsonapi.Annotations.Id;
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


    public String getAvatarUrl() {
        return "https://graph.facebook.com/" + externalId + "/picture?type=large";
    }

}
