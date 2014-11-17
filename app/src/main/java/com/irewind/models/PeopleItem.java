package com.irewind.models;

import java.util.Date;

/**
 * Created by tzmst on 11/17/2014.
 */
public class PeopleItem {

    private int id;
    private String name;
    private String lastName;
    private Date date;
    private int nrVideos;

    public PeopleItem(int id, String name, String lastName, Date date, int nrVideos) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.date = date;
        this.nrVideos = nrVideos;
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

    public int getNrVideos() {
        return nrVideos;
    }

    public void setNrVideos(int nrVideos) {
        this.nrVideos = nrVideos;
    }
}
