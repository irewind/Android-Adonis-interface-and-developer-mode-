package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class PageInfo {

    @SerializedName("number")
    private int number;

    @SerializedName("size")
    private int size;

    @SerializedName("totalElements")
    private int totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
