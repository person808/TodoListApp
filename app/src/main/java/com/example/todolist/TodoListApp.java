package com.example.todolist;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class TodoListApp extends Application {

    private static String uid;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("", "AUTHENTICATED");
                    FirebaseDatabase.getInstance().getReference().push().setValue(auth.getCurrentUser().getUid());
                    uid = auth.getCurrentUser().getUid();
                }
            });
        } else {
            uid = auth.getCurrentUser().getUid();
        }
    }

    public static String getUid() {
        return uid;
    }
}
