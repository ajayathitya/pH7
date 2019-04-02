package com.example.arvind.ihl;

public class List_Data {
    private String  id;
    private  String likes;
    private String name;
    private String image_url;
    private String username;
    private String timestamp;


    public List_Data(String id,  String image_url,String name, String likes,String username,String timestamp) {
        this.name = name;
        this.image_url = image_url;
        this.id = id;
        this.likes = likes;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getId() {
        return id;
    }

    public String getLikes() {
        return likes;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }
}