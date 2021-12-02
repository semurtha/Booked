package com.semurtha.booked;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    @Exclude private String id;

    private String bookTitle, reviewTitle, reviewContent, reviewedBy, reviewedByName, published, coverURL;
    private float rating;
    private boolean favorited;
    private Date reviewDate;


    public Review() {
    }

    public Review (String bookTitle, String published, String coverURL, float rating, boolean favorited, String reviewTitle, String reviewContent, String reviewedBy, String reviewedByName, Date reviewDate) {
        this.bookTitle = bookTitle;
        this.published = published;
        this.coverURL = coverURL;
        this.rating = rating;
        this.favorited = favorited;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.reviewedBy = reviewedBy;
        this.reviewedByName = reviewedByName;
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return  "\nbookTitle='" + bookTitle + '\'' + '\n' +
                "pubYear='" + published + '\'' + '\n' +
                "coverURL='" + coverURL + '\'' + '\n' +
                "reviewTitle='" + reviewTitle + '\'' + '\n' +
                "reviewContent='" + reviewContent + '\'' + '\n' +
                "rating=" + rating + '\n' +
                "favorited=" + favorited + '\n' +
                "reviewedBy='" + reviewedBy + '\'' + '\n' +
                "reviewedByName='" + reviewedByName + '\'' + '\n' +
                "reviewDate=" + reviewDate + '\n';
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

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
