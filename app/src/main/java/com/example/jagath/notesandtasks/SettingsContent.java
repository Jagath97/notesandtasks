package com.example.jagath.notesandtasks;

/**
 * Created by jagath on 27/02/2018.
 */

public class SettingsContent  {
    private String title,value;

    public SettingsContent(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
