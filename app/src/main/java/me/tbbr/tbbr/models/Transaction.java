package me.tbbr.tbbr.models;

import android.graphics.Color;

import com.gustavofao.jsonapi.Annotations.Type;
import com.gustavofao.jsonapi.Models.Resource;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Maaz on 2016-04-20.
 */
@Type("transactions")
public class Transaction extends Resource implements Serializable {
    private int amount;
    private String type;
    private String memo;
    private String status;

    private int relatedObjectId;
    private String relatedObjectType;

    private User creator;
    private User recipient;
    private User sender;


    private String createdAt;

    // For transforming to and from json
    public Transaction() {
        // Default status to confirmed until UI functionality is there
        this.status = "Confirmed";
    }

    public Transaction(User sender, User recipient, int amount, String memo, int relatedObjectId,
                String relatedObjectType, String type) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.memo = memo;
        this.relatedObjectId = relatedObjectId;
        this.relatedObjectType = relatedObjectType;
        this.type = type;

        // Default status to confirmed until UI functionality is there
        this.status = "Confirmed";
    }


    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public User getSender() {
        return sender;
    }

    public String getFormattedAmount(String curUserId) {
        double amount = ((double) this.amount) / 100;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(amount);

        if (sender.getId().equals(curUserId)) {
            moneyString = "+ " + moneyString;
        } else {
            moneyString = "- " + moneyString;
        }

        return moneyString;
    }

    public int getAmountColor(String curUserId) {
        if (sender.getId().equals(curUserId)) {
            return Color.parseColor("#2ecc71");
        } else {
            return Color.parseColor("#e74c3c");
        }
    }

    public String getMemo() {
        if (!memo.equals("") && memo != null) {
            return memo;
        } else {
            return "no memo available";
        }
    }

    public User getCreator() {
        return creator;
    }


    public DateTime getCreatedAt() {
        return DateTime.parse(createdAt);
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
