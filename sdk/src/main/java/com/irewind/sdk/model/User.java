package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

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
    private long createdDate;

    @SerializedName("status")
    private String status;

    @SerializedName("authProvider")
    private String authProvider;

    @SerializedName("role")
    private String role;

    @SerializedName("picture")
    private String picture;

    @SerializedName("lastLoginDate")
    private long lastLoginDate;

    @SerializedName("vpsId")
    private long vpsId;

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

    public long getCreatedDate() {
        return createdDate;
    }

    public String getStatus() {
        return status;
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

    public long getLastLoginDate() {
        return lastLoginDate;
    }

    public long getVpsId() {
        return vpsId;
    }

    public String getDisplayName() {
        String name = "";
        if (getFirstname() != null && getFirstname().length() > 0) {
            name += getFirstname();
        }

        if (getLastname() != null && getLastname().length() > 0) {
            if (name.length() > 0) {
                name += " ";
            }
            name += getLastname();
        }

        if (name.length() == 0 && getFullname() != null && getFullname().length() > 0) {
            name = getFullname();
        }

        if (name.length() == 0 && getEmail() != null && getEmail().length() > 0) {
            name = getEmail();
        }

        return name;
    }

    public User(long id, long vpsId, String email, String firstname, String lastname, String fullname, long createdDate, String status, String authProvider, String role, String picture, long lastLoginDate) {
        this.id = id;
        this.vpsId = vpsId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullname = fullname;
        this.createdDate = createdDate;
        this.status = status;
        this.authProvider = authProvider;
        this.role = role;
        this.picture = picture;
        this.lastLoginDate = lastLoginDate;
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
                ", vpsId=" + vpsId +
                '}';
    }
}
