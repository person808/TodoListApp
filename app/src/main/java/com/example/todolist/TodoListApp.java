package com.example.todolist;

import android.app.Application;

public class TodoListApp extends Application {

    private static TodoListApp INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static TodoListApp getApplication() {
        return INSTANCE;
    }
}
