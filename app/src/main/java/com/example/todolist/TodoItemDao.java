package com.example.todolist;

import android.arch.lifecycle.LiveData;

import java.util.List;

public interface TodoItemDao {
    LiveData<List<TodoItem>> getAll();
    LiveData<List<TodoItem>> getAllUnarchived();
    LiveData<List<TodoItem>> getAllArchived();
    LiveData<TodoItem> getTodoItem(String id);
    void insertAll(TodoItem... todoItems);
    void updateItem(TodoItem todoItem);
    void delete(TodoItem todoItem);
}
