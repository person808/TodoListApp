package com.example.todolist;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements TodoItemAdapter.OnListInteractionListener, DeleteDialogFragment.DeleteDialogListener {

    private TodoViewModel todoViewModel;
    private TodoItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        Date date = new Date();
        TodoItem item = new TodoItem(date, "Test title", "test body");
        todoViewModel.addTodoItem(item);

        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        TooltipCompat.setTooltipText(fab, getString(R.string.fab_hint));

        TodoItemAdapter adapter = new TodoItemAdapter(this);
        recyclerView.setAdapter(adapter);

        todoViewModel.refreshTodoItems();
        todoViewModel.getTodoItems().observe(this, items -> {
            if (items != null) {
                adapter.submitList(items);
            }
        });
    }

    @Override
    public void onItemClick(TodoItem item) {
    }

    @Override
    public boolean onItemLongClick(TodoItem item) {
        selectedItem = item;
        DialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteDialogFragment");
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        todoViewModel.deleteTodoItem(selectedItem);
        todoViewModel.refreshTodoItems();
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
