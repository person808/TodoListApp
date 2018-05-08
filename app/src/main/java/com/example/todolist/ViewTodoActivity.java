package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Date;

public class ViewTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TodoViewModel todoViewModel;
    private TodoItem todoItem;
    private EditText titleTextView;
    private EditText bodyTextView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);
        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        titleTextView = findViewById(R.id.editText_title);
        bodyTextView = findViewById(R.id.editText_body);
        dateTextView = findViewById(R.id.textView_date);
        dateTextView.setOnClickListener(v -> startDateSelection());

        Drawable transparentDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        Util.setViewBackgroundWithoutResettingPadding(titleTextView, transparentDrawable);
        Util.setViewBackgroundWithoutResettingPadding(bodyTextView, transparentDrawable);

        if (id == null) {
            todoItem = new TodoItem(Util.getNewTodoDate(), "", "");
            titleTextView.setText(todoItem.getTitle());
            bodyTextView.setText(todoItem.getBody());
            setDateText();
        } else {
            todoViewModel.getTodoItem(id).observe(this, item -> {
                if (item != null) {
                    todoItem = item;
                    titleTextView.setText(todoItem.getTitle());
                    bodyTextView.setText(todoItem.getBody());
                    setDateText();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    public void onBackPressed() {
        if (!hasTitleOrBody() && todoItem.getId() != null) {
            todoViewModel.deleteTodoItem(todoItem);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_todo_menu, menu);
        if (todoItem.isArchived()) {
            menu.findItem(R.id.unarchive).setVisible(true);
            menu.findItem(R.id.done).setVisible(false);
        } else {
            menu.findItem(R.id.unarchive).setVisible(false);
            menu.findItem(R.id.done).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                todoViewModel.setArchivedStatus(todoItem, true);
                save();
                invalidateOptionsMenu();
                return true;
            case R.id.unarchive:
                todoViewModel.setArchivedStatus(todoItem, false);
                save();
                invalidateOptionsMenu();
                return true;
            case R.id.setTime:
                startDateSelection();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date newDate = Util.updateDate(todoItem.getDate(), year, month, dayOfMonth);
        todoItem.setDate(newDate);
        save();
        setDateText();
        DialogFragment newFragment = TimePickerFragment.newInstance(todoItem.getDate());
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date newDate = Util.updateTime(todoItem.getDate(), hourOfDay, minute);
        todoItem.setDate(newDate);
        save();
        setDateText();
    }

    private void startDateSelection() {
        DialogFragment newFragment = DatePickerFragment.newInstance(todoItem.getDate());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setDateText() {
        String text = Util.dateToString(todoItem.getDate());
        dateTextView.setText(text);
    }

    private boolean hasTitleOrBody() {
        return !(titleTextView.getText().length() == 0) || !(bodyTextView.getText().length() == 0);
    }

    private void save() {
        todoItem.setTitle(titleTextView.getText().toString());
        todoItem.setBody(bodyTextView.getText().toString());
        if (hasTitleOrBody() && todoItem.getId() == null) {
            todoViewModel.addTodoItem(todoItem);
        } else if (hasTitleOrBody()) {
            todoViewModel.updateTodoItem(todoItem);
        }
    }
}