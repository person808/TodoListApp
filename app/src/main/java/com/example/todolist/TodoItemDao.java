package com.example.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface TodoItemDao {

    @Query("SELECT * FROM todoitem")
    LiveData<List<TodoItem>> getAll();

    @Query("SELECT * FROM todoitem WHERE id = :id")
    LiveData<TodoItem> getTodoItem(int id);

    @Insert(onConflict = REPLACE)
    void insertAll(TodoItem... todoItems);

    @Update(onConflict = REPLACE)
    void updateItem(TodoItem todoItem);

    @Delete
    void delete(TodoItem todoItem);
}
