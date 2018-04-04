package me.tbbr.tbbr.models;


import android.graphics.Color;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.NumberFormat;


@Type("friendships")
public class Friendship extends Resource {
    public int balance;

    public String hashId;

    public int friendshipDataId;

    public User user;

    public User friend;


    public void removeTransactionFromBalance(Transaction t) {
        if (t.getSender().getId().equals(user.getId())) {
            balance -= t.getAmount();
        } else {
            balance += t.getAmount();
        }
    }

    public void addTransactionToBalance(Transaction t) {
        if (t.getSender().getId().equals(user.getId())) {
            balance += t.getAmount();
        } else {
            balance -= t.getAmount();
        }
    }


    public User getFriend() {
        return friend;
    }

    public User getUser() { return user; }

    public int getBalance() {
        return balance;
    }

    public int getBalanceColor() {
        if (balance == 0) {
            return Color.parseColor("#ECF0F1");
        } else if (balance > 0) {
            return Color.parseColor("#2ecc71");
        } else {
            return Color.parseColor("#e74c3c");
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
