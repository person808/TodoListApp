package com.example.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

public class TodoViewModel extends ViewModel {

    private TodoItemDao dao;
    private LiveData<List<TodoItem>> todoItems;

    public TodoViewModel() {
        DaoRepository daoRepository = new DaoRepository();
        dao = daoRepository.getDao();
    }

    public void refreshTodoItems() {
        todoItems = dao.getAll();
    }

    public LiveData<List<TodoItem>> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(TodoItem item) {
        AsyncTask.execute(() -> dao.insertAll(item));
    }

    public void updateTodoItem(TodoItem item) {
        dao.updateItem(item);
    }

    public void deleteTodoItem(TodoItem item) {
        AsyncTask.execute(() -> dao.delete(item));
    }
}
