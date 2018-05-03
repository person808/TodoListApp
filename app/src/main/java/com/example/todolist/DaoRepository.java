package com.example.todolist;

public class DaoRepository {

    private AppDatabase db = AppDatabase.getDatabase();

    public TodoItemDao getDao() {
        return db.todoItemDao();
    }
}
