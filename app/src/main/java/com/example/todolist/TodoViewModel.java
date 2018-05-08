package com.example.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

public class TodoViewModel extends ViewModel {

    private final TodoItemDao dao;
    private LiveData<List<TodoItem>> todoItems;

    public TodoViewModel() {
        DaoRepository daoRepository = new DaoRepository();
        dao = daoRepository.getDao();
    }

    public void refreshTodoItems() {
        todoItems = dao.getAll();
    }

    public void refreshTodoItems(boolean getArchived) {
        if (getArchived) {
            todoItems = dao.getAllArchived();
        } else {
            todoItems = dao.getAllUnarchived();
        }
    }

    public LiveData<List<TodoItem>> getTodoItems() {
        return todoItems;
    }

    public LiveData<TodoItem> getTodoItem(int id) {
        return dao.getTodoItem(id);
    }

    public void addTodoItem(TodoItem item) {
        AsyncTask.execute(() -> dao.insertAll(item));
    }

    public void updateTodoItem(TodoItem item) {
        AsyncTask.execute(() -> dao.updateItem(item));
    }

    public void deleteTodoItem(TodoItem item) {
        AsyncTask.execute(() -> dao.delete(item));
    }

    public void setArchivedStatus(TodoItem item, boolean archived) {
        item.setArchived(archived);
        updateTodoItem(item);
    }
}
