package com.tbbr.tbbr.models;


import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.DecimalFormat;

/**
 * Created by Maaz on 2016-04-19.
 */

@Type("friendships")
public class Friendship extends Resource {
    public int balance;

    public String hashId;

    public int friendshipDataId;

    public User user;

    public User friend;


    public User getFriend() {
        return friend;
    }

    public int getBalance() {
        return balance;
    }

    public int getFriendshipDataId() {
        return friendshipDataId;
    }

    public String getFormattedBalance() {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        double balance = this.balance / 100;
        String formattedBalance = decimalFormat.format(balance);

        return "$" + formattedBalance;
    }
}
