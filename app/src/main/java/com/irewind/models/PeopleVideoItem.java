package com.irewind.models;

import java.util.Date;

/**
 * Created by tzmst on 11/17/2014.
 */
public class PeopleVideoItem {

    private int id;
    private String name;
    private String lastName;
    private Date date;
    private String link;
    private String comment;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    boolean checked;

    public PeopleVideoItem(){}

    public PeopleVideoItem(int id, String name, String lastName, Date date, String link, String comment, boolean checked) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.date = date;
        this.link = link;
        this.comment = comment;
        this.checked = checked;
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
