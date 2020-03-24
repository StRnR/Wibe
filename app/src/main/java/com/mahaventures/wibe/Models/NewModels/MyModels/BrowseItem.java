package com.mahaventures.wibe.Models.NewModels.MyModels;


public class BrowseItem {
    public String image;
    public String title;
    public BrowseType type;
    public String id;

    public BrowseItem(String image, String title, BrowseType type, String id) {
        this.image = image;
        this.title = title;
        this.type = type;
        this.id = id;
    }

    public enum BrowseType {
        Album,
        Artist,
        Playlist,
        Track
    }
}
