package com.example.todolist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class ViewTodoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);
        Intent intent = getIntent();
        int id = intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0);

        TodoViewModel todoViewModel= ViewModelProviders.of(this).get(TodoViewModel.class);


        EditText titleTextView= findViewById(R.id.editText_title);
        EditText bodyTextView= findViewById(R.id.editText_body);

        todoViewModel.getTodoItem(id).observe(this, new Observer<TodoItem>() {
            @Override
            public void onChanged(@Nullable TodoItem todoItem) {
                if(todoItem != null){
                    titleTextView.setText(todoItem.getTitle());
                    bodyTextView.setText(todoItem.getBody());
                }

            }
        });
    }
}
