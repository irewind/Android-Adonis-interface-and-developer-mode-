package com.irewind.models;

import java.util.Date;

/**
 * Created by tzmst on 11/17/2014.
 */
public class MovieGridItem {

    private int id;
    private String link, username, title;
    private Date date;

    public MovieGridItem(int id, String link, String username, String title, Date date) {
        this.id = id;
        this.link = link;
        this.username = username;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
