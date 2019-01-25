package it.uniba.di.sms.barintondo.utils;

public class Review {
    private String userName;
    private String reviewText;
    private int vote;
    private String date;


    public Review(String userName , String reviewText , int vote , String date) {
        this.userName = userName;
        this.reviewText = reviewText;
        this.vote = vote;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getVote() {
        return vote;
    }

    public String getDate() {
        return date;
    }
}
