package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private Date createdAt;
    private String title;
    private String comment;
    private String contents;
    private String publisher;
    private String id;
    private int likesCount;
    private Map<String,Boolean> likes  = new HashMap<>();

    public PostInfo(String title, String contents, String publisher, Date createdAt,String id){

        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.likesCount = 0;
    }
    public PostInfo(String title, String contents, String publisher, Date createdAt){

        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.likesCount = 0;
    }
    //댓글
    public PostInfo(String comment, Date createdAt){

        this.comment = comment;
        this.createdAt = createdAt;
    }

    public PostInfo(String comment, String publisher, Date createdAt){

        this.comment = comment;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }



    public String getTitle(){return this.title;}
    public void setTitle(String title){this.title= title;}
    public String getContents(){return this.contents;}
    public void setContents(String contents){this.contents= contents;}
    public String getPublisher(){return this.publisher;}
    public void setPublisher(String publisher){this.publisher= publisher;}
    public Date getCreatedAt(){return this.createdAt;}
    public void setCreatedAt(Date createdAt){this.createdAt = createdAt;}
    public String getID(){return this.id;}
    public void setID(String id){this.id=id;}

    public int getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public Map<String, Boolean> getLikes() {
        return likes;
    }
    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public String getComment(){return this.comment;}
    public void setComment(String comment){this.comment= comment;}
}
