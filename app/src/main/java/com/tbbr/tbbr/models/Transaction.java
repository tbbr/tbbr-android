package com.tbbr.tbbr.models;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Maaz on 2016-04-20.
 */
@Type("transactions")
public class Transaction extends Resource {
    private int amount;
    private String type;
    private String memo;

    private int relatedObjectId;
    private String relatedObjectType;

    private User creator;
    private User recipient;
    private User sender;

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public User getSender() {
        return sender;
    }

    public String getFormattedAmount() {
        double amount = ((double) this.amount) / 100;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(amount);

        return moneyString;
    }
}
