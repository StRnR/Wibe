package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("total")
    @Expose
    public int total;
    @SerializedName("count")
    @Expose
    public int count;
    @SerializedName("per_page")
    @Expose
    public int perPage;
    @SerializedName("current_page")
    @Expose
    public int currentPage;
    @SerializedName("total_pages")
    @Expose
    public int totalPages;
//    @SerializedName("links")
//    @Expose
//    public List<Object> links;
}