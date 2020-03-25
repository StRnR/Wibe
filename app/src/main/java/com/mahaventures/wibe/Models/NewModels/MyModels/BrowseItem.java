package com.mahaventures.wibe.Models.NewModels.MyModels;


public class BrowseItem {
    public String image;
    public String title;
    public BrowseType type;
    public String id;
    public String color;

    public BrowseItem(String image, String title, BrowseType type, String id, String color) {
        this.image = image;
        this.title = title;
        this.type = type;
        this.id = id;
        this.color = color;
    }

    public enum BrowseType {
        Album,
        Artist,
        Playlist,
        Track
    }
}