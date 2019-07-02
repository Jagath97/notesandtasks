package com.example.jagath.notesandtasks;

/**
 * Created by jagath on 17/03/2018.
 */

public class NoteContent {
    int id,color;
    String title;
    String content;
    String modified;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String pin;
    String status;

    public NoteContent(int id, int color, String title, String content, String modified,String pin,String status) {
        this.id = id;
        this.color = color;
        this.title = title;
        this.content = content;
        this.modified = modified;
        this.pin=pin;
        this.status=status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
