package com.example.todolist;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DaoRepository {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference dbReference = database.getReference(TodoListApp.getUid());

    public TodoItemDao getDao() {
        return new FireBaseDatabaseDao(dbReference);
    }
}
