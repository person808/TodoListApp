package com.example.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDatabaseDao implements TodoItemDao {

    private final DatabaseReference databaseReference;

    public FireBaseDatabaseDao(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    @Override
    public LiveData<List<TodoItem>> getAll() {
        MutableLiveData<List<TodoItem>> data = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TodoItem> todoItemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    TodoItem item = itemSnapshot.getValue(TodoItem.class);
                    todoItemList.add(item);
                }
                data.setValue(todoItemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "Error reading firebase db.");
            }
        });
        return data;
    }

    @Override
    public LiveData<List<TodoItem>> getAllArchived() {
        MutableLiveData<List<TodoItem>> data = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TodoItem> todoItemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    TodoItem item = itemSnapshot.getValue(TodoItem.class);
                    if (item.isArchived()) {
                        todoItemList.add(item);
                    }
                }
                data.setValue(todoItemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "Error reading firebase db.");
            }
        });
        return data;
    }

    @Override
    public LiveData<List<TodoItem>> getAllUnarchived() {
        MutableLiveData<List<TodoItem>> data = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TodoItem> todoItemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    TodoItem item = itemSnapshot.getValue(TodoItem.class);
                    if (!item.isArchived()) {
                        todoItemList.add(item);
                    }
                }
                data.setValue(todoItemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "Error reading firebase db.");
            }
        });
        return data;
    }

    @Override
    public LiveData<TodoItem> getTodoItem(String id) {
        MutableLiveData<TodoItem> data = new MutableLiveData<>();
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TodoItem item = dataSnapshot.getValue(TodoItem.class);
                data.setValue(item);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "Error reading firebase db.");
            }
        });
        return data;
    }

    @Override
    public void insertAll(TodoItem... todoItems) {
        for (TodoItem item : todoItems) {
            DatabaseReference pushedItemRef = databaseReference.push();
            String id = pushedItemRef.getKey();
            item.setId(id);
            pushedItemRef.setValue(item);
        }
    }

    @Override
    public void updateItem(TodoItem todoItem) {
        DatabaseReference itemRef = databaseReference.child(todoItem.getId());
        itemRef.setValue(todoItem);
    }

    @Override
    public void delete(TodoItem todoItem) {
        DatabaseReference itemRef = databaseReference.child(todoItem.getId());
        itemRef.removeValue();
    }
}
