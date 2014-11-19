package com.irewind.models;

import java.util.Date;

/**
 * Created by tzmst on 11/17/2014.
 */
public class RelatedItem {

    private int id;
    private String name;
    private String lastName;
    private Date date;
    private int nrViews, nrLikes;
    private String link;

    public RelatedItem(int id, String name, String lastName, Date date, int nrViews, int nrLikes, String link) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.date = date;
        this.nrViews = nrViews;
        this.nrLikes = nrLikes;
        this.link = link;
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

    public int getNrViews() {
        return nrViews;
    }

    public void setNrViews(int nrViews) {
        this.nrViews = nrViews;
    }

    public int getNrLikes() {
        return nrLikes;
    }

    public void setNrLikes(int nrLikes) {
        this.nrLikes = nrLikes;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
