package com.example.lenovo.Android_LIFE_Journal.model;

import java.io.Serializable;

/*
 筆記的數據類
 實現了Serializable，可以添加到Bundle數據包裡
 */
public class NoteInfo implements Serializable{
    private String name=null;
    private String content=null;
    private String Date=null;
    private String Type=null;// 筆記的樣式：“圖文”或者“文字”
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return Date;
    }

    public String getType() {
        return Type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setType(String type) {
        Type = type;
    }
}
