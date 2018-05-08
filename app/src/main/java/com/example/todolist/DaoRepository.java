package com.example.todolist;

public class DaoRepository {

    private final AppDatabase db = AppDatabase.getDatabase();

    public TodoItemDao getDao() {
        return db.todoItemDao();
    }
}
