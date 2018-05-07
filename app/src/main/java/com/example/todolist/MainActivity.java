package com.example.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements TodoItemAdapter.OnListInteractionListener {

    public static final String EXTRA_MESSAGE = "com.example.TodoList.ID";
    private static final String LIST_STATE = "list_state";
    private TodoViewModel todoViewModel;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private TodoItemAdapter adapter;
    private boolean fabPressed = false;
    private final RecyclerView.OnScrollListener hideFab = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                floatingActionButton.hide();
            } else {
                floatingActionButton.show();
            }
        }
    };
    private final View.OnClickListener viewTodo = (View v) -> {
        Intent intent = new Intent(MainActivity.this, ViewTodoActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        floatingActionButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        floatingActionButton.setOnClickListener(viewTodo);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(hideFab);
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));

        adapter = new TodoItemAdapter(this);
        recyclerView.setAdapter(adapter);

        todoViewModel.refreshTodoItems();
        todoViewModel.getTodoItems().observe(this, items -> {
            if (items != null) {
                Collections.sort(items);
                adapter.submitList(items);
                // Restore scroll position on rotation
                if (savedInstanceState != null) {
                    Parcelable listState = savedInstanceState.getParcelable(LIST_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onBackPressed() {
        if (adapter.isMultiSelect()) {
            onMultiSelectFinish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(TodoItem item) {
        Intent intent = new Intent(this, ViewTodoActivity.class);
        intent.putExtra(EXTRA_MESSAGE, item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(TodoItem item) {
        recyclerView.removeOnScrollListener(hideFab);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_done_all_white_24dp));
        floatingActionButton.setOnClickListener((View v) -> {
            fabPressed = true;
            removeSelectedItems();
            onMultiSelectFinish();
        });
        floatingActionButton.show();
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.mark_done));
    }

    @Override
    public void onMultiSelectFinish() {
        recyclerView.addOnScrollListener(hideFab);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_white_24px));
        floatingActionButton.setOnClickListener(viewTodo);
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));
        adapter.setMultiSelect(false);

        // If the user marks the TodoItems as done, we can refresh our list and let the
        // adapter handle the change without flickering. Otherwise manually notify the adapter of
        // the change to remove selection highlight.
        if (fabPressed) {
            todoViewModel.refreshTodoItems();
        } else {
            for (int position : adapter.getSelectedItemPositions()) {
                adapter.notifyItemChanged(position);
            }
        }
        fabPressed = false;
        adapter.getSelectedItemPositions().clear();
    }

    public void removeSelectedItems() {
        for (int position : adapter.getSelectedItemPositions()) {
            todoViewModel.deleteTodoItem(adapter.getItemAtPosition(position));
        }
    }
}
