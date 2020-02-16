package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("total")
    @Expose
    public Integer total;
    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("per_page")
    @Expose
    public Integer perPage;
    @SerializedName("current_page")
    @Expose
    public Integer currentPage;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

}
