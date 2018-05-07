package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Date;

public class ViewTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int CREATE_NEW_TODO = -1;
    private TodoViewModel todoViewModel;
    private TodoItem todoItem;
    private EditText titleTextView;
    private EditText bodyTextView;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);
        Intent intent = getIntent();
        id = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, CREATE_NEW_TODO);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        titleTextView = findViewById(R.id.editText_title);
        bodyTextView = findViewById(R.id.editText_body);

        if (id == CREATE_NEW_TODO) {
            todoItem = new TodoItem(new Date(), "", "");
            titleTextView.setText(todoItem.getTitle());
            bodyTextView.setText(todoItem.getBody());
        } else {
            todoViewModel.getTodoItem(id).observe(this, item -> {
                if (item != null) {
                    todoItem = item;
                    titleTextView.setText(todoItem.getTitle());
                    bodyTextView.setText(todoItem.getBody());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_todo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                todoViewModel.deleteTodoItem(todoItem);
                return true;
            case R.id.save:
                todoItem.setTitle(titleTextView.getText().toString());
                todoItem.setBody(bodyTextView.getText().toString());
                if (id == CREATE_NEW_TODO) {
                    todoViewModel.addTodoItem(todoItem);
                } else {
                    todoViewModel.updateTodoItem(todoItem);
                }
                return true;
            case R.id.setTime:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date newDate = Util.updateDate(todoItem.getDate(), year, month, dayOfMonth);
        todoItem.setDate(newDate);
        todoViewModel.updateTodoItem(todoItem);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date newDate = Util.updateTime(todoItem.getDate(), hourOfDay, minute);
        todoItem.setDate(newDate);
        todoViewModel.updateTodoItem(todoItem);
    }
}