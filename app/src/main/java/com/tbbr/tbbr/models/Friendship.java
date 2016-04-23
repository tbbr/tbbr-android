package com.tbbr.tbbr.models;


import android.graphics.Color;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    public int getBalanceColor() {
        if (balance == 0) {
            return Color.GRAY;
        } else if (balance > 0) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }

    public boolean isBalanceNegative() {
        return balance < 0;
    }

    public boolean isBalancePositive() {
        return balance > 0;
    }

    public boolean isBalanceZero() {
        return balance == 0;
    }

    public int getFriendshipDataId() {
        return friendshipDataId;
    }

    public String getFormattedBalance() {
        double balance = ((double) this.balance) / 100;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(balance);

        return moneyString;
    }
}
