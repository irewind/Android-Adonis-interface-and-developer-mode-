package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    @SerializedName("userId")
    private long id;

    @SerializedName("email")
    private String email;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("createdDate")
    private Date createdDate;

    @SerializedName("status")
    private String status;

    @SerializedName("authProvider")
    private String authProvider;

    @SerializedName("role")
    private String role;

    @SerializedName("picture")
    private String picture;

    @SerializedName("lastLoginDate")
    private Date lastLoginDate;

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public String getRole() {
        return role;
    }

    public String getPicture() {
        return picture;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", fullname='" + fullname + '\'' +
                ", createdDate=" + createdDate +
                ", status='" + status + '\'' +
                ", authProvider='" + authProvider + '\'' +
                ", role='" + role + '\'' +
                ", picture='" + picture + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                '}';
    }
}
