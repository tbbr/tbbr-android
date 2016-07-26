package me.tbbr.tbbr.models;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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


    private String createdAt;

    // For transforming to and from json
    public Transaction() {}

    public Transaction(User sender, User recipient, int amount, String memo, String relatedObjectId,
                String relatedObjectType, String type) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.memo = memo;
        this.relatedObjectId = Integer.valueOf(relatedObjectId);
        this.relatedObjectType = relatedObjectType;
        this.type = type;
    }


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

    public String getMemo() {
        if (!memo.equals("") && memo != null) {
            return memo;
        } else {
            return "no memo available";
        }

    }


    public String getCreatedAtMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatMonth = new SimpleDateFormat("MMM");
        String month = "";
        try {
            Date date = format.parse(createdAt);
            month = formatMonth.format(date);
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        return month;
    }

    public String getCreatedAtDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDay = new SimpleDateFormat("d");
        String day = "";
        try {
            Date date = format.parse(createdAt);
            day = formatDay.format(date);
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        return day;
    }

    public String getCreatedAtYear() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
        String year = "";
        try {
            Date date = format.parse(createdAt);
            year = formatYear.format(date);
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        return year;
    }
}
