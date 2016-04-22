package com.tbbr.tbbr.models;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.DecimalFormat;

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

    public String getFormattedAmount() {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        double amount = this.amount / 100;
        String formattedAmount = decimalFormat.format(amount);

        return "$" + formattedAmount;
    }
}
