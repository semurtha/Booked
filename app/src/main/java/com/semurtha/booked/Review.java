package com.semurtha.booked;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    @Exclude private String id;

    private String bookTitle, reviewTitle, reviewContent;
    private float rating;
    private boolean favorited;
    private String reviewedBy;
    private Date reviewDate;

    public Review() {
    }

    public Review (String bookTitle, float rating, boolean favorited, String reviewTitle, String reviewContent, String reviewedBy, Date reviewDate) {
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.favorited = favorited;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.reviewedBy = reviewedBy;
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return  "\nbookTitle='" + bookTitle + '\'' + '\n' +
                "reviewTitle='" + reviewTitle + '\'' + '\n' +
                "reviewContent='" + reviewContent + '\'' + '\n' +
                "rating=" + rating + '\n' +
                "favorited=" + favorited + '\n' +
                "reviewedBy='" + reviewedBy + '\'' + '\n' +
                "reviewDate=" + reviewDate + '\n';
    }


    /*
        TODO : Swap from list view to recyclerview w/ cards
        TODO : Handle adding a book to users' favorites
    */

    @Exclude
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
}
