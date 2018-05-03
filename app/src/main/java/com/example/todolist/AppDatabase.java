package com.example.todolist;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {TodoItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    private static final String DB_NAME = "todo.db";

    public static AppDatabase getDatabase() {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(TodoListApp.getApplication(), AppDatabase.class, DB_NAME)
                            .build();
        }
        return INSTANCE;
    }

    public abstract TodoItemDao todoItemDao();
}
