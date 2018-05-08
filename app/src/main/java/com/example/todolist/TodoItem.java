package com.example.todolist;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class TodoItem implements Comparable<TodoItem> {

    private String id;
    private Date date;
    private String title;
    private String body;
    private boolean archived;

    public TodoItem() {
        // Empty constructor
    }

    public TodoItem(Date date, String title, String body) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.archived = false;
    }

    public TodoItem(Date date, String title, String body, boolean archived) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.archived = archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoItem todoItem = (TodoItem) o;

        if (archived != todoItem.archived) return false;
        if (!id.equals(todoItem.id)) return false;
        if (!date.equals(todoItem.date)) return false;
        if (!title.equals(todoItem.title)) return false;
        return body.equals(todoItem.body);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + (archived ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull TodoItem o) {
        return this.getDate().compareTo(o.getDate());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
