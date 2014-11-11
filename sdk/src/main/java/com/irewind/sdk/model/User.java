package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    @SerializedName("userId")
    private Integer id;

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

    @SerializedName("role")
    private String role;

    @SerializedName("lastLoginDate")
    private Date lastLoginDate;

    public Integer getId() {
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

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
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
                ", role='" + role + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                '}';
    }
}
