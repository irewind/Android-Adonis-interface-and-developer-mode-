package com.irewind.models;

import java.util.Date;

/**
 * Created by tzmst on 11/17/2014.
 */
public class CommentItem {

    private int id;
    private String name;
    private String lastName;
    private Date date;
    private String link;
    private String comment;

    public CommentItem(int id, String name, String lastName, Date date, String link, String comment) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.date = date;
        this.link = link;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
